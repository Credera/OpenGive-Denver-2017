package org.openlearn.service;

import org.openlearn.domain.Authority;
import org.openlearn.domain.User;
import org.openlearn.dto.InstructorDTO;
import org.openlearn.security.AuthoritiesConstants;
import org.openlearn.security.SecurityUtils;
import org.openlearn.transformer.InstructorTransformer;
import org.openlearn.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing instructor users.
 */
@Service
@Transactional
public class InstructorService {

	private static final Authority INSTRUCTOR = new Authority(AuthoritiesConstants.INSTRUCTOR);

	private final Logger log = LoggerFactory.getLogger(InstructorService.class);

	private final InstructorTransformer instructorTransformer;

	private final UserRepository userRepository;

	private final UserService userService;

	public InstructorService(InstructorTransformer instructorTransformer, UserRepository userRepository,
	                      UserService userService) {
		this.instructorTransformer = instructorTransformer;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	/**
	 * Save an instructor user.
	 *
	 * @param instructorDTO the entity to save
	 * @return the persisted entity
	 */
	public InstructorDTO save(InstructorDTO instructorDTO) {
		log.debug("Request to save instructor : {}", instructorDTO);
		if (AuthoritiesConstants.INSTRUCTOR.equals(instructorDTO.getAuthority())
			&& (SecurityUtils.isAdmin() || inOrgOfCurrentUser(instructorDTO))) {
			return instructorTransformer.transform(userRepository.save(instructorTransformer.transform(instructorDTO)));
		}
		// TODO: Error handling / logging
		return null;
	}

	/**
	 * Get all the instructor users.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<InstructorDTO> findAll(Pageable pageable) {
		log.debug("Request to get all instructor users");
		User user = userService.getCurrentUser();
		if (SecurityUtils.isAdmin()) {
			return userRepository.findAllByAuthority(INSTRUCTOR, pageable).map(instructorTransformer::transform);
		} else {
			return userRepository.findAllByOrganizationAndAuthority(user.getOrganization(), INSTRUCTOR, pageable)
				.map(instructorTransformer::transform);
		}
	}

	/**
	 * Get one user by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public InstructorDTO findOne(Long id) {
		log.debug("Request to get instructor : {}", id);
		User instructor = userRepository.findOneByIdAndAuthority(id, INSTRUCTOR);
		if (instructor != null && (SecurityUtils.isAdmin() || inOrgOfCurrentUser(instructor))) {
			return instructorTransformer.transform(instructor);
		}
		// TODO: Error handling / logging
		return null;
	}

	/**
	 * Delete the user by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete instructor : {}", id);
		User instructor = userRepository.findOneByIdAndAuthority(id, INSTRUCTOR);
		if (instructor != null && (SecurityUtils.isAdmin() || inOrgOfCurrentUser(instructor))) {
			userRepository.delete(id);
		} else {
			// TODO: Error handling / logging
		}
	}

	/**
	 * Determines if an instructor is in the organization of current user
	 *
	 * @param instructorDTO the instructor
	 * @return true if instructor and current user are in the same org
	 */
	private boolean inOrgOfCurrentUser(InstructorDTO instructorDTO) {
		User user = userService.getCurrentUser();
		return user.getOrganization().getId().equals(instructorDTO.getOrganizationId());
	}

	/**
	 * Determines if an instructor is in the organization of current user
	 *
	 * @param instructor the instructor
	 * @return true if instructor and current user are in the same org
	 */
	private boolean inOrgOfCurrentUser(User instructor) {
		User user = userService.getCurrentUser();
		return user.getOrganization().equals(instructor.getOrganization());
	}
}