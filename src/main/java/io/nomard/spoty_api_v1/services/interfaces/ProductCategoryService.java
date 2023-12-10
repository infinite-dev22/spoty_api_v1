package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategory> getAll();

    ProductCategory getById(Long id) throws NotFoundException;

    List<ProductCategory> getByContains(String search);

    ProductCategory save(ProductCategory productCategory);

    ProductCategory update(Long id, ProductCategory productCategory);

    String delete(Long id);
}
