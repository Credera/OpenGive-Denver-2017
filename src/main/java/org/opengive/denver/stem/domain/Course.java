package org.opengive.denver.stem.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Course.
 */
//TODO: Set the correct @JsonIgnore Fields
@Entity
@Table(name = "course")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "course")
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 3, max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Size(min = 5, max = 200)
	@Column(name = "description", length = 200)
	private String description;

	@Column(name = "start_date")
	private ZonedDateTime startDate;

	@Column(name = "end_date")
	private ZonedDateTime endDate;

	@NotNull
	@ManyToOne(optional = false)
    @JsonIgnore
	private Organization organization;

	@NotNull
	@ManyToOne(optional = false)
    @JsonIgnore
	private Program program;

	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(unique = true)
    @JsonIgnore
	private User instructor;

	@ManyToMany
	@JoinTable(
			name="course_link",
			joinColumns=@JoinColumn(name="course_id", referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="link_id", referencedColumnName="id"))
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<ItemLink> resources = new HashSet<>();

	@OneToMany
	@JoinTable(
			name = "student_course",
			joinColumns = {	@JoinColumn(name = "user_id", referencedColumnName = "id") },
			inverseJoinColumns = { @JoinColumn(name = "course_id", referencedColumnName = "id") }
			)
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<User> students = new HashSet<>();

	@OneToMany(mappedBy = "course")
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<Activity> activities = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Course name(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Course description(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public Course startDate(final ZonedDateTime startDate) {
		this.startDate = startDate;
		return this;
	}

	public void setStartDate(final ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public Course endDate(final ZonedDateTime endDate) {
		this.endDate = endDate;
		return this;
	}

	public void setEndDate(final ZonedDateTime endDate) {
		this.endDate = endDate;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Course organization(final Organization organization) {
		this.organization = organization;
		return this;
	}

	public void setOrganization(final Organization organization) {
		this.organization = organization;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(final Program program) {
		this.program = program;
	}

	public User getInstructor() {
		return instructor;
	}

	public Course instructor(final User user) {
		instructor = user;
		return this;
	}

	public void setInstructor(final User user) {
		instructor = user;
	}

	public Set<ItemLink> getResources() {
		return resources;
	}

	public Course resources(final Set<ItemLink> itemLinks) {
		resources = itemLinks;
		return this;
	}

	public Course addResources(final ItemLink itemLink) {
		resources.add(itemLink);
		return this;
	}

	public Course removeResources(final ItemLink itemLink) {
		resources.remove(itemLink);
		return this;
	}

	public void setResources(final Set<ItemLink> itemLinks) {
		resources = itemLinks;
	}

	public Set<User> getStudents() {
		return students;
	}

	public Course students(final Set<User> users) {
		students = users;
		return this;
	}

	public Course addStudents(final User user) {
		students.add(user);
		return this;
	}

	public Course removeStudents(final User user) {
		students.remove(user);
		return this;
	}

	public void setStudents(final Set<User> users) {
		students = users;
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public Course activities(final Set<Activity> activities) {
		this.activities = activities;
		return this;
	}

	public Course addActivities(final Activity activity) {
		activities.add(activity);
		activity.setCourse(this);
		return this;
	}

	public Course removeActivities(final Activity activity) {
		activities.remove(activity);
		activity.setCourse(null);
		return this;
	}

	public void setActivities(final Set<Activity> activities) {
		this.activities = activities;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Course course = (Course) o;
		if (course.id == null || id == null)
			return false;
		return Objects.equals(id, course.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Course [id=").append(id).append(", name=").append(name).append(", description=")
		.append(description).append(", startDate=").append(startDate).append(", endDate=").append(endDate)
		.append(", organization=").append(organization).append(", program=").append(program)
		.append(", instructor=").append(instructor).append(", resources=").append(resources)
		.append(", students=").append(students).append(", activities=").append(activities).append("]");
		return builder.toString();
	}
}
