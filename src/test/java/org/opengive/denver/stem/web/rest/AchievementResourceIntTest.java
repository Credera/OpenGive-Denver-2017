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
import org.opengive.denver.stem.domain.Achievement;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.AchievementRepository;
import org.opengive.denver.stem.repository.search.AchievementSearchRepository;
import org.opengive.denver.stem.service.AchievementService;
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
 * Test class for the AchievementResource REST controller.
 *
 * @see AchievementResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class AchievementResourceIntTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
	private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

	private static final String DEFAULT_BADGE_URL = "AAAAAAAAAA";
	private static final String UPDATED_BADGE_URL = "BBBBBBBBBB";

	@Autowired
	private AchievementRepository achievementRepository;

	@Autowired
	private AchievementService achievementService;

	@Autowired
	private AchievementSearchRepository achievementSearchRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restAchievementMockMvc;

	private Achievement achievement;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final AchievementResource achievementResource = new AchievementResource(achievementService);
		restAchievementMockMvc = MockMvcBuilders.standaloneSetup(achievementResource)
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
	public static Achievement createEntity(final EntityManager em) {
		final Achievement achievement = new Achievement()
				.name(DEFAULT_NAME)
				.description(DEFAULT_DESCRIPTION)
				.badgeUrl(DEFAULT_BADGE_URL);

		final User student = UserResourceIntTest.createEntity(em);
		em.persist(student);
		em.flush();
		achievement.setAchievedBy(student);

		return achievement;
	}

	@Before
	public void initTest() {
		achievementSearchRepository.deleteAll();
		achievement = createEntity(em);
	}

	@Test
	@Transactional
	public void createAchievement() throws Exception {
		final int databaseSizeBeforeCreate = achievementRepository.findAll().size();

		// Create the Achievement
		restAchievementMockMvc.perform(post("/api/achievements")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(achievement)))
		.andExpect(status().isCreated());

		// Validate the Achievement in the database
		final List<Achievement> achievementList = achievementRepository.findAll();
		assertThat(achievementList).hasSize(databaseSizeBeforeCreate + 1);
		final Achievement testAchievement = achievementList.get(achievementList.size() - 1);
		assertThat(testAchievement.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testAchievement.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
		assertThat(testAchievement.getBadgeUrl()).isEqualTo(DEFAULT_BADGE_URL);

		// Validate the Achievement in Elasticsearch
		final Achievement achievementEs = achievementSearchRepository.findOne(testAchievement.getId());
		assertThat(achievementEs).isEqualToComparingFieldByField(testAchievement);
	}

	@Test
	@Transactional
	public void createAchievementWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = achievementRepository.findAll().size();

		// Create the Achievement with an existing ID
		achievement.setId(1L);

		// An entity with an existing ID cannot be created, so this API call must fail
		restAchievementMockMvc.perform(post("/api/achievements")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(achievement)))
		.andExpect(status().isBadRequest());

		// Validate the Alice in the database
		final List<Achievement> achievementList = achievementRepository.findAll();
		assertThat(achievementList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		final int databaseSizeBeforeTest = achievementRepository.findAll().size();
		// set the field null
		achievement.setName(null);

		// Create the Achievement, which fails.

		restAchievementMockMvc.perform(post("/api/achievements")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(achievement)))
		.andExpect(status().isBadRequest());

		final List<Achievement> achievementList = achievementRepository.findAll();
		assertThat(achievementList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllAchievements() throws Exception {
		// Initialize the database
		achievementRepository.saveAndFlush(achievement);

		// Get all the achievementList
		restAchievementMockMvc.perform(get("/api/achievements?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(achievement.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
		.andExpect(jsonPath("$.[*].badgeUrl").value(hasItem(DEFAULT_BADGE_URL.toString())));
	}

	@Test
	@Transactional
	public void getAchievement() throws Exception {
		// Initialize the database
		achievementRepository.saveAndFlush(achievement);

		// Get the achievement
		restAchievementMockMvc.perform(get("/api/achievements/{id}", achievement.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.id").value(achievement.getId().intValue()))
		.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
		.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
		.andExpect(jsonPath("$.badgeUrl").value(DEFAULT_BADGE_URL.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingAchievement() throws Exception {
		// Get the achievement
		restAchievementMockMvc.perform(get("/api/achievements/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateAchievement() throws Exception {
		// Initialize the database
		achievementService.save(achievement);

		final int databaseSizeBeforeUpdate = achievementRepository.findAll().size();

		// Update the achievement
		final Achievement updatedAchievement = achievementRepository.findOne(achievement.getId());
		updatedAchievement
		.name(UPDATED_NAME)
		.description(UPDATED_DESCRIPTION)
		.badgeUrl(UPDATED_BADGE_URL);

		restAchievementMockMvc.perform(put("/api/achievements")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedAchievement)))
		.andExpect(status().isOk());

		// Validate the Achievement in the database
		final List<Achievement> achievementList = achievementRepository.findAll();
		assertThat(achievementList).hasSize(databaseSizeBeforeUpdate);
		final Achievement testAchievement = achievementList.get(achievementList.size() - 1);
		assertThat(testAchievement.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testAchievement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testAchievement.getBadgeUrl()).isEqualTo(UPDATED_BADGE_URL);

		// Validate the Achievement in Elasticsearch
		final Achievement achievementEs = achievementSearchRepository.findOne(testAchievement.getId());
		assertThat(achievementEs).isEqualToComparingFieldByField(testAchievement);
	}

	@Test
	@Transactional
	public void updateNonExistingAchievement() throws Exception {
		final int databaseSizeBeforeUpdate = achievementRepository.findAll().size();

		// Create the Achievement

		// If the entity doesn't have an ID, it will be created instead of just being updated
		restAchievementMockMvc.perform(put("/api/achievements")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(achievement)))
		.andExpect(status().isCreated());

		// Validate the Achievement in the database
		final List<Achievement> achievementList = achievementRepository.findAll();
		assertThat(achievementList).hasSize(databaseSizeBeforeUpdate + 1);
	}

	@Test
	@Transactional
	public void deleteAchievement() throws Exception {
		// Initialize the database
		achievementService.save(achievement);

		final int databaseSizeBeforeDelete = achievementRepository.findAll().size();

		// Get the achievement
		restAchievementMockMvc.perform(delete("/api/achievements/{id}", achievement.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate Elasticsearch is empty
		final boolean achievementExistsInEs = achievementSearchRepository.exists(achievement.getId());
		assertThat(achievementExistsInEs).isFalse();

		// Validate the database is empty
		final List<Achievement> achievementList = achievementRepository.findAll();
		assertThat(achievementList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchAchievement() throws Exception {
		// Initialize the database
		achievementService.save(achievement);

		// Search the achievement
		restAchievementMockMvc.perform(get("/api/_search/achievements?query=id:" + achievement.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(achievement.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
		.andExpect(jsonPath("$.[*].badgeUrl").value(hasItem(DEFAULT_BADGE_URL.toString())));
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Achievement.class);
	}
}
