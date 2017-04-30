package org.opengive.denver.stem.repository;

import org.opengive.denver.stem.domain.PortfolioItem;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the PortfolioItem entity.
 */
@SuppressWarnings("unused")
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem,Long> {

    @Query("select distinct portfolioItem from PortfolioItem portfolioItem left join fetch portfolioItem.resources")
    List<PortfolioItem> findAllWithEagerRelationships();

    @Query("select portfolioItem from PortfolioItem portfolioItem left join fetch portfolioItem.resources where portfolioItem.id =:id")
    PortfolioItem findOneWithEagerRelationships(@Param("id") Long id);

}
