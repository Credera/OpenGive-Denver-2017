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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * A PortfolioItem.
 */
@Entity
@Table(name = "portfolio_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "portfolioitem")
public class PortfolioItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    private Portfolio portfolio;

    @ManyToOne
    private Course course;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotNull
    @JoinTable(name = "portfolio_item_resource",
               joinColumns = @JoinColumn(name="portfolio_items_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="resources_id", referencedColumnName="id"))
    private Set<ItemLink> resources = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public PortfolioItem portfolio(final Portfolio portfolio) {
        this.portfolio = portfolio;
        return this;
    }

    public void setPortfolio(final Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Course getCourse() {
        return course;
    }

    public PortfolioItem course(final Course course) {
        this.course = course;
        return this;
    }

    public void setCourse(final Course course) {
        this.course = course;
    }

    public Set<ItemLink> getResources() {
        return resources;
    }

    public PortfolioItem resources(final Set<ItemLink> itemLinks) {
        resources = itemLinks;
        return this;
    }

    public PortfolioItem addResource(final ItemLink itemLink) {
        resources.add(itemLink);
        return this;
    }

    public PortfolioItem removeResource(final ItemLink itemLink) {
        resources.remove(itemLink);
        return this;
    }

    public void setResources(final Set<ItemLink> itemLinks) {
        resources = itemLinks;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
			return true;
        if (o == null || getClass() != o.getClass())
			return false;
        final PortfolioItem portfolioItem = (PortfolioItem) o;
        if (portfolioItem.id == null || id == null)
			return false;
        return Objects.equals(id, portfolioItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PortfolioItem{" +
            "id=" + id +
            '}';
    }
}
