package org.opengive.denver.stem.service.dto;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.opengive.denver.stem.config.Constants;
import org.opengive.denver.stem.domain.Address;
import org.opengive.denver.stem.domain.Authority;
import org.opengive.denver.stem.domain.User;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO
{

    private Long id;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 100)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    @Size(min = 10, max = 15)
    private String phoneNumber;

    private Address address;

    @Size(max = 256)
    private String imageUrl;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private String createdBy;

    private ZonedDateTime createdDate;

    private String lastModifiedBy;

    private ZonedDateTime lastModifiedDate;

    private Set<String> authorities;

    private boolean is14Plus;

    public UserDTO()
    {
        // Empty constructor needed for MapStruct.
    }

    public UserDTO(final User user)
    {
        this(user.getId(), user.getLogin(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getPhoneNumber(), user.getAddress(), user.getActivated(), user.getImageUrl(),
            user.getLangKey(), user.getCreatedBy(), user.getCreatedDate(), user.getLastModifiedBy(),
            user.getLastModifiedDate(),
            user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()), user.getIs14Plus());
    }

    public UserDTO(final Long id, final String login, final String firstName, final String lastName,
        final String email, final String phoneNumber, final Address address, final boolean activated,
        final String imageUrl, final String langKey, final String createdBy, final ZonedDateTime createdDate,
        final String lastModifiedBy, final ZonedDateTime lastModifiedDate, final Set<String> authorities, final boolean is14Plus)
    {

        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.imageUrl = imageUrl;
        this.langKey = langKey;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.authorities = authorities;
        this.is14Plus = is14Plus;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(final String login)
    {
        this.login = login;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public Address getAddress()
    {
        return address;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public String getLangKey()
    {
        return langKey;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public ZonedDateTime getCreatedDate()
    {
        return createdDate;
    }

    public String getLastModifiedBy()
    {
        return lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final ZonedDateTime lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<String> getAuthorities()
    {
        return authorities;
    }

    public boolean getIs14Plus()
    {
        return is14Plus;
    }

    public void setIs14Plus(boolean is14Plus)
    {
        this.is14Plus = is14Plus;
    }

    @Override
    public String toString()
    {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            "}";
    }
}
