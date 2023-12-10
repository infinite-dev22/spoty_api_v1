package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BrandRepository;
import io.nomard.spoty_api_v1.services.interfaces.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepo;

    @Override
    public List<Brand> getAll() {
        return brandRepo.findAll();
    }

    @Override
    public Brand getById(Long id) throws NotFoundException {
        Optional<Brand> brand = brandRepo.findById(id);
        if (brand.isEmpty()) {
            throw new NotFoundException();
        }
        return brand.get();
    }

    @Override
    public List<Brand> getByContains(String search) {
        return brandRepo.searchAll(search.toLowerCase());
    }

    @Override
    public Brand save(Brand brand) {
        return brandRepo.saveAndFlush(brand);
    }

    @Override
    public Brand update(Long id, Brand brand) {
        brand.setId(id);
        return brandRepo.saveAndFlush(brand);
    }

    @Override
    public String delete(Long id) {
        try {
            brandRepo.deleteById(id);
            return "Brand successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to delete Brand, Contact your system administrator for assistance";
        }
    }
}
