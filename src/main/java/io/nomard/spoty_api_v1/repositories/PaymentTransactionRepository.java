package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends PagingAndSortingRepository<PaymentTransaction, Long>, JpaRepository<PaymentTransaction, Long> {

    @Query("select p from PaymentTransaction p where p.tenant.id = :id")
    Page<PaymentTransaction> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
