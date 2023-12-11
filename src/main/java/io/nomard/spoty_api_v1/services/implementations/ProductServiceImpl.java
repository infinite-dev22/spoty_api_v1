package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ProductRepository;
import io.nomard.spoty_api_v1.services.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    @Override
    public Product getById(Long id) throws NotFoundException {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException();
        }
        return product.get();
    }

    @Override
    public List<Product> getByContains(String search) {
        return productRepo.searchAll(search.toLowerCase());
    }

    @Override
    public List<Product> getWarning() {
        return productRepo.findAllByQuantityIsLessThanEqualStockAlert();
    }

    @Override
    public Product save(Product product) {
        product.setCreatedBy(authService.authUser());
        product.setCreatedAt(new Date());
        return productRepo.saveAndFlush(product);
    }

    @Override
    public Product update(Long id, Product product) {
        product.setUpdatedBy(authService.authUser());
        product.setUpdatedAt(new Date());
        product.setId(id);
        return productRepo.saveAndFlush(product);
    }

    @Override
    public String delete(Long id) {
        try {
            productRepo.deleteById(id);
            return "Product successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to delete product, Contact your system administrator for assistance";
        }
    }
}
