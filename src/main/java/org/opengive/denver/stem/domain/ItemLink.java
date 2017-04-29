package org.opengive.denver.stem.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ItemLink.
 */
@Entity
@Table(name = "item_link")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "itemlink")
public class ItemLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(min = 10, max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @NotNull
    @Size(min = 10, max = 200)
    @Column(name = "thumbnail_image_url", length = 200, nullable = false)
    private String thumbnailImageUrl;

    @NotNull
    @Size(min = 10, max = 200)
    @Column(name = "item_url", length = 200, nullable = false)
    private String itemUrl;

    @ManyToOne
    private Portfolio portfolio;

    @ManyToOne
    private Course course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ItemLink name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public ItemLink description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public ItemLink thumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
        return this;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public ItemLink itemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
        return this;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public ItemLink portfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        return this;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Course getCourse() {
        return course;
    }

    public ItemLink course(Course course) {
        this.course = course;
        return this;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemLink itemLink = (ItemLink) o;
        if (itemLink.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, itemLink.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemLink{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", thumbnailImageUrl='" + thumbnailImageUrl + "'" +
            ", itemUrl='" + itemUrl + "'" +
            '}';
    }
}
