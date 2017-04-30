package org.opengive.denver.stem.repository.search;

import org.opengive.denver.stem.domain.Session;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Session entity.
 */
public interface SessionSearchRepository extends ElasticsearchRepository<Session, Long> {
}
