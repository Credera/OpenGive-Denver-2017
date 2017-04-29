package org.opengive.denver.stem.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {
	public static final String ADMIN = "ROLE_ADMIN";
	public static final String STUDENT = "ROLE_STUDENT";
	public static final String INSTRUCTOR = "ROLE_INSTRUCTOR";
	public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";

	public static final String ANONYMOUS = "ROLE_ANONYMOUS";

	private AuthoritiesConstants() {
	}
}
