package io.nomard.spoty_api_v1.repositories.accounting;

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
    List<Account> searchAllByAccountNameContainingIgnoreCaseOrAccountNumberContainsIgnoreCase(String accountName, String accountNumber);

    @Query("select p from Account p where p.tenant.id = :id")
    Page<Account> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
