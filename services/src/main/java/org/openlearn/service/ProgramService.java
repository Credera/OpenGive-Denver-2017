package org.openlearn.service;

import org.openlearn.domain.Program;
import org.openlearn.domain.User;
import org.openlearn.dto.ProgramDTO;
import org.openlearn.repository.ProgramRepository;
import org.openlearn.repository.SessionRepository;
import org.openlearn.security.SecurityUtils;
import org.openlearn.transformer.ProgramTransformer;
import org.openlearn.web.rest.errors.ItemHasChildrenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Program.
 */
@Service
@Transactional
public class ProgramService {

	private static final Logger log = LoggerFactory.getLogger(ProgramService.class);

	private final ProgramRepository programRepository;

	private final ProgramTransformer programTransformer;

	private final UserService userService;

	private final SessionRepository sessionRepository;

	public ProgramService(final ProgramRepository programRepository,
						  final ProgramTransformer programTransformer,
	                      final UserService userService,
						  final SessionRepository sessionRepository) {
		this.programRepository = programRepository;
		this.programTransformer = programTransformer;
		this.userService = userService;
		this.sessionRepository = sessionRepository;
	}

	/**
	 * Save a program.
	 *
	 * @param programDTO the entity to save
	 * @return the persisted entity
	 */
	public ProgramDTO save(final ProgramDTO programDTO) {
		log.debug("Request to save Program : {}", programDTO);
		if (SecurityUtils.isAdmin() || inOrgOfCurrentUser(programDTO)) {
			return programTransformer.transform(programRepository.save(programTransformer.transform(programDTO)));
		}
		// TODO: Error handling / logging
		return null;
	}

	/**
	 * Get all the programs.
	 *
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public List<ProgramDTO> findAll() {
		log.debug("Request to get all Programs");
		User user = userService.getCurrentUser();
		if (SecurityUtils.isAdmin()) {
			return programRepository.findAll()
				.stream()
				.map(programTransformer::transform)
				.collect(Collectors.toList());
		} else {
			return programRepository.findByOrganization(user.getOrganization())
				.stream()
				.map(programTransformer::transform)
				.collect(Collectors.toList());
		}
	}

	/**
	 * Get one program by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public ProgramDTO findOne(final Long id) {
		log.debug("Request to get Program : {}", id);
		Program program = programRepository.findOne(id);
		if (program != null && (SecurityUtils.isAdmin() || inOrgOfCurrentUser(program))) {
			return programTransformer.transform(program);
		}
		// TODO: Error handling / logging
		return null;
	}

	/**
	 * Delete the program by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(final Long id) {
		log.debug("Request to delete Program : {}", id);
		Program program = programRepository.findOne(id);
		if (program != null && (SecurityUtils.isAdmin() || inOrgOfCurrentUser(program))) {

			if (sessionRepository.existsByProgram(program)) {
				throw new ItemHasChildrenException("Before you delete this program, please remove all sessions from the program.");
			}

			programRepository.delete(id);
		} else {
			// TODO: Error handling / logging
		}
	}

	private boolean inOrgOfCurrentUser(final ProgramDTO programDTO) {
		User user = userService.getCurrentUser();
		return user.getOrganization().getId().equals(programDTO.getOrganizationId());
	}

	private boolean inOrgOfCurrentUser(final Program program) {
		User user = userService.getCurrentUser();
		return user.getOrganization().equals(program.getOrganization());
	}
}
