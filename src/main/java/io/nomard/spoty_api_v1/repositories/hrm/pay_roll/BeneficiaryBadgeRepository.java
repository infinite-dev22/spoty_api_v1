package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryBadgeRepository extends PagingAndSortingRepository<BeneficiaryBadge, Long>, JpaRepository<BeneficiaryBadge, Long> {
    List<BeneficiaryBadge> searchAllByNameContainingIgnoreCaseOrColorContainingIgnoreCase(String name, String color);
}
