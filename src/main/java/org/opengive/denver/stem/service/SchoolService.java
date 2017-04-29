package org.opengive.denver.stem.service;

import org.opengive.denver.stem.domain.School;
import org.opengive.denver.stem.repository.SchoolRepository;
import org.opengive.denver.stem.repository.search.SchoolSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing School.
 */
@Service
@Transactional
public class SchoolService {

    private final Logger log = LoggerFactory.getLogger(SchoolService.class);
    
    private final SchoolRepository schoolRepository;

    private final SchoolSearchRepository schoolSearchRepository;

    public SchoolService(SchoolRepository schoolRepository, SchoolSearchRepository schoolSearchRepository) {
        this.schoolRepository = schoolRepository;
        this.schoolSearchRepository = schoolSearchRepository;
    }

    /**
     * Save a school.
     *
     * @param school the entity to save
     * @return the persisted entity
     */
    public School save(School school) {
        log.debug("Request to save School : {}", school);
        School result = schoolRepository.save(school);
        schoolSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the schools.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<School> findAll() {
        log.debug("Request to get all Schools");
        List<School> result = schoolRepository.findAll();

        return result;
    }

    /**
     *  Get one school by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public School findOne(Long id) {
        log.debug("Request to get School : {}", id);
        School school = schoolRepository.findOne(id);
        return school;
    }

    /**
     *  Delete the  school by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete School : {}", id);
        schoolRepository.delete(id);
        schoolSearchRepository.delete(id);
    }

    /**
     * Search for the school corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<School> search(String query) {
        log.debug("Request to search Schools for query {}", query);
        return StreamSupport
            .stream(schoolSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
