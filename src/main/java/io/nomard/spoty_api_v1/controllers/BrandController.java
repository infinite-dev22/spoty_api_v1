package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.BrandServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class BrandController {
    @Autowired
    private BrandServiceImpl brandService;


    @GetMapping("/brands")
    public List<Brand> getAll() {
        return brandService.getAll();
    }

    @PostMapping("/brand")
    public Brand getById(@RequestBody Long id) throws NotFoundException {
        return brandService.getById(id);
    }

    @PostMapping("/brands/search")
    public List<Brand> getByContains(@RequestBody String search) {
        return brandService.getByContains(search);
    }

    @PostMapping("/brand/add")
    public Brand save(@Valid @RequestBody Brand brand) {
        brand.setCreatedAt(new Date());
        return brandService.save(brand);
    }

    @PutMapping("/brand/update")
    public Brand update(@RequestBody Long id, @Valid @RequestBody Brand brand) {
        brand.setId(id);
        brand.setUpdatedAt(new Date());
        return brandService.update(id, brand);
    }

    @PostMapping("/brand/delete")
    public String delete(@RequestBody Long id) {
        return brandService.delete(id);
    }
}
