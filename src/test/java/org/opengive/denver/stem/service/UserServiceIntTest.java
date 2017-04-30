package org.opengive.denver.stem.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengive.denver.stem.OpenGiveApplication;
import org.opengive.denver.stem.config.Constants;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.UserRepository;
import org.opengive.denver.stem.service.dto.UserDTO;
import org.opengive.denver.stem.service.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenGiveApplication.class)
@Transactional
public class UserServiceIntTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Test
	public void assertThatUserMustExistToResetPassword() {
		Optional<User> maybeUser = userService.requestPasswordReset("john.doe");
		assertThat(maybeUser.isPresent()).isFalse();

		maybeUser = userService.requestPasswordReset("admin");
		assertThat(maybeUser.isPresent()).isTrue();

		assertThat(maybeUser.get().getEmail()).isEqualTo("admin@localhost");
		assertThat(maybeUser.get().getResetDate()).isNotNull();
		assertThat(maybeUser.get().getResetKey()).isNotNull();
	}

	@Test
	public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
		final User user = userService.createUser("johndoe", "johndoe", "John", "Doe", "john.doe@localhost",
				"http://placehold.it/50x50");
		final Optional<User> maybeUser = userService.requestPasswordReset("johndoe");
		assertThat(maybeUser.isPresent()).isFalse();
		userRepository.delete(user);
	}

	@Test
	public void assertThatResetKeyMustNotBeOlderThan24Hours() {
		final User user = userService.createUser("johndoe", "johndoe", "John", "Doe", "john.doe@localhost",
				"http://placehold.it/50x50");

		final ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(25);
		final String resetKey = RandomUtil.generateResetKey();
		user.setActivated(true);
		user.setResetDate(daysAgo);
		user.setResetKey(resetKey);

		userRepository.save(user);

		final Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());

		assertThat(maybeUser.isPresent()).isFalse();

		userRepository.delete(user);
	}

	@Test
	public void assertThatResetKeyMustBeValid() {
		final User user = userService.createUser("johndoe", "johndoe", "John", "Doe", "john.doe@localhost",
				"http://placehold.it/50x50");

		final ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(25);
		user.setActivated(true);
		user.setResetDate(daysAgo);
		user.setResetKey("1234");
		userRepository.save(user);
		final Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
		assertThat(maybeUser.isPresent()).isFalse();
		userRepository.delete(user);
	}

	@Test
	public void assertThatUserCanResetPassword() {
		final User user = userService.createUser("johndoe", "johndoe", "John", "Doe", "john.doe@localhost",
				"http://placehold.it/50x50");
		final String oldPassword = user.getPassword();
		final ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(2);
		final String resetKey = RandomUtil.generateResetKey();
		user.setActivated(true);
		user.setResetDate(daysAgo);
		user.setResetKey(resetKey);
		userRepository.save(user);
		final Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
		assertThat(maybeUser.isPresent()).isTrue();
		assertThat(maybeUser.get().getResetDate()).isNull();
		assertThat(maybeUser.get().getResetKey()).isNull();
		assertThat(maybeUser.get().getPassword()).isNotEqualTo(oldPassword);

		userRepository.delete(user);
	}

	@Test
	public void testFindNotActivatedUsersByCreationDateBefore() {
		userService.removeNotActivatedUsers();
		final ZonedDateTime now = ZonedDateTime.now();
		final List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
		assertThat(users).isEmpty();
	}

	@Test
	public void assertThatAnonymousUserIsNotGet() {
		final PageRequest pageable = new PageRequest(0, (int) userRepository.count());
		final Page<UserDTO> allManagedUsers = userService.getAllManagedUsers(pageable);
		assertThat(allManagedUsers.getContent().stream()
				.noneMatch(user -> Constants.ANONYMOUS_USER.equals(user.getLogin())))
		.isTrue();
	}
}
