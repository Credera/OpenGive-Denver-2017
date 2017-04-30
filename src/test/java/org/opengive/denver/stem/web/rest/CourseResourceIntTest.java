package org.opengive.denver.stem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.opengive.denver.stem.web.rest.TestUtil.sameInstant;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.opengive.denver.stem.OpenGiveApplication;
import org.opengive.denver.stem.domain.Course;
import org.opengive.denver.stem.domain.Organization;
import org.opengive.denver.stem.domain.Program;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.CourseRepository;
import org.opengive.denver.stem.repository.search.CourseSearchRepository;
import org.opengive.denver.stem.service.CourseService;
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
 * Test class for the CourseResource REST controller.
 *
 * @see CourseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class CourseResourceIntTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
	private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

	private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
	private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

	private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
	private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseSearchRepository courseSearchRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restCourseMockMvc;

	private Course course;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final CourseResource courseResource = new CourseResource(courseService);
		restCourseMockMvc = MockMvcBuilders.standaloneSetup(courseResource)
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
	public static Course createEntity(final EntityManager em) {

		final Course course = new Course()
				.name(DEFAULT_NAME)
				.description(DEFAULT_DESCRIPTION)
				.startDate(DEFAULT_START_DATE)
				.endDate(DEFAULT_END_DATE);

		// Add required entity
		final User instructor = UserResourceIntTest.createEntity(em);
		em.persist(instructor);
		em.flush();
		course.setInstructor(instructor);

		final Program program = ProgramResourceIntTest.createEntity(em);
		em.persist(program);
		em.flush();
		course.setProgram(program);

		final Organization org = OrganizationResourceIntTest.createEntity(em);
		em.persist(org);
		em.flush();
		course.setOrganization(org);

		return course;
	}

	@Before
	public void initTest() {
		courseSearchRepository.deleteAll();
		course = createEntity(em);
	}

	@Test
	@Transactional
	public void createCourse() throws Exception {
		final int databaseSizeBeforeCreate = courseRepository.findAll().size();

		// Create the Course
		restCourseMockMvc.perform(post("/api/courses")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(course)))
		.andExpect(status().isCreated());

		// Validate the Course in the database
		final List<Course> courseList = courseRepository.findAll();
		assertThat(courseList).hasSize(databaseSizeBeforeCreate + 1);
		final Course testCourse = courseList.get(courseList.size() - 1);
		assertThat(testCourse.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testCourse.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
		assertThat(testCourse.getStartDate()).isEqualTo(DEFAULT_START_DATE);
		assertThat(testCourse.getEndDate()).isEqualTo(DEFAULT_END_DATE);

		// Validate the Course in Elasticsearch
		final Course courseEs = courseSearchRepository.findOne(testCourse.getId());
		assertThat(courseEs).isEqualToComparingFieldByField(testCourse);
	}

	@Test
	@Transactional
	public void createCourseWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = courseRepository.findAll().size();

		// Create the Course with an existing ID
		course.setId(1L);

		// An entity with an existing ID cannot be created, so this API call must fail
		restCourseMockMvc.perform(post("/api/courses")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(course)))
		.andExpect(status().isBadRequest());

		// Validate the Alice in the database
		final List<Course> courseList = courseRepository.findAll();
		assertThat(courseList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		final int databaseSizeBeforeTest = courseRepository.findAll().size();
		// set the field null
		course.setName(null);

		// Create the Course, which fails.

		restCourseMockMvc.perform(post("/api/courses")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(course)))
		.andExpect(status().isBadRequest());

		final List<Course> courseList = courseRepository.findAll();
		assertThat(courseList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllCourses() throws Exception {
		// Initialize the database
		courseRepository.saveAndFlush(course);

		// Get all the courseList
		restCourseMockMvc.perform(get("/api/courses?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
		.andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
		.andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
	}

	@Test
	@Transactional
	public void getCourse() throws Exception {
		// Initialize the database
		courseRepository.saveAndFlush(course);

		// Get the course
		restCourseMockMvc.perform(get("/api/courses/{id}", course.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.id").value(course.getId().intValue()))
		.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
		.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
		.andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
		.andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)));
	}

	@Test
	@Transactional
	public void getNonExistingCourse() throws Exception {
		// Get the course
		restCourseMockMvc.perform(get("/api/courses/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateCourse() throws Exception {
		// Initialize the database
		courseService.save(course);

		final int databaseSizeBeforeUpdate = courseRepository.findAll().size();

		// Update the course
		final Course updatedCourse = courseRepository.findOne(course.getId());
		updatedCourse
		.name(UPDATED_NAME)
		.description(UPDATED_DESCRIPTION)
		.startDate(UPDATED_START_DATE)
		.endDate(UPDATED_END_DATE);

		restCourseMockMvc.perform(put("/api/courses")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updatedCourse)))
		.andExpect(status().isOk());

		// Validate the Course in the database
		final List<Course> courseList = courseRepository.findAll();
		assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
		final Course testCourse = courseList.get(courseList.size() - 1);
		assertThat(testCourse.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testCourse.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testCourse.getStartDate()).isEqualTo(UPDATED_START_DATE);
		assertThat(testCourse.getEndDate()).isEqualTo(UPDATED_END_DATE);

		// Validate the Course in Elasticsearch
		final Course courseEs = courseSearchRepository.findOne(testCourse.getId());
		assertThat(courseEs).isEqualToComparingFieldByField(testCourse);
	}

	@Test
	@Transactional
	public void updateNonExistingCourse() throws Exception {
		final int databaseSizeBeforeUpdate = courseRepository.findAll().size();

		// Create the Course

		// If the entity doesn't have an ID, it will be created instead of just being updated
		restCourseMockMvc.perform(put("/api/courses")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(course)))
		.andExpect(status().isCreated());

		// Validate the Course in the database
		final List<Course> courseList = courseRepository.findAll();
		assertThat(courseList).hasSize(databaseSizeBeforeUpdate + 1);
	}

	@Test
	@Transactional
	public void deleteCourse() throws Exception {
		// Initialize the database
		courseService.save(course);

		final int databaseSizeBeforeDelete = courseRepository.findAll().size();

		// Get the course
		restCourseMockMvc.perform(delete("/api/courses/{id}", course.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate Elasticsearch is empty
		final boolean courseExistsInEs = courseSearchRepository.exists(course.getId());
		assertThat(courseExistsInEs).isFalse();

		// Validate the database is empty
		final List<Course> courseList = courseRepository.findAll();
		assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchCourse() throws Exception {
		// Initialize the database
		courseService.save(course);

		// Search the course
		restCourseMockMvc.perform(get("/api/_search/courses?query=id:" + course.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
		.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
		.andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
		.andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Course.class);
	}
}
