package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;

import java.util.List;

public interface BrandService {
    List<Brand> getAll();

    Brand getById(Long id) throws NotFoundException;

    List<Brand> getByContains(String search);

    Brand save(Brand brand);

    Brand update(Long id, Brand brand);

    String delete(Long id);
}
