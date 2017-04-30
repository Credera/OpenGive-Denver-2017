package org.opengive.denver.stem.repository.search;

import org.opengive.denver.stem.domain.School;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the School entity.
 */
public interface SchoolSearchRepository extends ElasticsearchRepository<School, Long> {
}
