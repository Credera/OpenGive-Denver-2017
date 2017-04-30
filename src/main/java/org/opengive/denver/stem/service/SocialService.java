package org.opengive.denver.stem.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.opengive.denver.stem.domain.Authority;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.AuthorityRepository;
import org.opengive.denver.stem.repository.UserRepository;
import org.opengive.denver.stem.repository.search.UserSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;

@Service
public class SocialService {

	private final Logger log = LoggerFactory.getLogger(SocialService.class);

	private final UsersConnectionRepository usersConnectionRepository;

	private final AuthorityRepository authorityRepository;

	private final PasswordEncoder passwordEncoder;

	private final UserRepository userRepository;

	private final MailService mailService;

	private final UserSearchRepository userSearchRepository;

	public SocialService(final UsersConnectionRepository usersConnectionRepository, final AuthorityRepository authorityRepository,
			final PasswordEncoder passwordEncoder, final UserRepository userRepository,
			final MailService mailService, final UserSearchRepository userSearchRepository) {

		this.usersConnectionRepository = usersConnectionRepository;
		this.authorityRepository = authorityRepository;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.mailService = mailService;
		this.userSearchRepository = userSearchRepository;
	}

	public void deleteUserSocialConnection(final String login) {
		final ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(login);
		connectionRepository.findAllConnections().keySet().stream()
		.forEach(providerId -> {
			connectionRepository.removeConnections(providerId);
			log.debug("Delete user social connection providerId: {}", providerId);
		});
	}

	public void createSocialUser(final Connection<?> connection) {
		if (connection == null) {
			log.error("Cannot create social user because connection is null");
			throw new IllegalArgumentException("Connection cannot be null");
		}
		final UserProfile userProfile = connection.fetchUserProfile();
		final String providerId = connection.getKey().getProviderId();
		final String imageUrl = connection.getImageUrl();
		final User user = createUserIfNotExist(userProfile, providerId, imageUrl);
		createSocialConnection(user.getLogin(), connection);
		mailService.sendSocialRegistrationValidationEmail(user, providerId);
	}

	private User createUserIfNotExist(final UserProfile userProfile, final String providerId, final String imageUrl) {
		final String email = userProfile.getEmail();
		String userName = userProfile.getUsername();
		if (!StringUtils.isBlank(userName))
			userName = userName.toLowerCase(Locale.ENGLISH);
		if (StringUtils.isBlank(email) && StringUtils.isBlank(userName)) {
			log.error("Cannot create social user because email and login are null");
			throw new IllegalArgumentException("Email and login cannot be null");
		}
		if (StringUtils.isBlank(email) && userRepository.findOneByLogin(userName).isPresent()) {
			log.error("Cannot create social user because email is null and login already exist, login -> {}", userName);
			throw new IllegalArgumentException("Email cannot be null with an existing login");
		}
		if (!StringUtils.isBlank(email)) {
			final Optional<User> user = userRepository.findOneByEmail(email);
			if (user.isPresent()) {
				log.info("User already exist associate the connection to this account");
				return user.get();
			}
		}

		final String login = getLoginDependingOnProviderId(userProfile, providerId);
		final String encryptedPassword = passwordEncoder.encode(RandomStringUtils.random(10));
		final Set<Authority> authorities = new HashSet<>(1);
		authorities.add(authorityRepository.findOne("ROLE_USER"));

		final User newUser = new User();
		newUser.setLogin(login);
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(userProfile.getFirstName());
		newUser.setLastName(userProfile.getLastName());
		newUser.setEmail(email);
		newUser.setActivated(true);
		newUser.setAuthorities(authorities);
		newUser.setImageUrl(imageUrl);

		userSearchRepository.save(newUser);
		return userRepository.save(newUser);
	}

	/**
	 * @return login if provider manage a login like Twitter or Github otherwise email address.
	 *         Because provider like Google or Facebook didn't provide login or login like "12099388847393"
	 */
	private String getLoginDependingOnProviderId(final UserProfile userProfile, final String providerId) {
		switch (providerId) {
		case "twitter":
			return userProfile.getUsername().toLowerCase();
		default:
			return userProfile.getEmail();
		}
	}

	private void createSocialConnection(final String login, final Connection<?> connection) {
		final ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(login);
		connectionRepository.addConnection(connection);
	}
}
