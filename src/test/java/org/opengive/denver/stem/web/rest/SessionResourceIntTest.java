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
import org.opengive.denver.stem.domain.Organization;
import org.opengive.denver.stem.domain.Session;
import org.opengive.denver.stem.repository.SessionRepository;
import org.opengive.denver.stem.repository.search.SessionSearchRepository;
import org.opengive.denver.stem.service.SessionService;
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
 * Test class for the SessionResource REST controller.
 *
 * @see SessionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class SessionResourceIntTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final Boolean DEFAULT_ACTIVE = false;
	private static final Boolean UPDATED_ACTIVE = true;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private SessionSearchRepository sessionSearchRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restSessionMockMvc;

	private Session session;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final SessionResource sessionResource = new SessionResource(sessionService);
		restSessionMockMvc = MockMvcBuilders.standaloneSetup(sessionResource)
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
	public static Session createEntity(final EntityManager em) {
		final Session session = new Session()
				.name(DEFAULT_NAME)
				.active(DEFAULT_ACTIVE);
		// Add required entity
		final Organization organization = OrganizationResourceIntTest.createEntity(em);
		em.persist(organization);
		em.flush();
		session.setOrganization(organization);
		return session;
	}

	@Before
	public void initTest() {
		sessionSearchRepository.deleteAll();
		session = createEntity(em);
	}

	@Test
	@Transactional
	public void createSession() throws Exception {
		final int databaseSizeBeforeCreate = sessionRepository.findAll().size();

		// Create the Session
		restSessionMockMvc.perform(post("/api/sessions")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(session)))
		.andExpect(status().isCreated());

		// Validate the Session in the database
		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeCreate + 1);
		final Session testSession = sessionList.get(sessionList.size() - 1);
		assertThat(testSession.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testSession.isActive()).isEqualTo(DEFAULT_ACTIVE);

		// Validate the Session in Elasticsearch
		final Session sessionEs = sessionSearchRepository.findOne(testSession.getId());
		assertThat(sessionEs).isEqualToComparingFieldByField(testSession);
	}

	@Test
	@Transactional
	public void createSessionWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = sessionRepository.findAll().size();

		// Create the Session with an existing ID
		session.setId(1L);

		// An entity with an existing ID cannot be created, so this API call must fail
		restSessionMockMvc.perform(post("/api/sessions")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(session)))
		.andExpect(status().isBadRequest());

		// Validate the Alice in the database
		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		final int databaseSizeBeforeTest = sessionRepository.findAll().size();
		// set the field null
		session.setName(null);

		// Create the Session, which fails.

		restSessionMockMvc.perform(post("/api/sessions")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(session)))
		.andExpect(status().isBadRequest());

		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkActiveIsRequired() throws Exception {
		final int databaseSizeBeforeTest = sessionRepository.findAll().size();
		// set the field null
		session.setActive(null);

		// Create the Session, which fails.

		restSessionMockMvc.perform(post("/api/sessions")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(session)))
		.andExpect(status().isBadRequest());

		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllSessions() throws Exception {
		// Initialize the database
		sessionRepository.saveAndFlush(session);

		// Get all the sessionList
		restSessionMockMvc.perform(get("/api/sessions?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(session.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
	}

	@Test
	@Transactional
	public void getSession() throws Exception {
		// Initialize the database
		sessionRepository.saveAndFlush(session);

		// Get the session
		restSessionMockMvc.perform(get("/api/sessions/{id}", session.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.id").value(session.getId().intValue()))
		.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
		.andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
	}

	@Test
	@Transactional
	public void getNonExistingSession() throws Exception {
		// Get the session
		restSessionMockMvc.perform(get("/api/sessions/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateSession() throws Exception {
		// Initialize the database
		sessionService.save(session);

		final int databaseSizeBeforeUpdate = sessionRepository.findAll().size();

		// Update the session
		final Session updatedSession = sessionRepository.findOne(session.getId());
		updatedSession
		.name(UPDATED_NAME)
		.active(UPDATED_ACTIVE);

		restSessionMockMvc.perform(put("/api/sessions")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedSession)))
		.andExpect(status().isOk());

		// Validate the Session in the database
		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
		final Session testSession = sessionList.get(sessionList.size() - 1);
		assertThat(testSession.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testSession.isActive()).isEqualTo(UPDATED_ACTIVE);

		// Validate the Session in Elasticsearch
		final Session sessionEs = sessionSearchRepository.findOne(testSession.getId());
		assertThat(sessionEs).isEqualToComparingFieldByField(testSession);
	}

	@Test
	@Transactional
	public void updateNonExistingSession() throws Exception {
		final int databaseSizeBeforeUpdate = sessionRepository.findAll().size();

		// Create the Session

		// If the entity doesn't have an ID, it will be created instead of just being updated
		restSessionMockMvc.perform(put("/api/sessions")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(session)))
		.andExpect(status().isCreated());

		// Validate the Session in the database
		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeUpdate + 1);
	}

	@Test
	@Transactional
	public void deleteSession() throws Exception {
		// Initialize the database
		sessionService.save(session);

		final int databaseSizeBeforeDelete = sessionRepository.findAll().size();

		// Get the session
		restSessionMockMvc.perform(delete("/api/sessions/{id}", session.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate Elasticsearch is empty
		final boolean sessionExistsInEs = sessionSearchRepository.exists(session.getId());
		assertThat(sessionExistsInEs).isFalse();

		// Validate the database is empty
		final List<Session> sessionList = sessionRepository.findAll();
		assertThat(sessionList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchSession() throws Exception {
		// Initialize the database
		sessionService.save(session);

		// Search the session
		restSessionMockMvc.perform(get("/api/_search/sessions?query=id:" + session.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(session.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Session.class);
	}
}
