package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ApproverRepository extends JpaRepository<Reviewer, Long> {
    @Query("select p from Reviewer p where p.tenant.id = :id")
    ArrayList<Reviewer> findAllByTenantId(@Param("id") Long id);

    @Query("select p from Reviewer p where p.employee.id = :id")
    Optional<Reviewer> findByUserId(@Param("id") Long id);
}
