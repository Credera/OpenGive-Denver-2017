package org.opengive.denver.stem.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.opengive.denver.stem.domain.ItemLink;
import org.opengive.denver.stem.repository.ItemLinkRepository;
import org.opengive.denver.stem.repository.search.ItemLinkSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing ItemLink.
 */
@Service
@Transactional
public class ItemLinkService {

	private final Logger log = LoggerFactory.getLogger(ItemLinkService.class);

	private final ItemLinkRepository itemLinkRepository;

	private final ItemLinkSearchRepository itemLinkSearchRepository;

	public ItemLinkService(final ItemLinkRepository itemLinkRepository, final ItemLinkSearchRepository itemLinkSearchRepository) {
		this.itemLinkRepository = itemLinkRepository;
		this.itemLinkSearchRepository = itemLinkSearchRepository;
	}

	/**
	 * Save a itemLink.
	 *
	 * @param itemLink the entity to save
	 * @return the persisted entity
	 */
	public ItemLink save(final ItemLink itemLink) {
		log.debug("Request to save ItemLink : {}", itemLink);
		final ItemLink result = itemLinkRepository.save(itemLink);
		itemLinkSearchRepository.save(result);
		return result;
	}

	/**
	 *  Get all the itemLinks.
	 * 
	 *  @param pageable the pagination information
	 *  @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<ItemLink> findAll(final Pageable pageable) {
		log.debug("Request to get all ItemLinks");
		final Page<ItemLink> result = itemLinkRepository.findAll(pageable);
		return result;
	}

	/**
	 *  Get one itemLink by id.
	 *
	 *  @param id the id of the entity
	 *  @return the entity
	 */
	@Transactional(readOnly = true)
	public ItemLink findOne(final Long id) {
		log.debug("Request to get ItemLink : {}", id);
		final ItemLink itemLink = itemLinkRepository.findOne(id);
		return itemLink;
	}

	/**
	 *  Delete the  itemLink by id.
	 *
	 *  @param id the id of the entity
	 */
	public void delete(final Long id) {
		log.debug("Request to delete ItemLink : {}", id);
		itemLinkRepository.delete(id);
		itemLinkSearchRepository.delete(id);
	}

	/**
	 * Search for the itemLink corresponding to the query.
	 *
	 *  @param query the query of the search
	 *  @param pageable the pagination information
	 *  @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<ItemLink> search(final String query, final Pageable pageable) {
		log.debug("Request to search for a page of ItemLinks for query {}", query);
		final Page<ItemLink> result = itemLinkSearchRepository.search(queryStringQuery(query), pageable);
		return result;
	}
}
