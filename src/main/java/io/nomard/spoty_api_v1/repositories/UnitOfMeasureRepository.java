package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long> {
    @Query("select uOM from UnitOfMeasure uOM where concat(" +
            "trim(lower(uOM.name))," +
            "trim(lower(uOM.shortName))) like %:search%")
    List<UnitOfMeasure> searchAll(String search);
}
