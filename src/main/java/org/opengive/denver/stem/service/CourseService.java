package org.opengive.denver.stem.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.opengive.denver.stem.domain.Course;
import org.opengive.denver.stem.repository.CourseRepository;
import org.opengive.denver.stem.repository.search.CourseSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Course.
 */
@Service
@Transactional
public class CourseService {

	private final Logger log = LoggerFactory.getLogger(CourseService.class);

	private final CourseRepository courseRepository;

	private final CourseSearchRepository courseSearchRepository;

	public CourseService(final CourseRepository courseRepository, final CourseSearchRepository courseSearchRepository) {
		this.courseRepository = courseRepository;
		this.courseSearchRepository = courseSearchRepository;
	}

	/**
	 * Save a course.
	 *
	 * @param course the entity to save
	 * @return the persisted entity
	 */
	public Course save(final Course course) {
		log.debug("Request to save Course : {}", course);
		final Course result = courseRepository.save(course);
		courseSearchRepository.save(result);
		return result;
	}

	/**
	 *  Get all the courses.
	 * 
	 *  @param pageable the pagination information
	 *  @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<Course> findAll(final Pageable pageable) {
		log.debug("Request to get all Courses");
		final Page<Course> result = courseRepository.findAll(pageable);
		return result;
	}

	/**
	 *  Get one course by id.
	 *
	 *  @param id the id of the entity
	 *  @return the entity
	 */
	@Transactional(readOnly = true)
	public Course findOne(final Long id) {
		log.debug("Request to get Course : {}", id);
		final Course course = courseRepository.findOne(id);
		return course;
	}

	/**
	 *  Delete the  course by id.
	 *
	 *  @param id the id of the entity
	 */
	public void delete(final Long id) {
		log.debug("Request to delete Course : {}", id);
		courseRepository.delete(id);
		courseSearchRepository.delete(id);
	}

	/**
	 * Search for the course corresponding to the query.
	 *
	 *  @param query the query of the search
	 *  @param pageable the pagination information
	 *  @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<Course> search(final String query, final Pageable pageable) {
		log.debug("Request to search for a page of Courses for query {}", query);
		final Page<Course> result = courseSearchRepository.search(queryStringQuery(query), pageable);
		return result;
	}
}
