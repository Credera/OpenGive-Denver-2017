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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * A Session.
 */
@Entity
@Table(name = "session")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "session")
public class Session implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 100)
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
	@Column(name = "active", nullable = false)
	private Boolean active;

	@OneToMany(mappedBy = "session")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<Program> programs = new HashSet<>();

	@ManyToOne(optional = false)
	@NotNull
	private Organization organization;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Session name(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Boolean isActive() {
		return active;
	}

	public Session active(final Boolean active) {
		this.active = active;
		return this;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public Set<Program> getPrograms() {
		return programs;
	}

	public Session programs(final Set<Program> programs) {
		this.programs = programs;
		return this;
	}

	public Session addProgram(final Program program) {
		programs.add(program);
		program.setSession(this);
		return this;
	}

	public Session removeProgram(final Program program) {
		programs.remove(program);
		program.setSession(null);
		return this;
	}

	public void setPrograms(final Set<Program> programs) {
		this.programs = programs;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Session organization(final Organization organization) {
		this.organization = organization;
		return this;
	}

	public void setOrganization(final Organization organization) {
		this.organization = organization;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(final ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(final ZonedDateTime endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Session session = (Session) o;
		if (session.id == null || id == null)
			return false;
		return Objects.equals(id, session.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Session{" +
				"id=" + id +
				", name='" + name + "'" +
				", active='" + active + "'" +
				'}';
	}
}
