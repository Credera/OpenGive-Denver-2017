package org.opengive.denver.stem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.opengive.denver.stem.OpenGiveApplication;
import org.opengive.denver.stem.domain.Program;
import org.opengive.denver.stem.domain.Session;
import org.opengive.denver.stem.repository.ProgramRepository;
import org.opengive.denver.stem.repository.search.ProgramSearchRepository;
import org.opengive.denver.stem.service.ProgramService;
import org.opengive.denver.stem.web.rest.errors.ExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the ProgramResource REST controller.
 *
 * @see ProgramResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class ProgramResourceIntTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
	private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

	private static final Boolean DEFAULT_ACTIVE = false;
	private static final Boolean UPDATED_ACTIVE = true;

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	private ProgramService programService;

	@Autowired
	private ProgramSearchRepository programSearchRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restProgramMockMvc;

	private Program program;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final ProgramResource programResource = new ProgramResource(programService);
		restProgramMockMvc = MockMvcBuilders.standaloneSetup(programResource)
				.setCustomArgumentResolvers(pageableArgumentResolver)
				.setControllerAdvice(exceptionTranslator)
				.setMessageConverters(jacksonMessageConverter).build();
	}

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it,
	 * if they test an entity which requires the current entity.
	 */
	public static Program createEntity(final EntityManager em) {
		final Program program = new Program()
				.name(DEFAULT_NAME)
				.description(DEFAULT_DESCRIPTION)
				.active(DEFAULT_ACTIVE);

		final Session session = SessionResourceIntTest.createEntity(em);
		em.persist(session);
		em.flush();
		program.setSession(session);

		return program;
	}

	@Before
	public void initTest() {
		programSearchRepository.deleteAll();
		program = createEntity(em);
	}

	@Test
	@Transactional
	public void createProgram() throws Exception {
		final int databaseSizeBeforeCreate = programRepository.findAll().size();

		// Create the Program
		restProgramMockMvc.perform(post("/api/programs")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(program)))
		.andExpect(status().isCreated());

		// Validate the Program in the database
		final List<Program> programList = programRepository.findAll();
		assertThat(programList).hasSize(databaseSizeBeforeCreate + 1);
		final Program testProgram = programList.get(programList.size() - 1);
		assertThat(testProgram.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testProgram.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
		assertThat(testProgram.isActive()).isEqualTo(DEFAULT_ACTIVE);

		// Validate the Program in Elasticsearch
		final Program programEs = programSearchRepository.findOne(testProgram.getId());
		assertThat(programEs).isEqualToComparingFieldByField(testProgram);
	}

	@Test
	@Transactional
	public void createProgramWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = programRepository.findAll().size();

		// Create the Program with an existing ID
		program.setId(1L);

		// An entity with an existing ID cannot be created, so this API call must fail
		restProgramMockMvc.perform(post("/api/programs")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(program)))
		.andExpect(status().isBadRequest());

		// Validate the Alice in the database
		final List<Program> programList = programRepository.findAll();
		assertThat(programList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		final int databaseSizeBeforeTest = programRepository.findAll().size();
		// set the field null
		program.setName(null);

		// Create the Program, which fails.

		restProgramMockMvc.perform(post("/api/programs")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(program)))
		.andExpect(status().isBadRequest());

		final List<Program> programList = programRepository.findAll();
		assertThat(programList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllPrograms() throws Exception {
		// Initialize the database
		programRepository.saveAndFlush(program);

		// Get all the programList
		restProgramMockMvc.perform(get("/api/programs?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(program.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
		.andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
	}

	@Test
	@Transactional
	public void getProgram() throws Exception {
		// Initialize the database
		programRepository.saveAndFlush(program);

		// Get the program
		restProgramMockMvc.perform(get("/api/programs/{id}", program.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.id").value(program.getId().intValue()))
		.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
		.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
		.andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
	}

	@Test
	@Transactional
	public void getNonExistingProgram() throws Exception {
		// Get the program
		restProgramMockMvc.perform(get("/api/programs/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateProgram() throws Exception {
		// Initialize the database
		programService.save(program);

		final int databaseSizeBeforeUpdate = programRepository.findAll().size();

		// Update the program
		final Program updatedProgram = programRepository.findOne(program.getId());
		updatedProgram
		.name(UPDATED_NAME)
		.description(UPDATED_DESCRIPTION)
		.active(UPDATED_ACTIVE);

		restProgramMockMvc.perform(put("/api/programs")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedProgram)))
		.andExpect(status().isOk());

		// Validate the Program in the database
		final List<Program> programList = programRepository.findAll();
		assertThat(programList).hasSize(databaseSizeBeforeUpdate);
		final Program testProgram = programList.get(programList.size() - 1);
		assertThat(testProgram.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testProgram.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testProgram.isActive()).isEqualTo(UPDATED_ACTIVE);

		// Validate the Program in Elasticsearch
		final Program programEs = programSearchRepository.findOne(testProgram.getId());
		assertThat(programEs).isEqualToComparingFieldByField(testProgram);
	}

	@Test
	@Transactional
	public void updateNonExistingProgram() throws Exception {
		final int databaseSizeBeforeUpdate = programRepository.findAll().size();

		// Create the Program

		// If the entity doesn't have an ID, it will be created instead of just being updated
		restProgramMockMvc.perform(put("/api/programs")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(program)))
		.andExpect(status().isCreated());

		// Validate the Program in the database
		final List<Program> programList = programRepository.findAll();
		assertThat(programList).hasSize(databaseSizeBeforeUpdate + 1);
	}

	@Test
	@Transactional
	public void deleteProgram() throws Exception {
		// Initialize the database
		programService.save(program);

		final int databaseSizeBeforeDelete = programRepository.findAll().size();

		// Get the program
		restProgramMockMvc.perform(delete("/api/programs/{id}", program.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate Elasticsearch is empty
		final boolean programExistsInEs = programSearchRepository.exists(program.getId());
		assertThat(programExistsInEs).isFalse();

		// Validate the database is empty
		final List<Program> programList = programRepository.findAll();
		assertThat(programList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchProgram() throws Exception {
		// Initialize the database
		programService.save(program);

		// Search the program
		restProgramMockMvc.perform(get("/api/_search/programs?query=id:" + program.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(program.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
		.andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Program.class);
	}
}
