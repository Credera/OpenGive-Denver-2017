package org.opengive.denver.stem.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.opengive.denver.stem.domain.Activity;
import org.opengive.denver.stem.service.ActivityService;
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
 * REST controller for managing Activity.
 */
@RestController
@RequestMapping("/api")
public class ActivityResource {

	private final Logger log = LoggerFactory.getLogger(ActivityResource.class);

	private static final String ENTITY_NAME = "activity";

	private final ActivityService activityService;

	public ActivityResource(final ActivityService activityService) {
		this.activityService = activityService;
	}

	/**
	 * POST  /activities : Create a new activity.
	 *
	 * @param activity the activity to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new activity, or with status 400 (Bad Request) if the activity has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/activities")
	@Timed
	public ResponseEntity<Activity> createActivity(@Valid @RequestBody final Activity activity) throws URISyntaxException {
		log.debug("REST request to save Activity : {}", activity);
		if (activity.getId() != null)
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new activity cannot already have an ID")).body(null);
		final Activity result = activityService.save(activity);
		return ResponseEntity.created(new URI("/api/activities/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * PUT  /activities : Updates an existing activity.
	 *
	 * @param activity the activity to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated activity,
	 * or with status 400 (Bad Request) if the activity is not valid,
	 * or with status 500 (Internal Server Error) if the activity couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/activities")
	@Timed
	public ResponseEntity<Activity> updateActivity(@Valid @RequestBody final Activity activity) throws URISyntaxException {
		log.debug("REST request to update Activity : {}", activity);
		if (activity.getId() == null)
			return createActivity(activity);
		final Activity result = activityService.save(activity);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, activity.getId().toString()))
				.body(result);
	}

	/**
	 * GET  /activities : get all the activities.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of activities in body
	 */
	@GetMapping("/activities")
	@Timed
	public ResponseEntity<List<Activity>> getAllActivitys(@ApiParam final Pageable pageable) {
		log.debug("REST request to get a page of Activitys");
		final Page<Activity> page = activityService.findAll(pageable);
		final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activities");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET  /activities/:id : get the "id" activity.
	 *
	 * @param id the id of the activity to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the activity, or with status 404 (Not Found)
	 */
	@GetMapping("/activities/{id}")
	@Timed
	public ResponseEntity<Activity> getActivity(@PathVariable final Long id) {
		log.debug("REST request to get Activity : {}", id);
		final Activity activity = activityService.findOne(id);
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(activity));
	}

	/**
	 * DELETE  /activities/:id : delete the "id" activity.
	 *
	 * @param id the id of the activity to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/activities/{id}")
	@Timed
	public ResponseEntity<Void> deleteActivity(@PathVariable final Long id) {
		log.debug("REST request to delete Activity : {}", id);
		activityService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}

	/**
	 * SEARCH  /_search/activities?query=:query : search for the activity corresponding
	 * to the query.
	 *
	 * @param query the query of the activity search
	 * @param pageable the pagination information
	 * @return the result of the search
	 */
	@GetMapping("/_search/activities")
	@Timed
	public ResponseEntity<List<Activity>> searchActivitys(@RequestParam final String query, @ApiParam final Pageable pageable) {
		log.debug("REST request to search for a page of Activitys for query {}", query);
		final Page<Activity> page = activityService.search(query, pageable);
		final HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/activities");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}


}
