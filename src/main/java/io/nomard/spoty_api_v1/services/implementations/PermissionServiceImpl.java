package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PermissionDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.PermissionMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.PermissionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Page<PermissionDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return permissionRepo.findAll(pageRequest).map(permission -> permissionMapper.toDTO(permission));
    }

    @Override
    public PermissionDTO getById(Long id) throws NotFoundException {
        Optional<Permission> permission = permissionRepo.findById(id);
        if (permission.isEmpty()) {
            throw new NotFoundException();
        }
        return permissionMapper.toDTO(permission.get());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Permission permission) {
        try {
            permission.setCreatedBy(authService.authUser());
            permission.setCreatedAt(LocalDateTime.now());
            permissionRepo.save(permission);
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
        permission.setUpdatedAt(LocalDateTime.now());

        try {
            permissionRepo.save(permission);
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
