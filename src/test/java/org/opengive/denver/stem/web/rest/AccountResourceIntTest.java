package org.opengive.denver.stem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opengive.denver.stem.OpenGiveApplication;
import org.opengive.denver.stem.domain.Authority;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.AuthorityRepository;
import org.opengive.denver.stem.repository.UserRepository;
import org.opengive.denver.stem.security.AuthoritiesConstants;
import org.opengive.denver.stem.service.MailService;
import org.opengive.denver.stem.service.UserService;
import org.opengive.denver.stem.service.dto.UserDTO;
import org.opengive.denver.stem.web.rest.vm.ManagedUserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see AccountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
public class AccountResourceIntTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private UserService userService;

	@Mock
	private UserService mockUserService;

	@Mock
	private MailService mockMailService;

	private MockMvc restUserMockMvc;

	private MockMvc restMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		doNothing().when(mockMailService).sendActivationEmail(anyObject());

		final AccountResource accountResource =
				new AccountResource(userRepository, userService, mockMailService);

		final AccountResource accountUserMockResource =
				new AccountResource(userRepository, mockUserService, mockMailService);

		restMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
		restUserMockMvc = MockMvcBuilders.standaloneSetup(accountUserMockResource).build();
	}

	@Test
	public void testNonAuthenticatedUser() throws Exception {
		restUserMockMvc.perform(get("/api/authenticate")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(""));
	}

	@Test
	public void testAuthenticatedUser() throws Exception {
		restUserMockMvc.perform(get("/api/authenticate")
				.with(request -> {
					request.setRemoteUser("test");
					return request;
				})
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("test"));
	}

	@Test
	public void testGetExistingAccount() throws Exception {
		final Set<Authority> authorities = new HashSet<>();
		final Authority authority = new Authority();
		authority.setName(AuthoritiesConstants.ADMIN);
		authorities.add(authority);

		final User user = new User();
		user.setLogin("test");
		user.setFirstName("john");
		user.setLastName("doe");
		user.setEmail("john.doe@jhipster.com");
		user.setImageUrl("http://placehold.it/50x50");
		user.setAuthorities(authorities);
		when(mockUserService.getUserWithAuthorities()).thenReturn(user);

		restUserMockMvc.perform(get("/api/account")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.login").value("test"))
		.andExpect(jsonPath("$.firstName").value("john"))
		.andExpect(jsonPath("$.lastName").value("doe"))
		.andExpect(jsonPath("$.email").value("john.doe@jhipster.com"))
		.andExpect(jsonPath("$.imageUrl").value("http://placehold.it/50x50"))
		.andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
	}

	@Test
	public void testGetUnknownAccount() throws Exception {
		when(mockUserService.getUserWithAuthorities()).thenReturn(null);

		restUserMockMvc.perform(get("/api/account")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError());
	}

	@Test
	@Transactional
	public void testRegisterValid() throws Exception {
		final ManagedUserVM validUser = new ManagedUserVM(
				null,                   // id
				"joe",                  // login
				"password",             // password
				"Joe",                  // firstName
				"Shmoe",                // lastName
				"joe@example.com",      // email
				"1234567890",			// phone
				null,					// address
				true,                   // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		restMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(validUser)))
		.andExpect(status().isCreated());

		final Optional<User> user = userRepository.findOneByLogin("joe");
		assertThat(user.isPresent()).isTrue();
	}

	@Test
	@Transactional
	public void testRegisterInvalidLogin() throws Exception {
		final ManagedUserVM invalidUser = new ManagedUserVM(
				null,                   // id
				"funky-log!n",          // login <-- invalid
				"password",             // password
				"Funky",                // firstName
				"One",                  // lastName
				"funky@example.com",    // email
				"1234567890",			// phone
				null,					// address
				true,                   // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		restUserMockMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
		.andExpect(status().isBadRequest());

		final Optional<User> user = userRepository.findOneByEmail("funky@example.com");
		assertThat(user.isPresent()).isFalse();
	}

	@Test
	@Transactional
	public void testRegisterInvalidEmail() throws Exception {
		final ManagedUserVM invalidUser = new ManagedUserVM(
				null,               // id
				"bob",              // login
				"password",         // password
				"Bob",              // firstName
				"Green",            // lastName
				"invalid",          // email <-- invalid
				"1234567890",			// phone
				null,					// address
				true,               // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		restUserMockMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
		.andExpect(status().isBadRequest());

		final Optional<User> user = userRepository.findOneByLogin("bob");
		assertThat(user.isPresent()).isFalse();
	}

	@Test
	@Transactional
	public void testRegisterInvalidPassword() throws Exception {
		final ManagedUserVM invalidUser = new ManagedUserVM(
				null,               // id
				"bob",              // login
				"123",              // password with only 3 digits
				"Bob",              // firstName
				"Green",            // lastName
				"bob@example.com",  // email
				"1234567890",			// phone
				null,					// address
				true,               // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		restUserMockMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
		.andExpect(status().isBadRequest());

		final Optional<User> user = userRepository.findOneByLogin("bob");
		assertThat(user.isPresent()).isFalse();
	}

	@Test
	@Transactional
	public void testRegisterDuplicateLogin() throws Exception {
		// Good
		final ManagedUserVM validUser = new ManagedUserVM(
				null,                   // id
				"alice",                // login
				"password",             // password
				"Alice",                // firstName
				"Something",            // lastName
				"alice@example.com",    // email
				"1234567890",			// phone
				null,					// address
				true,                   // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		// Duplicate login, different email
		final ManagedUserVM duplicatedUser = new ManagedUserVM(validUser.getId(), validUser.getLogin(), validUser.getPassword(), validUser.getFirstName(), validUser.getLastName(),
				"alicejr@example.com", validUser.getPhoneNumber(), validUser.getAddress(), true,
				validUser.getImageUrl(), validUser.getCreatedBy(), validUser.getCreatedDate(),
				validUser.getLastModifiedBy(), validUser.getLastModifiedDate(), validUser.getAuthorities());

		// Good user
		restMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(validUser)))
		.andExpect(status().isCreated());

		// Duplicate login
		restMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
		.andExpect(status().is4xxClientError());

		final Optional<User> userDup = userRepository.findOneByEmail("alicejr@example.com");
		assertThat(userDup.isPresent()).isFalse();
	}

	@Test
	@Transactional
	public void testRegisterDuplicateEmail() throws Exception {
		// Good
		final ManagedUserVM validUser = new ManagedUserVM(
				null,                   // id
				"john",                 // login
				"password",             // password
				"John",                 // firstName
				"Doe",                  // lastName
				"john@example.com",     // email
				"1234567890",			// phone
				null,					// address
				true,                   // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		// Duplicate email, different login
		final ManagedUserVM duplicatedUser = new ManagedUserVM(validUser.getId(), "johnjr", validUser.getPassword(), validUser.getLogin(), validUser.getLastName(),
				validUser.getEmail(), validUser.getPhoneNumber(), validUser.getAddress(), true, validUser.getImageUrl(),
				validUser.getCreatedBy(), validUser.getCreatedDate(), validUser.getLastModifiedBy(),
				validUser.getLastModifiedDate(), validUser.getAuthorities());

		// Good user
		restMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(validUser)))
		.andExpect(status().isCreated());

		// Duplicate email
		restMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
		.andExpect(status().is4xxClientError());

		final Optional<User> userDup = userRepository.findOneByLogin("johnjr");
		assertThat(userDup.isPresent()).isFalse();
	}

	@Test
	@Transactional
	public void testRegisterAdminIsIgnored() throws Exception {
		final ManagedUserVM validUser = new ManagedUserVM(
				null,                   // id
				"badguy",               // login
				"password",             // password
				"Bad",                  // firstName
				"Guy",                  // lastName
				"badguy@example.com",   // email
				"1234567890",			// phone
				null,					// address
				true,                   // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.ADMIN)));

		restMvc.perform(
				post("/api/register")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(validUser)))
		.andExpect(status().isCreated());

		final Optional<User> userDup = userRepository.findOneByLogin("badguy");
		assertThat(userDup.isPresent()).isTrue();
		assertThat(userDup.get().getAuthorities()).hasSize(1)
		.containsExactly(authorityRepository.findOne(AuthoritiesConstants.USER));
	}

	@Test
	@Transactional
	public void testSaveInvalidLogin() throws Exception {
		final UserDTO invalidUser = new UserDTO(
				null,                   // id
				"funky-log!n",          // login <-- invalid
				"Funky",                // firstName
				"One",                  // lastName
				"funky@example.com",    // email
				"1234567890",			// phone
				null,					// address
				true,                   // activated
				"http://placehold.it/50x50", //imageUrl
				null,                   // createdBy
				null,                   // createdDate
				null,                   // lastModifiedBy
				null,                   // lastModifiedDate
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER))
				);

		restUserMockMvc.perform(
				post("/api/account")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
		.andExpect(status().isBadRequest());

		final Optional<User> user = userRepository.findOneByEmail("funky@example.com");
		assertThat(user.isPresent()).isFalse();
	}
}
