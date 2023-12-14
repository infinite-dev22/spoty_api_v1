package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ProductCategoryRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductCategoryRepository productCategoryRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<ProductCategory> getAll() {
        return productCategoryRepo.findAll();
    }

    @Override
    public ProductCategory getById(Long id) throws NotFoundException {
        Optional<ProductCategory> productCategory = productCategoryRepo.findById(id);
        if (productCategory.isEmpty()) {
            throw new NotFoundException();
        }
        return productCategory.get();
    }

    @Override
    public List<ProductCategory> getByContains(String search) {
        return productCategoryRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(ProductCategory productCategory) {
        try {
            productCategory.setCreatedBy(authService.authUser());
            productCategory.setCreatedAt(new Date());
            productCategoryRepo.saveAndFlush(productCategory);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, ProductCategory productCategory) {
        try {
            productCategory.setId(id);
            productCategory.setUpdatedBy(authService.authUser());
            productCategory.setUpdatedAt(new Date());
            productCategoryRepo.saveAndFlush(productCategory);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            productCategoryRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
