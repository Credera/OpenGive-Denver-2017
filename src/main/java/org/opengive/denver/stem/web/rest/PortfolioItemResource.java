package org.opengive.denver.stem.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.opengive.denver.stem.domain.PortfolioItem;
import org.opengive.denver.stem.service.PortfolioItemService;
import org.opengive.denver.stem.web.rest.util.HeaderUtil;
import org.opengive.denver.stem.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing PortfolioItem.
 */
@RestController
@RequestMapping("/api")
public class PortfolioItemResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioItemResource.class);

    private static final String ENTITY_NAME = "portfolioItem";
        
    private final PortfolioItemService portfolioItemService;

    public PortfolioItemResource(PortfolioItemService portfolioItemService) {
        this.portfolioItemService = portfolioItemService;
    }

    /**
     * POST  /portfolio-items : Create a new portfolioItem.
     *
     * @param portfolioItem the portfolioItem to create
     * @return the ResponseEntity with status 201 (Created) and with body the new portfolioItem, or with status 400 (Bad Request) if the portfolioItem has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/portfolio-items")
    @Timed
    public ResponseEntity<PortfolioItem> createPortfolioItem(@Valid @RequestBody PortfolioItem portfolioItem) throws URISyntaxException {
        log.debug("REST request to save PortfolioItem : {}", portfolioItem);
        if (portfolioItem.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new portfolioItem cannot already have an ID")).body(null);
        }
        PortfolioItem result = portfolioItemService.save(portfolioItem);
        return ResponseEntity.created(new URI("/api/portfolio-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /portfolio-items : Updates an existing portfolioItem.
     *
     * @param portfolioItem the portfolioItem to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated portfolioItem,
     * or with status 400 (Bad Request) if the portfolioItem is not valid,
     * or with status 500 (Internal Server Error) if the portfolioItem couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/portfolio-items")
    @Timed
    public ResponseEntity<PortfolioItem> updatePortfolioItem(@Valid @RequestBody PortfolioItem portfolioItem) throws URISyntaxException {
        log.debug("REST request to update PortfolioItem : {}", portfolioItem);
        if (portfolioItem.getId() == null) {
            return createPortfolioItem(portfolioItem);
        }
        PortfolioItem result = portfolioItemService.save(portfolioItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, portfolioItem.getId().toString()))
            .body(result);
    }

    /**
     * GET  /portfolio-items : get all the portfolioItems.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of portfolioItems in body
     */
    @GetMapping("/portfolio-items")
    @Timed
    public ResponseEntity<List<PortfolioItem>> getAllPortfolioItems(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of PortfolioItems");
        Page<PortfolioItem> page = portfolioItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/portfolio-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /portfolio-items/:id : get the "id" portfolioItem.
     *
     * @param id the id of the portfolioItem to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the portfolioItem, or with status 404 (Not Found)
     */
    @GetMapping("/portfolio-items/{id}")
    @Timed
    public ResponseEntity<PortfolioItem> getPortfolioItem(@PathVariable Long id) {
        log.debug("REST request to get PortfolioItem : {}", id);
        PortfolioItem portfolioItem = portfolioItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(portfolioItem));
    }

    /**
     * DELETE  /portfolio-items/:id : delete the "id" portfolioItem.
     *
     * @param id the id of the portfolioItem to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/portfolio-items/{id}")
    @Timed
    public ResponseEntity<Void> deletePortfolioItem(@PathVariable Long id) {
        log.debug("REST request to delete PortfolioItem : {}", id);
        portfolioItemService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/portfolio-items?query=:query : search for the portfolioItem corresponding
     * to the query.
     *
     * @param query the query of the portfolioItem search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/portfolio-items")
    @Timed
    public ResponseEntity<List<PortfolioItem>> searchPortfolioItems(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of PortfolioItems for query {}", query);
        Page<PortfolioItem> page = portfolioItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/portfolio-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
