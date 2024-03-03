package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleTerm;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleTermRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleTermServiceImpl implements SaleTermService {
    @Autowired
    private SaleTermRepository saleTermRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("sale_terms")
    @Transactional(readOnly = true)
    public List<SaleTerm> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<SaleTerm> page = saleTermRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    @Cacheable("sale_terms")
    @Transactional(readOnly = true)
    public SaleTerm getById(Long id) throws NotFoundException {
        Optional<SaleTerm> saleTerm = saleTermRepo.findById(id);
        if (saleTerm.isEmpty()) {
            throw new NotFoundException();
        }
        return saleTerm.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(SaleTerm saleTerm) {
        try {
            saleTerm.setCreatedBy(authService.authUser());
            saleTerm.setCreatedAt(new Date());
            saleTermRepo.saveAndFlush(saleTerm);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Cacheable("sale_terms")
    @Transactional(readOnly = true)
    public List<SaleTerm> getByContains(String search) {
        return saleTermRepo.searchAllByNameContainingIgnoreCase(search.toLowerCase());
    }

    @Override
    @CacheEvict(value = "sale_terms", key = "#data.id")
    public ResponseEntity<ObjectNode> update(SaleTerm data) throws NotFoundException {
        var opt = saleTermRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleTerm = opt.get();

        if (!Objects.equals(data.getName(), saleTerm.getName()) && !"".equalsIgnoreCase(data.getName())) {
            saleTerm.setName(data.getName());
        }

        if (!Objects.equals(data.isActive(), saleTerm.isActive())) {
            saleTerm.setActive(data.isActive());
        }

        if (!Objects.equals(data.getDescription(), saleTerm.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            saleTerm.setDescription(data.getDescription());
        }

        saleTerm.setUpdatedBy(authService.authUser());
        saleTerm.setUpdatedAt(new Date());

        try {
            saleTermRepo.saveAndFlush(saleTerm);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "sale_terms", key = "#id")
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleTermRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        return null;
    }
}
