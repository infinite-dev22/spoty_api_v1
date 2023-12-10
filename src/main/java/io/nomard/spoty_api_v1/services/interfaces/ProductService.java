package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;

import java.util.List;

public interface ProductService {
    List<Product> getAll();

    Product getById(Long id) throws NotFoundException;

    List<Product> getByContains(String search);

    List<Product> getWarning();

    Product save(Product product);

    Product update(Long id, Product product);

    String delete(Long id);
}
