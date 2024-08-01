package io.nomard.spoty_api_v1.repositories.accounting;

import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<Account, Long>, JpaRepository<Account, Long> {
    @Query("SELECT ac FROM Account ac WHERE ac.tenant.id = :tenantId " +
            "AND CONCAT(" +
            "TRIM(LOWER(ac.accountName))," +
            "TRIM(LOWER(ac.accountNumber))) LIKE %:search%")
    List<Account> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from Account p where p.tenant.id = :id")
    Page<Account> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    Account findByTenantAndAccountNameContainingIgnoreCase(Tenant tenant, String accountName);
}
