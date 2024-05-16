package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface BeneficiaryTypeRepository extends PagingAndSortingRepository<BeneficiaryType, Long>, JpaRepository<BeneficiaryType, Long> {
    ArrayList<BeneficiaryType> searchAllByNameContainingIgnoreCaseOrColorContainingIgnoreCase(String name, String color);
}
