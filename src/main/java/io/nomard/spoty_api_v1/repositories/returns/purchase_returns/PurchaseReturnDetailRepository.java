package io.nomard.spoty_api_v1.repositories.returns.purchase_returns;

import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseReturnDetailRepository extends PagingAndSortingRepository<PurchaseReturnDetail, Long>, JpaRepository<PurchaseReturnDetail, Long> {
}
