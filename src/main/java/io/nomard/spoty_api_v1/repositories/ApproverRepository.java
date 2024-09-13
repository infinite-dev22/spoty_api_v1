package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Approver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ApproverRepository extends JpaRepository<Approver, Long> {
    @Query("select p from Approver p where p.tenant.id = :id")
    ArrayList<Approver> findAllByTenantId(@Param("id") Long id);

    @Query("select p from Approver p where p.employee.id = :id")
    Optional<Approver> findByUserId(@Param("id") Long id);
}
