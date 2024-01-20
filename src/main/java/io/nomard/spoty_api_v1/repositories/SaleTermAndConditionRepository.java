package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleTermAndConditionRepository extends PagingAndSortingRepository<SaleTermAndCondition, Long>, JpaRepository<SaleTermAndCondition, Long> {
}
