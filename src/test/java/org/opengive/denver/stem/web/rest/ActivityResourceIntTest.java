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
import org.opengive.denver.stem.domain.Activity;
import org.opengive.denver.stem.repository.ActivityRepository;
import org.opengive.denver.stem.repository.search.ActivitySearchRepository;
import org.opengive.denver.stem.service.ActivityService;
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
 * Test class for the ActivityResource REST controller.
 *
 * @see ActivityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class ActivityResourceIntTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ActivitySearchRepository activitySearchRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restActivityMockMvc;

	private Activity activity;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final ActivityResource activityResource = new ActivityResource(activityService);
		restActivityMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
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
	public static Activity createEntity(final EntityManager em) {
		final Activity activity = new Activity()
				.name(DEFAULT_NAME);
		return activity;
	}

	@Before
	public void initTest() {
		activitySearchRepository.deleteAll();
		activity = createEntity(em);
	}

	@Test
	@Transactional
	public void createActivity() throws Exception {
		final int databaseSizeBeforeCreate = activityRepository.findAll().size();

		// Create the Activity
		restActivityMockMvc.perform(post("/api/activities")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(activity)))
		.andExpect(status().isCreated());

		// Validate the Activity in the database
		final List<Activity> activityList = activityRepository.findAll();
		assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
		final Activity testActivity = activityList.get(activityList.size() - 1);
		assertThat(testActivity.getName()).isEqualTo(DEFAULT_NAME);

		// Validate the Activity in Elasticsearch
		final Activity activityEs = activitySearchRepository.findOne(testActivity.getId());
		assertThat(activityEs).isEqualToComparingFieldByField(testActivity);
	}

	@Test
	@Transactional
	public void createActivityWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = activityRepository.findAll().size();

		// Create the Activity with an existing ID
		activity.setId(1L);

		// An entity with an existing ID cannot be created, so this API call must fail
		restActivityMockMvc.perform(post("/api/activities")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(activity)))
		.andExpect(status().isBadRequest());

		// Validate the Alice in the database
		final List<Activity> activityList = activityRepository.findAll();
		assertThat(activityList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		final int databaseSizeBeforeTest = activityRepository.findAll().size();
		// set the field null
		activity.setName(null);

		// Create the Activity, which fails.

		restActivityMockMvc.perform(post("/api/activities")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(activity)))
		.andExpect(status().isBadRequest());

		final List<Activity> activityList = activityRepository.findAll();
		assertThat(activityList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllActivitys() throws Exception {
		// Initialize the database
		activityRepository.saveAndFlush(activity);

		// Get all the activityList
		restActivityMockMvc.perform(get("/api/activities?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
	}

	@Test
	@Transactional
	public void getActivity() throws Exception {
		// Initialize the database
		activityRepository.saveAndFlush(activity);

		// Get the activity
		restActivityMockMvc.perform(get("/api/activities/{id}", activity.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.id").value(activity.getId().intValue()))
		.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingActivity() throws Exception {
		// Get the activity
		restActivityMockMvc.perform(get("/api/activities/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateActivity() throws Exception {
		// Initialize the database
		activityService.save(activity);

		final int databaseSizeBeforeUpdate = activityRepository.findAll().size();

		// Update the activity
		final Activity updatedActivity = activityRepository.findOne(activity.getId());
		updatedActivity
		.name(UPDATED_NAME);

		restActivityMockMvc.perform(put("/api/activities")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedActivity)))
		.andExpect(status().isOk());

		// Validate the Activity in the database
		final List<Activity> activityList = activityRepository.findAll();
		assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
		final Activity testActivity = activityList.get(activityList.size() - 1);
		assertThat(testActivity.getName()).isEqualTo(UPDATED_NAME);

		// Validate the Activity in Elasticsearch
		final Activity activityEs = activitySearchRepository.findOne(testActivity.getId());
		assertThat(activityEs).isEqualToComparingFieldByField(testActivity);
	}

	@Test
	@Transactional
	public void updateNonExistingActivity() throws Exception {
		final int databaseSizeBeforeUpdate = activityRepository.findAll().size();

		// Create the Activity

		// If the entity doesn't have an ID, it will be created instead of just being updated
		restActivityMockMvc.perform(put("/api/activities")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(activity)))
		.andExpect(status().isCreated());

		// Validate the Activity in the database
		final List<Activity> activityList = activityRepository.findAll();
		assertThat(activityList).hasSize(databaseSizeBeforeUpdate + 1);
	}

	@Test
	@Transactional
	public void deleteActivity() throws Exception {
		// Initialize the database
		activityService.save(activity);

		final int databaseSizeBeforeDelete = activityRepository.findAll().size();

		// Get the activity
		restActivityMockMvc.perform(delete("/api/activities/{id}", activity.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate Elasticsearch is empty
		final boolean activityExistsInEs = activitySearchRepository.exists(activity.getId());
		assertThat(activityExistsInEs).isFalse();

		// Validate the database is empty
		final List<Activity> activityList = activityRepository.findAll();
		assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchActivity() throws Exception {
		// Initialize the database
		activityService.save(activity);

		// Search the activity
		restActivityMockMvc.perform(get("/api/_search/activities?query=id:" + activity.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Activity.class);
	}
}
