package org.opengive.denver.stem.service;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.opengive.denver.stem.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import io.github.jhipster.config.JHipsterProperties;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 * </p>
 */
@Service
public class MailService {

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private static final String USER = "user";

	private static final String BASE_URL = "baseUrl";

	private final JHipsterProperties jHipsterProperties;

	private final JavaMailSender javaMailSender;

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;

	public MailService(final JHipsterProperties jHipsterProperties, final JavaMailSender javaMailSender,
			final MessageSource messageSource, final SpringTemplateEngine templateEngine) {

		this.jHipsterProperties = jHipsterProperties;
		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
	}

	@Async
	public void sendEmail(final String to, final String subject, final String content, final boolean isMultipart, final boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
				isMultipart, isHtml, to, subject, content);

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setTo(to);
			message.setFrom(jHipsterProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (final Exception e) {
			log.warn("Email could not be sent to user '{}'", to, e);
		}
	}

	@Async
	public void sendActivationEmail(final User user) {
		log.debug("Sending activation email to '{}'", user.getEmail());
		final Context context = new Context();
		context.setVariable(USER, user);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		final String content = templateEngine.process("activationEmail", context);
		final String subject = messageSource.getMessage("email.activation.title", null, Locale.getDefault());
		sendEmail(user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendCreationEmail(final User user) {
		log.debug("Sending creation email to '{}'", user.getEmail());
		final Context context = new Context();
		context.setVariable(USER, user);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		final String content = templateEngine.process("creationEmail", context);
		final String subject = messageSource.getMessage("email.activation.title", null, Locale.getDefault());
		sendEmail(user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendPasswordResetMail(final User user) {
		log.debug("Sending password reset email to '{}'", user.getEmail());
		final Context context = new Context();
		context.setVariable(USER, user);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		final String content = templateEngine.process("passwordResetEmail", context);
		final String subject = messageSource.getMessage("email.reset.title", null, Locale.getDefault());
		sendEmail(user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendSocialRegistrationValidationEmail(final User user, final String provider) {
		log.debug("Sending social registration validation email to '{}'", user.getEmail());
		final Context context = new Context();
		context.setVariable(USER, user);
		context.setVariable("provider", StringUtils.capitalize(provider));
		final String content = templateEngine.process("socialRegistrationValidationEmail", context);
		final String subject = messageSource.getMessage("email.social.registration.title", null, Locale.getDefault());
		sendEmail(user.getEmail(), subject, content, false, true);
	}
}
