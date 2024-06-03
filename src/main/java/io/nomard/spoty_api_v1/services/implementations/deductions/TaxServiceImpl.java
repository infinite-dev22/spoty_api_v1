package io.nomard.spoty_api_v1.services.implementations.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.deductions.TaxRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.deductions.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaxServiceImpl implements TaxService {
    @Autowired
    private TaxRepository taxRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Tax> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Tax> page = taxRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Tax getById(Long id) throws NotFoundException {
        Optional<Tax> tax = taxRepo.findById(id);
        if (tax.isEmpty()) {
            throw new NotFoundException();
        }
        return tax.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(Tax tax) {
        try {
            tax.setTenant(authService.authUser().getTenant());
            tax.setBranch(authService.authUser().getBranch());
            tax.setCreatedBy(authService.authUser());
            tax.setCreatedAt(new Date());
            taxRepo.saveAndFlush(tax);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Tax data) throws NotFoundException {
        var opt = taxRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var tax = opt.get();
        if (!Objects.equals(tax.getName(), data.getName()) && Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            tax.setName(data.getName());
        }
        if (!Objects.equals(tax.getPercentage(), data.getPercentage())) {
            tax.setPercentage(data.getPercentage());
        }
        tax.setUpdatedBy(authService.authUser());
        tax.setUpdatedAt(new Date());
        try {
            taxRepo.saveAndFlush(tax);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            taxRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            taxRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
