package org.opengive.denver.stem.web.rest;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.opengive.denver.stem.domain.User;
import org.opengive.denver.stem.repository.UserRepository;
import org.opengive.denver.stem.security.SecurityUtils;
import org.opengive.denver.stem.service.MailService;
import org.opengive.denver.stem.service.UserService;
import org.opengive.denver.stem.service.dto.UserDTO;
import org.opengive.denver.stem.web.rest.util.HeaderUtil;
import org.opengive.denver.stem.web.rest.vm.KeyAndPasswordVM;
import org.opengive.denver.stem.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

	private final Logger log = LoggerFactory.getLogger(AccountResource.class);

	private final UserRepository userRepository;

	private final UserService userService;

	private final MailService mailService;

	public AccountResource(final UserRepository userRepository, final UserService userService,
			final MailService mailService) {

		this.userRepository = userRepository;
		this.userService = userService;
		this.mailService = mailService;
	}

	/**
	 * POST  /register : register the user.
	 *
	 * @param managedUserVM the managed user View Model
	 * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or email is already in use
	 */
	@PostMapping(path = "/register",
			produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	@Timed
	public ResponseEntity<?> registerAccount(@Valid @RequestBody final ManagedUserVM managedUserVM) {

		final HttpHeaders textPlainHeaders = new HttpHeaders();
		textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

		return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
				.map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
				.orElseGet(() -> userRepository.findOneByEmail(managedUserVM.getEmail())
						.map(user -> new ResponseEntity<>("email address already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
						.orElseGet(() -> {
							final User user = userService
									.createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
											managedUserVM.getFirstName(), managedUserVM.getLastName(),
											managedUserVM.getEmail().toLowerCase(), managedUserVM.getImageUrl());

							mailService.sendActivationEmail(user);
							return new ResponseEntity<>(HttpStatus.CREATED);
						})
						);
	}

	/**
	 * GET  /activate : activate the registered user.
	 *
	 * @param key the activation key
	 * @return the ResponseEntity with status 200 (OK) and the activated user in body, or status 500 (Internal Server Error) if the user couldn't be activated
	 */
	@GetMapping("/activate")
	@Timed
	public ResponseEntity<?> activateAccount(@RequestParam(value = "key") final String key) {
		return userService.activateRegistration(key)
				.map(user -> new ResponseEntity<>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET  /authenticate : check if the user is authenticated, and return its login.
	 *
	 * @param request the HTTP request
	 * @return the login if the user is authenticated
	 */
	@GetMapping("/authenticate")
	@Timed
	public String isAuthenticated(final HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET  /account : get the current user.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
	 */
	@GetMapping("/account")
	@Timed
	public ResponseEntity<UserDTO> getAccount() {
		return Optional.ofNullable(userService.getUserWithAuthorities())
				.map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST  /account : update the current user information.
	 *
	 * @param userDTO the current user information
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
	 */
	@PostMapping("/account")
	@Timed
	public ResponseEntity<?> saveAccount(@Valid @RequestBody final UserDTO userDTO) {
		final Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
		if (existingUser.isPresent() && !existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use")).body(null);
		return userRepository
				.findOneByLogin(SecurityUtils.getCurrentUserLogin())
				.map(u -> {
					userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
							userDTO.getImageUrl());
					return new ResponseEntity<>(HttpStatus.OK);
				})
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST  /account/change_password : changes the current user's password
	 *
	 * @param password the new password
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
	 */
	@PostMapping(path = "/account/change_password",
			produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> changePassword(@RequestBody final String password) {
		if (!checkPasswordLength(password))
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		userService.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * POST   /account/reset_password/init : Send an email to reset the password of the user (if an email address is
     * present for the user)
	 *
	 * @param login the username for which to reset the password
	 * @return the ResponseEntity with status 200 (OK) if the email was sent, or status 400 (Bad Request) if the email address is not registered
	 */
	@PostMapping(path = "/account/reset_password/init",
			produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> requestPasswordReset(@RequestBody final String login) {

		return userService.requestPasswordReset(login)
				.map(user -> {
				    if (!Strings.isNullOrEmpty(user.getEmail())) {
                        mailService.sendPasswordResetMail(user);
                        return new ResponseEntity<>("email was sent", HttpStatus.OK);
                    } else {
				        return new ResponseEntity<String>("No email; contact an administrator", HttpStatus.OK);
                    }
				}).orElse(new ResponseEntity<>("login not registered", HttpStatus.BAD_REQUEST));
	}

	/**
	 * POST   /account/reset_password/finish : Finish to reset the password of the user
	 *
	 * @param keyAndPassword the generated key and the new password
	 * @return the ResponseEntity with status 200 (OK) if the password has been reset,
	 * or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not be reset
	 */
	@PostMapping(path = "/account/reset_password/finish",
			produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> finishPasswordReset(@RequestBody final KeyAndPasswordVM keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword()))
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
				.map(user -> new ResponseEntity<>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private boolean checkPasswordLength(final String password) {
		return !StringUtils.isEmpty(password) &&
				password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
				password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
	}
}
