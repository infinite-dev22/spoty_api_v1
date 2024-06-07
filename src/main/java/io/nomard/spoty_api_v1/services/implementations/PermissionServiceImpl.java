package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.PermissionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Permission> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Permission> page = permissionRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Permission getById(Long id) throws NotFoundException {
        Optional<Permission> permission = permissionRepo.findById(id);
        if (permission.isEmpty()) {
            throw new NotFoundException();
        }
        return permission.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Permission permission) {
        try {
            permission.setCreatedBy(authService.authUser());
            permission.setCreatedAt(new Date());
            permissionRepo.saveAndFlush(permission);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Permission data) throws NotFoundException {
        var opt = permissionRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var permission = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            permission.setName(data.getName());
        }

        if (Objects.nonNull(data.getLabel()) && !"".equalsIgnoreCase(data.getLabel())) {
            permission.setLabel(data.getLabel());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            permission.setDescription(data.getDescription());
        }

        permission.setUpdatedBy(authService.authUser());
        permission.setUpdatedAt(new Date());

        try {
            permissionRepo.saveAndFlush(permission);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            permissionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            permissionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
