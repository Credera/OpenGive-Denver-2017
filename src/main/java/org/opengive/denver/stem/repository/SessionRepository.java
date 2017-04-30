package org.opengive.denver.stem.repository;

import org.opengive.denver.stem.domain.Session;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Session entity.
 */
@SuppressWarnings("unused")
public interface SessionRepository extends JpaRepository<Session,Long> {

    @Query("select distinct session from Session session left join fetch session.programs")
    List<Session> findAllWithEagerRelationships();

    @Query("select session from Session session left join fetch session.programs where session.id =:id")
    Session findOneWithEagerRelationships(@Param("id") Long id);

}
