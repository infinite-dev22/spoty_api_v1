package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ProductCategoryRepository;
import io.nomard.spoty_api_v1.services.interfaces.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ProductCategory save(ProductCategory productCategory) {
        productCategory.setCreatedBy(authService.authUser());
        productCategory.setCreatedAt(new Date());
        return productCategoryRepo.saveAndFlush(productCategory);
    }

    @Override
    public ProductCategory update(Long id, ProductCategory productCategory) {
        productCategory.setId(id);
        productCategory.setUpdatedBy(authService.authUser());
        productCategory.setUpdatedAt(new Date());
        return productCategoryRepo.saveAndFlush(productCategory);
    }

    @Override
    public String delete(Long id) {
        try {
            productCategoryRepo.deleteById(id);
            return "ProductCategory successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to delete ProductCategory, Contact your system administrator for assistance";
        }
    }
}
