package org.opengive.denver.stem.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Portfolio.
 */
@Entity
@Table(name = "portfolio")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "portfolio")
public class Portfolio implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(optional = false)
	@NotNull
	@JoinColumn(unique = true)
	private User student;

	@ManyToMany
	@JoinTable(
			name = "portfolio_link",
			joinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"),
			inverseJoinColumns=@JoinColumn(name="link_id", referencedColumnName="id"))
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<ItemLink> portfolioItems = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public User getStudent() {
		return student;
	}

	public Portfolio student(final User user) {
		student = user;
		return this;
	}

	public void setStudent(final User user) {
		student = user;
	}

	public Set<ItemLink> getPortfolioItems() {
		return portfolioItems;
	}

	public Portfolio portfolioItems(final Set<ItemLink> itemLinks) {
		portfolioItems = itemLinks;
		return this;
	}

	public Portfolio addPortfolioItems(final ItemLink itemLink) {
		portfolioItems.add(itemLink);
		return this;
	}

	public Portfolio removePortfolioItems(final ItemLink itemLink) {
		portfolioItems.remove(itemLink);
		return this;
	}

	public void setPortfolioItems(final Set<ItemLink> itemLinks) {
		portfolioItems = itemLinks;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Portfolio portfolio = (Portfolio) o;
		if (portfolio.id == null || id == null)
			return false;
		return Objects.equals(id, portfolio.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Portfolio{" +
				"id=" + id +
				'}';
	}
}
