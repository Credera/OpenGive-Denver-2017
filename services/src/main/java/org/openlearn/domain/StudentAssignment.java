package org.openlearn.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * An entity representing one student's enrollment in one assignment
 */
@Entity
@Table(name = "student_assignment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StudentAssignment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "student_id")
	private User student;

	@ManyToOne(optional = false)
	@JoinColumn(name = "assignment_id")
	private Assignment assignment;

	@Column(name = "grade", length = 20, nullable = false)
	private String grade = "-";

	@Column(name = "complete", nullable = false)
	private Boolean complete = false;

	@Column(name = "on_portfolio", nullable = false)
	private Boolean onPortfolio = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public Boolean getOnPortfolio() {
		return onPortfolio;
	}

	public void setOnPortfolio(Boolean onPortfolio) {
		this.onPortfolio = onPortfolio;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StudentAssignment that = (StudentAssignment) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (student != null ? !student.equals(that.student) : that.student != null) return false;
		if (assignment != null ? !assignment.equals(that.assignment) : that.assignment != null) return false;
		if (grade != null ? !grade.equals(that.grade) : that.grade != null) return false;
		if (complete != null ? !complete.equals(that.complete) : that.complete != null) return false;
		return onPortfolio != null ? onPortfolio.equals(that.onPortfolio) : that.onPortfolio == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (student != null ? student.hashCode() : 0);
		result = 31 * result + (assignment != null ? assignment.hashCode() : 0);
		result = 31 * result + (grade != null ? grade.hashCode() : 0);
		result = 31 * result + (complete != null ? complete.hashCode() : 0);
		result = 31 * result + (onPortfolio != null ? onPortfolio.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "StudentAssignment{" +
			"id=" + id +
			", student=" + student +
			", assignment=" + assignment +
			", grade='" + grade + '\'' +
			", complete=" + complete +
			", onPortfolio=" + onPortfolio +
			'}';
	}
}
