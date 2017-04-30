package org.opengive.denver.stem.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.opengive.denver.stem.domain.Activity;
import org.opengive.denver.stem.repository.ActivityRepository;
import org.opengive.denver.stem.repository.search.ActivitySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityService {

	private final Logger log = LoggerFactory.getLogger(ActivityService.class);

	private final ActivityRepository activityRepository;

	private final ActivitySearchRepository activitySearchRepository;

	public ActivityService(final ActivityRepository activityRepository, final ActivitySearchRepository activitySearchRepository) {
		this.activityRepository = activityRepository;
		this.activitySearchRepository = activitySearchRepository;
	}

	/**
	 * Save a activity.
	 *
	 * @param activity the entity to save
	 * @return the persisted entity
	 */
	public Activity save(final Activity activity) {
		log.debug("Request to save Activity : {}", activity);
		final Activity result = activityRepository.save(activity);
		activitySearchRepository.save(result);
		return result;
	}

	/**
	 *  Get all the activities.
	 * 
	 *  @param pageable the pagination information
	 *  @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<Activity> findAll(final Pageable pageable) {
		log.debug("Request to get all Activitys");
		final Page<Activity> result = activityRepository.findAll(pageable);
		return result;
	}

	/**
	 *  Get one activity by id.
	 *
	 *  @param id the id of the entity
	 *  @return the entity
	 */
	@Transactional(readOnly = true)
	public Activity findOne(final Long id) {
		log.debug("Request to get Activity : {}", id);
		final Activity activity = activityRepository.findOne(id);
		return activity;
	}

	/**
	 *  Delete the  activity by id.
	 *
	 *  @param id the id of the entity
	 */
	public void delete(final Long id) {
		log.debug("Request to delete Activity : {}", id);
		activityRepository.delete(id);
		activitySearchRepository.delete(id);
	}

	/**
	 * Search for the activity corresponding to the query.
	 *
	 *  @param query the query of the search
	 *  @param pageable the pagination information
	 *  @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<Activity> search(final String query, final Pageable pageable) {
		log.debug("Request to search for a page of Activitys for query {}", query);
		final Page<Activity> result = activitySearchRepository.search(queryStringQuery(query), pageable);
		return result;
	}
}
