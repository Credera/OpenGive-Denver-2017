package org.opengive.denver.stem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
import org.opengive.denver.stem.repository.search.UserSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
@Transactional
public class SocialServiceIntTest {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserSearchRepository userSearchRepository;


	@Mock
	private MailService mockMailService;

	@Mock
	private UsersConnectionRepository mockUsersConnectionRepository;

	@Mock
	private ConnectionRepository mockConnectionRepository;

	private SocialService socialService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		doNothing().when(mockMailService).sendSocialRegistrationValidationEmail(anyObject(), anyString());
		doNothing().when(mockConnectionRepository).addConnection(anyObject());
		when(mockUsersConnectionRepository.createConnectionRepository(anyString())).thenReturn(mockConnectionRepository);

		socialService = new SocialService(mockUsersConnectionRepository, authorityRepository,
				passwordEncoder, userRepository, mockMailService, userSearchRepository);
	}

	@Test
	public void testDeleteUserSocialConnection() throws Exception {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");
		socialService.createSocialUser(connection);
		final MultiValueMap<String, Connection<?>> connectionsByProviderId = new LinkedMultiValueMap<>();
		connectionsByProviderId.put("PROVIDER", null);
		when(mockConnectionRepository.findAllConnections()).thenReturn(connectionsByProviderId);

		// Exercise
		socialService.deleteUserSocialConnection("@LOGIN");

		// Verify
		verify(mockConnectionRepository, times(1)).removeConnections("PROVIDER");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSocialUserShouldThrowExceptionIfConnectionIsNull() {
		// Exercise
		socialService.createSocialUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSocialUserShouldThrowExceptionIfConnectionHasNoEmailAndNoLogin() {
		// Setup
		final Connection<?> connection = createConnection("",
				"",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSocialUserShouldThrowExceptionIfConnectionHasNoEmailAndLoginAlreadyExist() {
		// Setup
		final User user = createExistingUser("@LOGIN",
				"mail@mail.com",
				"OTHER_FIRST_NAME",
				"OTHER_LAST_NAME",
				"OTHER_IMAGE_URL");
		final Connection<?> connection = createConnection("@LOGIN",
				"",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		try {
			// Exercise
			socialService.createSocialUser(connection);
		} finally {
			// Teardown
			userRepository.delete(user);
		}
	}

	@Test
	public void testCreateSocialUserShouldCreateUserIfNotExist() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		// Verify
		final Optional<User> user = userRepository.findOneByEmail("mail@mail.com");
		assertThat(user).isPresent();

		// Teardown
		userRepository.delete(user.get());
	}

	@Test
	public void testCreateSocialUserShouldCreateUserWithSocialInformation() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		final User user = userRepository.findOneByEmail("mail@mail.com").get();
		assertThat(user.getFirstName()).isEqualTo("FIRST_NAME");
		assertThat(user.getLastName()).isEqualTo("LAST_NAME");
		assertThat(user.getImageUrl()).isEqualTo("IMAGE_URL");

		// Teardown
		userRepository.delete(user);
	}

	@Test
	public void testCreateSocialUserShouldCreateActivatedUserWithRoleUserAndPassword() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		final User user = userRepository.findOneByEmail("mail@mail.com").get();
		assertThat(user.getActivated()).isEqualTo(true);
		assertThat(user.getPassword()).isNotEmpty();
		final Authority userAuthority = authorityRepository.findOne("ROLE_STUDENT");
		assertThat(user.getAuthorities().toArray()).containsExactly(userAuthority);

		// Teardown
		userRepository.delete(user);
	}

	@Test
	public void testCreateSocialUserShouldCreateUserWithLoginSameAsEmailIfNotTwitter() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER_OTHER_THAN_TWITTER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		final User user = userRepository.findOneByEmail("mail@mail.com").get();
		assertThat(user.getLogin()).isEqualTo("mail@mail.com");

		// Teardown
		userRepository.delete(user);
	}

	@Test
	public void testCreateSocialUserShouldCreateUserWithSocialLoginWhenIsTwitter() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"twitter");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		final User user = userRepository.findOneByEmail("mail@mail.com").get();
		assertThat(user.getLogin()).isEqualToIgnoringCase("@LOGIN");

		// Teardown
		userRepository.delete(user);
	}

	@Test
	public void testCreateSocialUserShouldCreateSocialConnection() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		verify(mockConnectionRepository, times(1)).addConnection(connection);

		// Teardown
		final User userToDelete = userRepository.findOneByEmail("mail@mail.com").get();
		userRepository.delete(userToDelete);
	}

	@Test
	public void testCreateSocialUserShouldNotCreateUserIfEmailAlreadyExist() {
		// Setup
		createExistingUser("@OTHER_LOGIN",
				"mail@mail.com",
				"OTHER_FIRST_NAME",
				"OTHER_LAST_NAME",
				"OTHER_IMAGE_URL");
		final long initialUserCount = userRepository.count();
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		assertThat(userRepository.count()).isEqualTo(initialUserCount);

		// Teardown
		final User userToDelete = userRepository.findOneByEmail("mail@mail.com").get();
		userRepository.delete(userToDelete);
	}

	@Test
	public void testCreateSocialUserShouldNotChangeUserIfEmailAlreadyExist() {
		// Setup
		createExistingUser("@OTHER_LOGIN",
				"mail@mail.com",
				"OTHER_FIRST_NAME",
				"OTHER_LAST_NAME",
				"OTHER_IMAGE_URL");
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		final User userToVerify = userRepository.findOneByEmail("mail@mail.com").get();
		assertThat(userToVerify.getLogin()).isEqualTo("@other_login");
		assertThat(userToVerify.getFirstName()).isEqualTo("OTHER_FIRST_NAME");
		assertThat(userToVerify.getLastName()).isEqualTo("OTHER_LAST_NAME");
		assertThat(userToVerify.getImageUrl()).isEqualTo("OTHER_IMAGE_URL");
		// Teardown
		userRepository.delete(userToVerify);
	}

	@Test
	public void testCreateSocialUserShouldSendRegistrationValidationEmail() {
		// Setup
		final Connection<?> connection = createConnection("@LOGIN",
				"mail@mail.com",
				"FIRST_NAME",
				"LAST_NAME",
				"IMAGE_URL",
				"PROVIDER");

		// Exercise
		socialService.createSocialUser(connection);

		//Verify
		verify(mockMailService, times(1)).sendSocialRegistrationValidationEmail(anyObject(), anyString());

		// Teardown
		final User userToDelete = userRepository.findOneByEmail("mail@mail.com").get();
		userRepository.delete(userToDelete);
	}

	private Connection<?> createConnection(final String login,
			final String email,
			final String firstName,
			final String lastName,
			final String imageUrl,
			final String providerId) {
		final UserProfile userProfile = mock(UserProfile.class);
		when(userProfile.getEmail()).thenReturn(email);
		when(userProfile.getUsername()).thenReturn(login);
		when(userProfile.getFirstName()).thenReturn(firstName);
		when(userProfile.getLastName()).thenReturn(lastName);

		final Connection<?> connection = mock(Connection.class);
		final ConnectionKey key = new ConnectionKey(providerId, "PROVIDER_USER_ID");
		when(connection.fetchUserProfile()).thenReturn(userProfile);
		when(connection.getKey()).thenReturn(key);
		when(connection.getImageUrl()).thenReturn(imageUrl);

		return connection;
	}

	private User createExistingUser(final String login,
			final String email,
			final String firstName,
			final String lastName,
			final String imageUrl) {
		final User user = new User();
		user.setLogin(login);
		user.setPassword(passwordEncoder.encode("password"));
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setImageUrl(imageUrl);
		return userRepository.saveAndFlush(user);
	}
}
