package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Account;
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
    List<Account> searchAllByBankNameContainingIgnoreCaseOrAccountNameContainingIgnoreCaseOrAccountNumberContainsIgnoreCase(String bankName, String accountName, String accountNumber);

    @Query("select a from Account a where a.tenant.id = :id")
    Page<Account> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
