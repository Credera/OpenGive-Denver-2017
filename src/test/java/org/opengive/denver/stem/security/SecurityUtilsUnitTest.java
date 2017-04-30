package org.opengive.denver.stem.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test class for the SecurityUtils utility class.
 *
 * @see SecurityUtils
 */
public class SecurityUtilsUnitTest
{

    @Test
    public void testgetCurrentUserLogin()
    {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        String login = SecurityUtils.getCurrentUserLogin();
        assertThat(login).isEqualTo("admin");
    }

    @Test
    public void testIsAuthenticated()
    {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    public void testAnonymousIsNotAuthenticated()
    {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        securityContext
            .setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    public void testAuthComparator1()
    {
        List<String> requestorPermissions = Arrays.asList(AuthoritiesConstants.ADMIN);
        List<String> requestedPermissions = Arrays.asList(AuthoritiesConstants.STUDENT);
        assertThat(AuthoritiesConstants.hasHigherPermissions(requestorPermissions, requestedPermissions)).isTrue();
    }

    @Test
    public void testAuthComparator2()
    {
        List<String> requestorPermissions = Arrays.asList(AuthoritiesConstants.ADMIN);
        List<String> requestedPermissions = Arrays.asList(AuthoritiesConstants.STUDENT);
        assertThat(AuthoritiesConstants.hasHigherPermissions(requestedPermissions, requestorPermissions)).isFalse();
    }

    @Test
    public void testAuthComparator3()
    {
        List<String> requestorPermissions = Arrays.asList(AuthoritiesConstants.ORG_ADMIN);
        List<String> requestedPermissions = Arrays.asList(AuthoritiesConstants.ADMIN);
        assertThat(AuthoritiesConstants.hasHigherPermissions(requestorPermissions, requestedPermissions)).isFalse();
    }

    @Test
    public void testAuthComparator4()
    {
        List<String> requestorPermissions = Arrays.asList(AuthoritiesConstants.INSTRUCTOR);
        List<String> requestedPermissions = Arrays.asList(AuthoritiesConstants.INSTRUCTOR);
        assertThat(AuthoritiesConstants.hasHigherPermissions(requestorPermissions, requestedPermissions)).isFalse();
    }

    @Test
    public void testAuthComparator5()
    {
        List<String> requestorPermissions = Arrays.asList(AuthoritiesConstants.ADMIN, AuthoritiesConstants.INSTRUCTOR,
            AuthoritiesConstants.INSTRUCTOR, AuthoritiesConstants.ORG_ADMIN, AuthoritiesConstants.STUDENT);
        List<String> requestedPermissions = Arrays.asList(AuthoritiesConstants.INSTRUCTOR);
        assertThat(AuthoritiesConstants.hasHigherPermissions(requestorPermissions, requestedPermissions)).isTrue();
    }

    @Test
    public void testAuthComparator6()
    {
        List<String> requestorPermissions = Arrays.asList(AuthoritiesConstants.ADMIN);
        List<String> requestedPermissions = Arrays.asList(AuthoritiesConstants.ADMIN);
        assertThat(AuthoritiesConstants.hasHigherPermissions(requestorPermissions, requestedPermissions)).isTrue();
    }
}
