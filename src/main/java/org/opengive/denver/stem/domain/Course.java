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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Course.
 */
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
	private Organization organization;

	@NotNull
	@ManyToOne(optional = false)
	private Program program;

	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(unique = true)
	private User instructor;

	@OneToMany(mappedBy = "course")
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<ItemLink> resources = new HashSet<>();

	@OneToMany
	@JoinTable(
			name = "STDT_PRGM",
			joinColumns = {	@JoinColumn(name = "USER_ID", referencedColumnName = "ID") },
			inverseJoinColumns = { @JoinColumn(name = "PRGM_ID", referencedColumnName = "ID") }
			)
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<User> students = new HashSet<>();

	@OneToMany(mappedBy = "course")
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<Milestone> milestones = new HashSet<>();

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
		itemLink.setCourse(this);
		return this;
	}

	public Course removeResources(final ItemLink itemLink) {
		resources.remove(itemLink);
		itemLink.setCourse(null);
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

	public Set<Milestone> getMilestones() {
		return milestones;
	}

	public Course milestones(final Set<Milestone> milestones) {
		this.milestones = milestones;
		return this;
	}

	public Course addMilestones(final Milestone milestone) {
		milestones.add(milestone);
		milestone.setCourse(this);
		return this;
	}

	public Course removeMilestones(final Milestone milestone) {
		milestones.remove(milestone);
		milestone.setCourse(null);
		return this;
	}

	public void setMilestones(final Set<Milestone> milestones) {
		this.milestones = milestones;
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
		.append(", students=").append(students).append(", milestones=").append(milestones).append("]");
		return builder.toString();
	}
}
