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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Program.
 */
@Entity
@Table(name = "program")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "program")
public class Program implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 50)
	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "active", nullable = false)
	private Boolean active;

	@ManyToOne
	private School school;

	@ManyToOne(optional = false)
	private Session session;

	@OneToMany(mappedBy = "program")
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<Course> courses = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Program name(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Program description(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Boolean isActive() {
		return active;
	}

	public Program active(final Boolean active) {
		this.active = active;
		return this;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public School getSchool() {
		return school;
	}

	public Program school(final School school) {
		this.school = school;
		return this;
	}

	public void setSchool(final School school) {
		this.school = school;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(final Session session) {
		this.session = session;
	}

	public Set<Course> getCourses() {
		return courses;
	}

	public Program courses(final Set<Course> courses) {
		this.courses = courses;
		return this;
	}

	public Program addCourse(final Course course) {
		courses.add(course);
		course.setProgram(this);
		return this;
	}

	public Program removeCourse(final Course course) {
		courses.remove(course);
		course.setProgram(null);
		return this;
	}

	public void setCourses(final Set<Course> courses) {
		this.courses = courses;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Program program = (Program) o;
		if (program.id == null || id == null)
			return false;
		return Objects.equals(id, program.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Program{" +
				"id=" + id +
				", name='" + name + "'" +
				", description='" + description + "'" +
				", active='" + active + "'" +
				'}';
	}
}
