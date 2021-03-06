package org.openlearn.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Portfolio Item.
 */
@Entity
@Table(name = "portfolio_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PortfolioItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", length = 500, nullable = false)
	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "student_id")
	private User student;

	@Column(name = "url")
	private String url;

	@ManyToOne(optional = false)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PortfolioItem that = (PortfolioItem) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (student != null ? !student.equals(that.student) : that.student != null) return false;
		if (url != null ? !url.equals(that.url) : that.url != null) return false;
		return organization != null ? organization.equals(that.organization) : that.organization == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (student != null ? student.hashCode() : 0);
		result = 31 * result + (url != null ? url.hashCode() : 0);
		result = 31 * result + (organization != null ? organization.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PortfolioItem{" +
			"id=" + id +
			", name='" + name + '\'' +
			", description='" + description + '\'' +
			", student=" + student +
			", url='" + url + '\'' +
			", organization=" + organization +
			'}';
	}
}
