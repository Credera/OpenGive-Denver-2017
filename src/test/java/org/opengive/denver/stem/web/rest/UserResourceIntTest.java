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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.opengive.denver.stem.OpenGiveApplication;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.UserRepository;
import org.opengive.denver.stem.repository.search.UserSearchRepository;
import org.opengive.denver.stem.service.MailService;
import org.opengive.denver.stem.service.UserService;
import org.opengive.denver.stem.web.rest.errors.ExceptionTranslator;
import org.opengive.denver.stem.web.rest.vm.ManagedUserVM;
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
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class UserResourceIntTest {

	private static final String DEFAULT_LOGIN = "johndoe";
	private static final String UPDATED_LOGIN = "jhipster";

	private static final String DEFAULT_PASSWORD = "passjohndoe";
	private static final String UPDATED_PASSWORD = "passjhipster";

	private static final String DEFAULT_EMAIL = "johndoe@localhost";
	private static final String UPDATED_EMAIL = "jhipster@localhost";

	private static final String DEFAULT_PHONE = "1234567890";
	private static final String UPDATED_PHONE = "2345678901";

	private static final String DEFAULT_FIRSTNAME = "john";
	private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

	private static final String DEFAULT_LASTNAME = "doe";
	private static final String UPDATED_LASTNAME = "jhipsterLastName";

	private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
	private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserSearchRepository userSearchRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private UserService userService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restUserMockMvc;

	private User user;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final UserResource userResource = new UserResource(userRepository, mailService, userService, userSearchRepository);
		restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
				.setCustomArgumentResolvers(pageableArgumentResolver)
				.setControllerAdvice(exceptionTranslator)
				.setMessageConverters(jacksonMessageConverter)
				.build();
	}

	/**
	 * Create a User.
	 *
	 * This is a static method, as tests for other entities might also need it,
	 * if they test an entity which has a required relationship to the User entity.
	 */
	public static User createEntity(final EntityManager em) {
		final User user = new User();
		user.setLogin(DEFAULT_LOGIN);
		user.setPassword(RandomStringUtils.random(60));
		user.setActivated(true);
		user.setEmail(DEFAULT_EMAIL);
		user.setFirstName(DEFAULT_FIRSTNAME);
		user.setLastName(DEFAULT_LASTNAME);
		user.setImageUrl(DEFAULT_IMAGEURL);
		return user;
	}

	@Before
	public void initTest() {
		user = createEntity(em);
	}

	@Test
	@Transactional
	public void createUser() throws Exception {
		final int databaseSizeBeforeCreate = userRepository.findAll().size();

		// Create the User
		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				null,
				DEFAULT_LOGIN,
				DEFAULT_PASSWORD,
				DEFAULT_FIRSTNAME,
				DEFAULT_LASTNAME,
				DEFAULT_EMAIL,
				DEFAULT_PHONE,
				null,
				true,
				DEFAULT_IMAGEURL,
				null,
				null,
				null,
				null,
				authorities,
                true);

		restUserMockMvc.perform(post("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isCreated());

		// Validate the User in the database
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
		final User testUser = userList.get(userList.size() - 1);
		assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
		assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
		assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
		assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
		assertThat(testUser.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
	}

	@Test
	@Transactional
	public void createUserWithExistingId() throws Exception {
		final int databaseSizeBeforeCreate = userRepository.findAll().size();

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				1L,
				DEFAULT_LOGIN,
				DEFAULT_PASSWORD,
				DEFAULT_FIRSTNAME,
				DEFAULT_LASTNAME,
				DEFAULT_EMAIL,
				DEFAULT_PHONE,
				null,
				true,
				DEFAULT_IMAGEURL,
				null,
				null,
				null,
				null,
				authorities,
                true);

		// An entity with an existing ID cannot be created, so this API call must fail
		restUserMockMvc.perform(post("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isBadRequest());

		// Validate the User in the database
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void createUserWithExistingLogin() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);
		final int databaseSizeBeforeCreate = userRepository.findAll().size();

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				null,
				DEFAULT_LOGIN, // this login should already be used
				DEFAULT_PASSWORD,
				DEFAULT_FIRSTNAME,
				DEFAULT_LASTNAME,
				"anothermail@localhost",
				DEFAULT_PHONE,
				null,
				true,
				DEFAULT_IMAGEURL,
				null,
				null,
				null,
				null,
				authorities,
                true);

		// Create the User
		restUserMockMvc.perform(post("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isBadRequest());

		// Validate the User in the database
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void createUserWithExistingEmail() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);
		final int databaseSizeBeforeCreate = userRepository.findAll().size();

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				null,
				"anotherlogin",
				DEFAULT_PASSWORD,
				DEFAULT_FIRSTNAME,
				DEFAULT_LASTNAME,
				DEFAULT_EMAIL, // this email should already be used
				DEFAULT_PHONE,
				null,
				true,
				DEFAULT_IMAGEURL,
				null,
				null,
				null,
				null,
				authorities,
                true);

		// Create the User
		restUserMockMvc.perform(post("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isBadRequest());

		// Validate the User in the database
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void getAllUsers() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);

		// Get all the users
		restUserMockMvc.perform(get("/api/users?sort=id,desc")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
		.andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
		.andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
		.andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
				.andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGEURL)));
	}

	@Test
	@Transactional
	public void getUser() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);

		// Get the user
		restUserMockMvc.perform(get("/api/users/{login}", user.getLogin()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.login").value(user.getLogin()))
		.andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
		.andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
		.andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
				.andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGEURL));
	}

	@Test
	@Transactional
	public void getNonExistingUser() throws Exception {
		restUserMockMvc.perform(get("/api/users/unknown"))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateUser() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);
		final int databaseSizeBeforeUpdate = userRepository.findAll().size();

		// Update the user
		final User updatedUser = userRepository.findOne(user.getId());

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				updatedUser.getId(),
				updatedUser.getLogin(),
				UPDATED_PASSWORD,
				UPDATED_FIRSTNAME,
				UPDATED_LASTNAME,
				UPDATED_EMAIL,
				UPDATED_PHONE,
				updatedUser.getAddress(),
				updatedUser.getActivated(),
				UPDATED_IMAGEURL,
				updatedUser.getCreatedBy(),
				updatedUser.getCreatedDate(),
				updatedUser.getLastModifiedBy(),
				updatedUser.getLastModifiedDate(),
				authorities,
                true);

		restUserMockMvc.perform(put("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isOk());

		// Validate the User in the database
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeUpdate);
		final User testUser = userList.get(userList.size() - 1);
		assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
		assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
		assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
		assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
	}

	@Test
	@Transactional
	public void updateUserLogin() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);
		final int databaseSizeBeforeUpdate = userRepository.findAll().size();

		// Update the user
		final User updatedUser = userRepository.findOne(user.getId());

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				updatedUser.getId(),
				UPDATED_LOGIN,
				UPDATED_PASSWORD,
				UPDATED_FIRSTNAME,
				UPDATED_LASTNAME,
				UPDATED_EMAIL,
				UPDATED_PHONE,
				updatedUser.getAddress(),
				updatedUser.getActivated(),
				UPDATED_IMAGEURL,
				updatedUser.getCreatedBy(),
				updatedUser.getCreatedDate(),
				updatedUser.getLastModifiedBy(),
				updatedUser.getLastModifiedDate(),
				authorities,
                true);

		restUserMockMvc.perform(put("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isOk());

		// Validate the User in the database
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeUpdate);
		final User testUser = userList.get(userList.size() - 1);
		assertThat(testUser.getLogin()).isEqualTo(UPDATED_LOGIN);
		assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
		assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
		assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
		assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
	}

	@Test
	@Transactional
	public void updateUserExistingEmail() throws Exception {
		// Initialize the database with 2 users
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);

		final User anotherUser = new User();
		anotherUser.setLogin("jhipster");
		anotherUser.setPassword(RandomStringUtils.random(60));
		anotherUser.setActivated(true);
		anotherUser.setEmail("jhipster@localhost");
		anotherUser.setFirstName("java");
		anotherUser.setLastName("hipster");
		anotherUser.setImageUrl("");
		userRepository.saveAndFlush(anotherUser);
		userSearchRepository.save(anotherUser);

		// Update the user
		final User updatedUser = userRepository.findOne(user.getId());

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				updatedUser.getId(),
				updatedUser.getLogin(),
				updatedUser.getPassword(),
				updatedUser.getFirstName(),
				updatedUser.getLastName(),
				"jhipster@localhost",  // this email should already be used by anotherUser
				updatedUser.getPhoneNumber(),
				updatedUser.getAddress(),
				updatedUser.getActivated(),
				updatedUser.getImageUrl(),
				updatedUser.getCreatedBy(),
				updatedUser.getCreatedDate(),
				updatedUser.getLastModifiedBy(),
				updatedUser.getLastModifiedDate(),
				authorities,
                true);

		restUserMockMvc.perform(put("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	public void updateUserExistingLogin() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);

		final User anotherUser = new User();
		anotherUser.setLogin("jhipster");
		anotherUser.setPassword(RandomStringUtils.random(60));
		anotherUser.setActivated(true);
		anotherUser.setEmail("jhipster@localhost");
		anotherUser.setFirstName("java");
		anotherUser.setLastName("hipster");
		anotherUser.setImageUrl("");
		userRepository.saveAndFlush(anotherUser);
		userSearchRepository.save(anotherUser);

		// Update the user
		final User updatedUser = userRepository.findOne(user.getId());

		final Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		final ManagedUserVM managedUserVM = new ManagedUserVM(
				updatedUser.getId(),
				"jhipster", // this login should already be used by anotherUser
				updatedUser.getPassword(),
				updatedUser.getFirstName(),
				updatedUser.getLastName(),
				updatedUser.getEmail(),
				updatedUser.getPhoneNumber(),
				updatedUser.getAddress(),
				updatedUser.getActivated(),
				updatedUser.getImageUrl(),
				updatedUser.getCreatedBy(),
				updatedUser.getCreatedDate(),
				updatedUser.getLastModifiedBy(),
				updatedUser.getLastModifiedDate(),
				authorities,
                true);

		restUserMockMvc.perform(put("/api/users")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
		.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	public void deleteUser() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		userSearchRepository.save(user);
		final int databaseSizeBeforeDelete = userRepository.findAll().size();

		// Delete the user
		restUserMockMvc.perform(delete("/api/users/{login}", user.getLogin())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate the database is empty
		final List<User> userList = userRepository.findAll();
		assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		final User userA = new User();
		userA.setLogin("AAA");
		final User userB = new User();
		userB.setLogin("BBB");
		assertThat(userA).isNotEqualTo(userB);
	}
}
