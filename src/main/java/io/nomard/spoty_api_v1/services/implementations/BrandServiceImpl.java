package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BrandRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Brand> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Brand> page = brandRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
        return brandRepo.searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Brand brand) {
        try {
            brand.setCreatedBy(authService.authUser());
            brand.setCreatedAt(new Date());
            brandRepo.saveAndFlush(brand);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Brand data) throws NotFoundException {
        var opt = brandRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var brand = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            brand.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            brand.setDescription(data.getDescription());
        }

        if (Objects.nonNull(data.getImage()) && !"".equalsIgnoreCase(data.getImage())) {
            brand.setImage(data.getImage());
        }

        brand.setUpdatedBy(authService.authUser());
        brand.setUpdatedAt(new Date());

        try {
            brandRepo.saveAndFlush(brand);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            brandRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            brandRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
