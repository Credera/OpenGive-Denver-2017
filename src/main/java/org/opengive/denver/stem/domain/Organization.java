package org.opengive.denver.stem.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * A Organization.
 */
@Entity
@Table(name = "organization")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "organization")
public class Organization implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 3, max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Size(min = 10, max = 800)
	@Column(name = "description", length = 800)
	private String description;

	@OneToMany(mappedBy = "organization")
	// @JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<Course> courses = new HashSet<>();

	@ManyToMany
	@JoinTable(
			name = "user_org", 
			joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id") }, 
			inverseJoinColumns = {@JoinColumn(name = "org_id", referencedColumnName = "id") }
			)
	// @JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<User> users = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Organization name(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Organization description(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Set<Course> getCourses() {
		return courses;
	}

	public Organization courses(final Set<Course> courses) {
		this.courses = courses;
		return this;
	}

	public Organization addCourses(final Course course) {
		courses.add(course);
		course.setOrganization(this);
		return this;
	}

	public Organization removeCourses(final Course course) {
		courses.remove(course);
		course.setOrganization(null);
		return this;
	}

	public void setCourses(final Set<Course> courses) {
		this.courses = courses;
	}

	public Set<User> getUsers() {
		return users;
	}

	public Organization users(final Set<User> users) {
		this.users = users;
		return this;
	}

	public Organization addUsers(final User user) {
		users.add(user);
		return this;
	}

	public Organization removeUsers(final User user) {
		users.remove(user);
		return this;
	}

	public void setUsers(final Set<User> users) {
		this.users = users;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Organization organization = (Organization) o;
		if (organization.id == null || id == null)
			return false;
		return Objects.equals(id, organization.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Organization{" +
				"id=" + id +
				", name='" + name + "'" +
				", description='" + description + "'" +
				'}';
	}
}
