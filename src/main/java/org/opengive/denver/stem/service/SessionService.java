package org.opengive.denver.stem.service;

import org.opengive.denver.stem.domain.Session;
import org.opengive.denver.stem.repository.SessionRepository;
import org.opengive.denver.stem.repository.search.SessionSearchRepository;
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
 * Service Implementation for managing Session.
 */
@Service
@Transactional
public class SessionService {

    private final Logger log = LoggerFactory.getLogger(SessionService.class);
    
    private final SessionRepository sessionRepository;

    private final SessionSearchRepository sessionSearchRepository;

    public SessionService(SessionRepository sessionRepository, SessionSearchRepository sessionSearchRepository) {
        this.sessionRepository = sessionRepository;
        this.sessionSearchRepository = sessionSearchRepository;
    }

    /**
     * Save a session.
     *
     * @param session the entity to save
     * @return the persisted entity
     */
    public Session save(Session session) {
        log.debug("Request to save Session : {}", session);
        Session result = sessionRepository.save(session);
        sessionSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the sessions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Session> findAll(Pageable pageable) {
        log.debug("Request to get all Sessions");
        Page<Session> result = sessionRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one session by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Session findOne(Long id) {
        log.debug("Request to get Session : {}", id);
        Session session = sessionRepository.findOneWithEagerRelationships(id);
        return session;
    }

    /**
     *  Delete the  session by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Session : {}", id);
        sessionRepository.delete(id);
        sessionSearchRepository.delete(id);
    }

    /**
     * Search for the session corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Session> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sessions for query {}", query);
        Page<Session> result = sessionSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
