package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends PagingAndSortingRepository<Branch, Long>, JpaRepository<Branch, Long> {
//    @Query("select b from Branch b where lower(concat(lower(b.email), lower(b.name), lower(b.city),lower(b.town),lower(b.phone))) like concat('%', :search, '%')")
//    List<Branch> searchAll(@Param("search") String search);

    List<Branch> searchAllByEmailContainingIgnoreCaseOrNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrTownContainingIgnoreCaseOrPhoneContainingIgnoreCase(String email, String name, String city, String town, String phone);
}
