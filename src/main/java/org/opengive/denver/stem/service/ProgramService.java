package org.opengive.denver.stem.service;

import org.opengive.denver.stem.domain.Program;
import org.opengive.denver.stem.repository.ProgramRepository;
import org.opengive.denver.stem.repository.search.ProgramSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Program.
 */
@Service
@Transactional
public class ProgramService {

    private final Logger log = LoggerFactory.getLogger(ProgramService.class);
    
    private final ProgramRepository programRepository;

    private final ProgramSearchRepository programSearchRepository;

    public ProgramService(ProgramRepository programRepository, ProgramSearchRepository programSearchRepository) {
        this.programRepository = programRepository;
        this.programSearchRepository = programSearchRepository;
    }

    /**
     * Save a program.
     *
     * @param program the entity to save
     * @return the persisted entity
     */
    public Program save(Program program) {
        log.debug("Request to save Program : {}", program);
        Program result = programRepository.save(program);
        programSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the programs.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Program> findAll() {
        log.debug("Request to get all Programs");
        List<Program> result = programRepository.findAll();

        return result;
    }

    /**
     *  Get one program by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Program findOne(Long id) {
        log.debug("Request to get Program : {}", id);
        Program program = programRepository.findOne(id);
        return program;
    }

    /**
     *  Delete the  program by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Program : {}", id);
        programRepository.delete(id);
        programSearchRepository.delete(id);
    }

    /**
     * Search for the program corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Program> search(String query) {
        log.debug("Request to search Programs for query {}", query);
        return StreamSupport
            .stream(programSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
