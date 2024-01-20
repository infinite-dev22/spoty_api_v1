package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitOfMeasureRepository extends PagingAndSortingRepository<UnitOfMeasure, Long>, JpaRepository<UnitOfMeasure, Long> {
    List<UnitOfMeasure> searchAllByNameContainingIgnoreCaseOrShortNameContainingIgnoreCase(String name, String shortName);
}
