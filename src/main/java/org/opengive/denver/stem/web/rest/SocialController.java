package org.opengive.denver.stem.web.rest;

import org.opengive.denver.stem.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/social")
public class SocialController {

	private final Logger log = LoggerFactory.getLogger(SocialController.class);

	private final SocialService socialService;

	private final ProviderSignInUtils providerSignInUtils;

	public SocialController(final SocialService socialService, final ProviderSignInUtils providerSignInUtils) {
		this.socialService = socialService;
		this.providerSignInUtils = providerSignInUtils;
	}

	@GetMapping("/signup")
	public RedirectView signUp(final WebRequest webRequest) {
		try {
			final Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
			socialService.createSocialUser(connection);
			return new RedirectView(URIBuilder.fromUri("/#/social-register/" + connection.getKey().getProviderId())
					.queryParam("success", "true")
					.build().toString(), true);
		} catch (final Exception e) {
			log.error("Exception creating social user: ", e);
			return new RedirectView(URIBuilder.fromUri("/#/social-register/no-provider")
					.queryParam("success", "false")
					.build().toString(), true);
		}
	}
}
