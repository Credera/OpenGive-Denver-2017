package org.opengive.denver.stem.security;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants
{
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";
    public static final String INSTRUCTOR = "ROLE_INSTRUCTOR";
    public static final String STUDENT = "ROLE_STUDENT";
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private enum AuthorityEnum
    {
        ROLE_ADMIN(5),
        ROLE_ORG_ADMIN(4),
        ROLE_INSTRUCTOR(3),
        ROLE_STUDENT(2),
        ROLE_ANONYMOUS(1);

        private final int code;

        AuthorityEnum(int code)
        {
            this.code = code;
        }

        public int getCode()
        {
            return code;
        }
    }

    private static Comparator<AuthorityEnum> authoritiesComparator()
    {
        return (o1, o2) -> o1.getCode() - o2.getCode();
    }

    public static boolean hasHigherPermissions(Collection<String> requesterAuthorities,
        Collection<String> requestedAuthorities)
    {
        Optional<AuthorityEnum> greatestCurrentRole = requesterAuthorities.stream()
            .map(AuthorityEnum::valueOf)
            .max(AuthoritiesConstants.authoritiesComparator());

        Optional<AuthorityEnum> greatestRequestedRole = requestedAuthorities.stream()
            .map(AuthorityEnum::valueOf)
            .max(AuthoritiesConstants.authoritiesComparator());

        return greatestCurrentRole.isPresent() && greatestRequestedRole.isPresent() &&
            (greatestCurrentRole.get().equals(AuthorityEnum.ROLE_ADMIN)
                && greatestRequestedRole.get() == AuthorityEnum.ROLE_ADMIN)
            || authoritiesComparator().compare(greatestCurrentRole.get(), greatestRequestedRole.get()) > 0;
    }
}
