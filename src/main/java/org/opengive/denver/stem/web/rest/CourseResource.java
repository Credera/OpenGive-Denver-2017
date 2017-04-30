package org.opengive.denver.stem.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.opengive.denver.stem.domain.Course;
import org.opengive.denver.stem.service.CourseService;
import org.opengive.denver.stem.web.rest.util.HeaderUtil;
import org.opengive.denver.stem.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing Course.
 */
@RestController
@RequestMapping("/api")
public class CourseResource {

	private final Logger log = LoggerFactory.getLogger(CourseResource.class);

	private static final String ENTITY_NAME = "course";

	private final CourseService courseService;

	public CourseResource(final CourseService courseService) {
		this.courseService = courseService;
	}

	/**
	 * POST  /courses : Create a new course.
	 *
	 * @param course the course to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new course, or with status 400 (Bad Request) if the course has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/courses")
	@Timed
	public ResponseEntity<Course> createCourse(@Valid @RequestBody final Course course) throws URISyntaxException {
		log.debug("REST request to save Course : {}", course);
		if (course.getId() != null)
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new course cannot already have an ID")).body(null);
		final Course result = courseService.save(course);
		return ResponseEntity.created(new URI("/api/courses/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * PUT  /courses : Updates an existing course.
	 *
	 * @param course the course to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated course,
	 * or with status 400 (Bad Request) if the course is not valid,
	 * or with status 500 (Internal Server Error) if the course couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/courses")
	@Timed
	public ResponseEntity<Course> updateCourse(@Valid @RequestBody final Course course) throws URISyntaxException {
		log.debug("REST request to update Course : {}", course);
		if (course.getId() == null)
			return createCourse(course);
		final Course result = courseService.save(course);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, course.getId().toString()))
				.body(result);
	}

	/**
	 * GET  /courses : get all the courses.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of courses in body
	 */
	@GetMapping("/courses")
	@Timed
	public ResponseEntity<List<Course>> getAllCourses(@ApiParam final Pageable pageable) {
		log.debug("REST request to get a page of Courses");
		final Page<Course> page = courseService.findAll(pageable);
		final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/courses");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET  /courses/:id : get the "id" course.
	 *
	 * @param id the id of the course to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the course, or with status 404 (Not Found)
	 */
	@GetMapping("/courses/{id}")
	@Timed
	public ResponseEntity<Course> getCourse(@PathVariable final Long id) {
		log.debug("REST request to get Course : {}", id);
		final Course course = courseService.findOne(id);
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(course));
	}

	/**
	 * DELETE  /courses/:id : delete the "id" course.
	 *
	 * @param id the id of the course to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/courses/{id}")
	@Timed
	public ResponseEntity<Void> deleteCourse(@PathVariable final Long id) {
		log.debug("REST request to delete Course : {}", id);
		courseService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}

	/**
	 * SEARCH  /_search/courses?query=:query : search for the course corresponding
	 * to the query.
	 *
	 * @param query the query of the course search
	 * @param pageable the pagination information
	 * @return the result of the search
	 */
	@GetMapping("/_search/courses")
	@Timed
	public ResponseEntity<List<Course>> searchCourses(@RequestParam final String query, @ApiParam final Pageable pageable) {
		log.debug("REST request to search for a page of Courses for query {}", query);
		final Page<Course> page = courseService.search(query, pageable);
		final HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/courses");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}


}
