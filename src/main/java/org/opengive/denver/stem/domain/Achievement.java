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
 * A Achievement.
 */
@Entity
@Table(name = "achievement")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "achievement")
public class Achievement implements Serializable {

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

	@Size(min = 10, max = 200)
	@Column(name = "badge_url", length = 200)
	private String badgeUrl;

	@ManyToOne
	private Activity activity;

	@OneToMany
	@JoinTable(
			name = "USER_ACHV", 
			joinColumns = {	@JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, 
			inverseJoinColumns = { @JoinColumn(name = "ACHV_ID", referencedColumnName = "ID") }
			)
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<User> achievedBy = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Achievement name(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Achievement description(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getBadgeUrl() {
		return badgeUrl;
	}

	public Achievement badgeUrl(final String badgeUrl) {
		this.badgeUrl = badgeUrl;
		return this;
	}

	public void setBadgeUrl(final String badgeUrl) {
		this.badgeUrl = badgeUrl;
	}

	public Activity getActivity() {
		return activity;
	}

	public Achievement activity(final Activity activity) {
		this.activity = activity;
		return this;
	}

	public void setActivity(final Activity activity) {
		this.activity = activity;
	}

	public Set<User> getAchievedBy() {
		return achievedBy;
	}

	public Achievement achievedBy(final Set<User> users) {
		achievedBy = users;
		return this;
	}

	public Achievement addAchievedBy(final User user) {
		achievedBy.add(user);
		return this;
	}

	public Achievement removeAchievedBy(final User user) {
		achievedBy.remove(user);
		return this;
	}

	public void setAchievedBy(final Set<User> users) {
		achievedBy = users;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Achievement achievement = (Achievement) o;
		if (achievement.id == null || id == null)
			return false;
		return Objects.equals(id, achievement.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Achievement{" +
				"id=" + id +
				", name='" + name + "'" +
				", description='" + description + "'" +
				", badgeUrl='" + badgeUrl + "'" +
				'}';
	}
}
