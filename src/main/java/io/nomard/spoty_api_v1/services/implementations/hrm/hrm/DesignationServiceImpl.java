package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.DesignationRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DesignationServiceImpl implements DesignationService {
    @Autowired
    private DesignationRepository designationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Designation> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Designation> page = designationRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Designation getById(Long id) throws NotFoundException {
        Optional<Designation> designation = designationRepo.findById(id);
        if (designation.isEmpty()) {
            throw new NotFoundException();
        }
        return designation.get();
    }

    @Override
    public ArrayList<Designation> getByContains(String search) {
        return designationRepo.searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search,
                search
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(Designation designation) {
        try {
            designation.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(designation.getBranch())) {
                designation.setBranch(authService.authUser().getBranch());
            }
            designation.setCreatedBy(authService.authUser());
            designation.setCreatedAt(new Date());
            designationRepo.saveAndFlush(designation);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Designation data) throws NotFoundException {
        var opt = designationRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var designation = opt.get();

        if (Objects.nonNull(data.getBranch())) {
            designation.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            designation.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            designation.setDescription(data.getDescription());
        }

        designation.setUpdatedBy(authService.authUser());
        designation.setUpdatedAt(new Date());

        try {
            designationRepo.saveAndFlush(designation);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            designationRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            designationRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}