package org.openlearn.web.rest;

import org.openlearn.dto.OrgAdminDTO;
import org.openlearn.security.AuthoritiesConstants;
import org.openlearn.service.OrgAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/org-administrators")
public class OrgAdminResource {

	// TODO: Error handling / logging

	private static final String ENDPOINT = "/api/org-administrators/";

	private static final Logger log = LoggerFactory.getLogger(OrgAdminResource.class);

	private final OrgAdminService orgAdminService;

	public OrgAdminResource(final OrgAdminService orgAdminService) {
		this.orgAdminService = orgAdminService;
	}

	/**
	 * GET  /:id : get a single org admin user by ID
	 *
	 * @param id the ID of the org admin to get
	 * @return the ResponseEntity with status 200 (OK) and the org admin in the body
	 *      or with ... TODO: Error handling
	 */
	@GetMapping(path = "/{id}")
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORG_ADMIN})
	public ResponseEntity get(@PathVariable final Long id) {
		log.debug("GET request to get org admin : {}", id);
		OrgAdminDTO response = orgAdminService.findOne(id);
		return ResponseEntity.ok(response);
	}

	/**
	 * GET  / : get a list of all org admin users, filtered by organization
	 *
	 * @return the ResponseEntity with status 200 (OK) and a list of org admins in the body
	 *      or with ... TODO: Error handling
	 */
	@GetMapping
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORG_ADMIN})
	public ResponseEntity get() {
		log.debug("GET request for all org admins");
		List<OrgAdminDTO> response = orgAdminService.findAll();
		return ResponseEntity.ok(response);
	}

	/**
	 * POST  / : create an org admin user
	 *
	 * @param orgAdminDTO the org admin to create
	 * @return the ResponseEntity with status 200 (OK) and the created org admin in the body
	 *      or with ... TODO: Error handling
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORG_ADMIN})
	public ResponseEntity create(@RequestBody @Valid final OrgAdminDTO orgAdminDTO) throws URISyntaxException {
		log.debug("POST request to create org admin : {}", orgAdminDTO);
		OrgAdminDTO response = orgAdminService.save(orgAdminDTO);
		return ResponseEntity.created(new URI(ENDPOINT + response.getId())).body(response);
	}

	/**
	 * PUT  / : update an org admin user
	 *
	 * @param orgAdminDTO the org admin to update
	 * @return the ResponseEntity with status 200 (OK) and the updated org admin in the body
	 *      or with ... TODO: Error handling
	 */
	@PutMapping
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORG_ADMIN})
	public ResponseEntity update(@RequestBody @Valid final OrgAdminDTO orgAdminDTO) {
		log.debug("PUT request to update org admin : {}", orgAdminDTO);
		OrgAdminDTO response = orgAdminService.save(orgAdminDTO);
		return ResponseEntity.ok(response);
	}

	/**
	 * DELETE  / : delete an org admin user
	 *
	 * @param id the ID of the orf admin to delete
	 * @return the ResponseEntity with status 200 (OK)
	 *      or with ... TODO: Error handling
	 */
	@DeleteMapping(path = "/{id}")
	@Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORG_ADMIN})
	public ResponseEntity delete(@PathVariable final Long id) {
		log.debug("DELETE request to delete org admin : {}", id);
		orgAdminService.delete(id);
		return ResponseEntity.ok().build();
	}
}
