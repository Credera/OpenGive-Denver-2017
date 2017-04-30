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
import org.opengive.denver.stem.domain.School;
import org.opengive.denver.stem.repository.SchoolRepository;
import org.opengive.denver.stem.repository.search.SchoolSearchRepository;
import org.opengive.denver.stem.service.SchoolService;
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
 * Test class for the SchoolResource REST controller.
 *
 * @see SchoolResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class SchoolResourceIntTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final String DEFAULT_DISTRICT = "AAAAAAAAAA";
	private static final String UPDATED_DISTRICT = "BBBBBBBBBB";

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private SchoolService schoolService;

	@Autowired
	private SchoolSearchRepository schoolSearchRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restSchoolMockMvc;

	private School school;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final SchoolResource schoolResource = new SchoolResource(schoolService);
		restSchoolMockMvc = MockMvcBuilders.standaloneSetup(schoolResource)
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
	public static School createEntity(final EntityManager em) {
		final School school = new School()
				.name(DEFAULT_NAME)
				.district(DEFAULT_DISTRICT);
		return school;
	}

	@Before
	public void initTest() {
		schoolSearchRepository.deleteAll();
		school = createEntity(em);
	}

	@Test
	@Transactional
	public void createSchool() throws Exception {
		final int databaseSizeBeforeCreate = schoolRepository.findAll().size();

		// Create the School
		restSchoolMockMvc.perform(post("/api/schools")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(school)))
		.andExpect(status().isCreated());

		// Validate the School in the database
		final List<School> schoolList = schoolRepository.findAll();
		assertThat(schoolList).hasSize(databaseSizeBeforeCreate + 1);
		final School testSchool = schoolList.get(schoolList.size() - 1);
		assertThat(testSchool.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testSchool.getDistrict()).isEqualTo(DEFAULT_DISTRICT);

		// Validate the School in Elasticsearch
		final School schoolEs = schoolSearchRepository.findOne(testSchool.getId());
		assertThat(schoolEs).isEqualToComparingFieldByField(testSchool);
	}

	@Test
	@Transactional
	public void createSchoolWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = schoolRepository.findAll().size();

		// Create the School with an existing ID
		school.setId(1L);

		// An entity with an existing ID cannot be created, so this API call must fail
		restSchoolMockMvc.perform(post("/api/schools")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(school)))
		.andExpect(status().isBadRequest());

		// Validate the Alice in the database
		final List<School> schoolList = schoolRepository.findAll();
		assertThat(schoolList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		final int databaseSizeBeforeTest = schoolRepository.findAll().size();
		// set the field null
		school.setName(null);

		// Create the School, which fails.

		restSchoolMockMvc.perform(post("/api/schools")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(school)))
		.andExpect(status().isBadRequest());

		final List<School> schoolList = schoolRepository.findAll();
		assertThat(schoolList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllSchools() throws Exception {
		// Initialize the database
		schoolRepository.saveAndFlush(school);

		// Get all the schoolList
		restSchoolMockMvc.perform(get("/api/schools?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(school.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].district").value(hasItem(DEFAULT_DISTRICT.toString())));
	}

	@Test
	@Transactional
	public void getSchool() throws Exception {
		// Initialize the database
		schoolRepository.saveAndFlush(school);

		// Get the school
		restSchoolMockMvc.perform(get("/api/schools/{id}", school.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.id").value(school.getId().intValue()))
		.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
		.andExpect(jsonPath("$.district").value(DEFAULT_DISTRICT.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingSchool() throws Exception {
		// Get the school
		restSchoolMockMvc.perform(get("/api/schools/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateSchool() throws Exception {
		// Initialize the database
		schoolService.save(school);

		final int databaseSizeBeforeUpdate = schoolRepository.findAll().size();

		// Update the school
		final School updatedSchool = schoolRepository.findOne(school.getId());
		updatedSchool
		.name(UPDATED_NAME)
		.district(UPDATED_DISTRICT);

		restSchoolMockMvc.perform(put("/api/schools")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedSchool)))
		.andExpect(status().isOk());

		// Validate the School in the database
		final List<School> schoolList = schoolRepository.findAll();
		assertThat(schoolList).hasSize(databaseSizeBeforeUpdate);
		final School testSchool = schoolList.get(schoolList.size() - 1);
		assertThat(testSchool.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testSchool.getDistrict()).isEqualTo(UPDATED_DISTRICT);

		// Validate the School in Elasticsearch
		final School schoolEs = schoolSearchRepository.findOne(testSchool.getId());
		assertThat(schoolEs).isEqualToComparingFieldByField(testSchool);
	}

	@Test
	@Transactional
	public void updateNonExistingSchool() throws Exception {
		final int databaseSizeBeforeUpdate = schoolRepository.findAll().size();

		// Create the School

		// If the entity doesn't have an ID, it will be created instead of just being updated
		restSchoolMockMvc.perform(put("/api/schools")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(school)))
		.andExpect(status().isCreated());

		// Validate the School in the database
		final List<School> schoolList = schoolRepository.findAll();
		assertThat(schoolList).hasSize(databaseSizeBeforeUpdate + 1);
	}

	@Test
	@Transactional
	public void deleteSchool() throws Exception {
		// Initialize the database
		schoolService.save(school);

		final int databaseSizeBeforeDelete = schoolRepository.findAll().size();

		// Get the school
		restSchoolMockMvc.perform(delete("/api/schools/{id}", school.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate Elasticsearch is empty
		final boolean schoolExistsInEs = schoolSearchRepository.exists(school.getId());
		assertThat(schoolExistsInEs).isFalse();

		// Validate the database is empty
		final List<School> schoolList = schoolRepository.findAll();
		assertThat(schoolList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchSchool() throws Exception {
		// Initialize the database
		schoolService.save(school);

		// Search the school
		restSchoolMockMvc.perform(get("/api/_search/schools?query=id:" + school.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(school.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].district").value(hasItem(DEFAULT_DISTRICT.toString())));
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(School.class);
	}
}
