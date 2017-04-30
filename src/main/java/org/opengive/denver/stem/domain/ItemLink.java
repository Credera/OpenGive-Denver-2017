package org.opengive.denver.stem.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

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

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ItemLink name(final String name) {
        this.name = name;
        return this;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public ItemLink description(final String description) {
        this.description = description;
        return this;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public ItemLink thumbnailImageUrl(final String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
        return this;
    }

    public void setThumbnailImageUrl(final String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public ItemLink itemUrl(final String itemUrl) {
        this.itemUrl = itemUrl;
        return this;
    }

    public void setItemUrl(final String itemUrl) {
        this.itemUrl = itemUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
			return true;
        if (o == null || getClass() != o.getClass())
			return false;
        final ItemLink itemLink = (ItemLink) o;
        if (itemLink.id == null || id == null)
			return false;
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
