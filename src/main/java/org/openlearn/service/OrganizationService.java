package org.openlearn.service;

import org.openlearn.domain.Organization;
import org.openlearn.repository.OrganizationRepository;
import org.openlearn.repository.search.OrganizationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Organization.
 */
@Service
@Transactional
public class OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository organizationRepository;

    private final OrganizationSearchRepository organizationSearchRepository;

    public OrganizationService(OrganizationRepository organizationRepository, OrganizationSearchRepository organizationSearchRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationSearchRepository = organizationSearchRepository;
    }

    /**
     * Save a organization.
     *
     * @param organization the entity to save
     * @return the persisted entity
     */
    public Organization save(Organization organization) {
        log.debug("Request to save Organization : {}", organization);
        Organization result = organizationRepository.save(organization);
        organizationSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the organizations.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Organization> findAll(Pageable pageable) {
        log.debug("Request to get all Organizations");
        Page<Organization> result = organizationRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one organization by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Organization findOne(Long id) {
        log.debug("Request to get Organization : {}", id);
        Organization organization = organizationRepository.findOne(id);
        return organization;
    }

    /**
     *  Delete the  organization by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Organization : {}", id);
        organizationRepository.delete(id);
        organizationSearchRepository.delete(id);
    }

    /**
     * Search for the organization corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Organization> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Organizations for query {}", query);
        Page<Organization> result = organizationSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
