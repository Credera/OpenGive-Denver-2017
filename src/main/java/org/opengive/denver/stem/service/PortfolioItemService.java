package org.opengive.denver.stem.service;

import org.opengive.denver.stem.domain.PortfolioItem;
import org.opengive.denver.stem.repository.PortfolioItemRepository;
import org.opengive.denver.stem.repository.search.PortfolioItemSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PortfolioItem.
 */
@Service
@Transactional
public class PortfolioItemService {

    private final Logger log = LoggerFactory.getLogger(PortfolioItemService.class);
    
    private final PortfolioItemRepository portfolioItemRepository;

    private final PortfolioItemSearchRepository portfolioItemSearchRepository;

    public PortfolioItemService(PortfolioItemRepository portfolioItemRepository, PortfolioItemSearchRepository portfolioItemSearchRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.portfolioItemSearchRepository = portfolioItemSearchRepository;
    }

    /**
     * Save a portfolioItem.
     *
     * @param portfolioItem the entity to save
     * @return the persisted entity
     */
    public PortfolioItem save(PortfolioItem portfolioItem) {
        log.debug("Request to save PortfolioItem : {}", portfolioItem);
        PortfolioItem result = portfolioItemRepository.save(portfolioItem);
        portfolioItemSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the portfolioItems.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PortfolioItem> findAll(Pageable pageable) {
        log.debug("Request to get all PortfolioItems");
        Page<PortfolioItem> result = portfolioItemRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one portfolioItem by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public PortfolioItem findOne(Long id) {
        log.debug("Request to get PortfolioItem : {}", id);
        PortfolioItem portfolioItem = portfolioItemRepository.findOneWithEagerRelationships(id);
        return portfolioItem;
    }

    /**
     *  Delete the  portfolioItem by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PortfolioItem : {}", id);
        portfolioItemRepository.delete(id);
        portfolioItemSearchRepository.delete(id);
    }

    /**
     * Search for the portfolioItem corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PortfolioItem> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PortfolioItems for query {}", query);
        Page<PortfolioItem> result = portfolioItemSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
