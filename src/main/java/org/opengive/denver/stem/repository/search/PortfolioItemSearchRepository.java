package org.opengive.denver.stem.repository.search;

import org.opengive.denver.stem.domain.PortfolioItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PortfolioItem entity.
 */
public interface PortfolioItemSearchRepository extends ElasticsearchRepository<PortfolioItem, Long> {
}
