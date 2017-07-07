package org.openlearn.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;
import org.openlearn.config.Constants;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A user.
 */
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "user")
public class User extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 100)
	@Column(length = 100, unique = true, nullable = false)
	private String login;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	@Column(name = "password_hash",length = 60)
	private String password;

	@Size(max = 50)
	@Column(name = "first_name", length = 50)
	private String firstName;

	@Size(max = 50)
	@Column(name = "last_name", length = 50)
	private String lastName;

	@Email
	@Size(min = 5, max = 100)
	@Column(length = 100, unique = true)
	private String email;

	@Size(max = 15)
	@Column(name = "phone_num", length = 15)
	private String phoneNumber;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "addr_id")
	private Address address;

	@NotNull
	@Column(nullable = false)
	private boolean activated = false;

	@Size(max = 256)
	@Column(name = "image_url", length = 256)
	private String imageUrl;

  @JsonIgnore
  @Size(max = 20)
	@Column(name = "activation_key", length = 20)
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	private String resetKey;

	@Column(name = "reset_date")
	private ZonedDateTime resetDate = null;

	@NotNull
	@Column(name = "is_14_plus", nullable = false)
	private boolean is14Plus;

	@Size(max = 2000)
	@Column(name = "biography", length = 2000)
	private String biography;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_authority",
			joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@BatchSize(size = 20)
	private Set<Authority> authorities = new HashSet<>();

  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_org",
			joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id") },
			inverseJoinColumns = {@JoinColumn(name = "org_id", referencedColumnName = "id") }
			)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private final Set<Organization> organizations = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	//Lowercase the login before saving it in database
	public void setLogin(final String login) {
		this.login = login.toLowerCase(Locale.ENGLISH);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(final Address address) {
		this.address = address;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(final String activationKey) {
		this.activationKey = activationKey;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(final String resetKey) {
		this.resetKey = resetKey;
	}

	public ZonedDateTime getResetDate() {
		return resetDate;
	}

	public void setResetDate(final ZonedDateTime resetDate) {
		this.resetDate = resetDate;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(final Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public boolean getIs14Plus()
	{
		return is14Plus;
	}

	public void setIs14Plus(final boolean is14Plus)
	{
		this.is14Plus = is14Plus;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(final String biography) {
		this.biography = biography;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final User user = (User) o;

		return login.equals(user.login);
	}

	@Override
	public int hashCode() {
		return login.hashCode();
	}

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", login='" + login + '\'' +
      ", password='" + password + '\'' +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", email='" + email + '\'' +
      ", phoneNumber='" + phoneNumber + '\'' +
      ", address=" + address +
      ", activated=" + activated +
      ", imageUrl='" + imageUrl + '\'' +
      ", activationKey='" + activationKey + '\'' +
      ", resetKey='" + resetKey + '\'' +
      ", resetDate=" + resetDate +
      ", is14Plus=" + is14Plus +
      ", biography='" + biography + '\'' +
      ", authorities=" + authorities +
      ", organizations=" + organizations +
      '}';
  }
}
