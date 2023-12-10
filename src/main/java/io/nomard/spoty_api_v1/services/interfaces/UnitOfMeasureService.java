package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;

import java.util.List;

public interface UnitOfMeasureService {
    List<UnitOfMeasure> getAll();

    UnitOfMeasure getById(Long id) throws NotFoundException;

    List<UnitOfMeasure> getByContains(String search);

    UnitOfMeasure save(UnitOfMeasure user);

    UnitOfMeasure update(Long id, UnitOfMeasure user);

    String delete(Long id);
}
