package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.PermissionRepository;
import io.nomard.spoty_api_v1.repositories.RoleRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PermissionRepository permissionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Role> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        Page<Role> page = roleRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        return page.getContent();
    }

    @Override
    public Role getById(Long id) throws NotFoundException {
        Optional<Role> role = roleRepo.findById(id);
        if (role.isEmpty()) {
            throw new NotFoundException();
        }
        return role.get();
    }

    @Override
    public List<Role> search(String search) {
        return roleRepo.searchAllByNameContainingIgnoreCase(search);
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Role role) {
        try {
            // Fetch permissions based on IDs
            List<Permission> permissions = permissionRepo.findAllById(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));
            role.setPermissions(permissions); // Set the fetched permissions
            role.setTenant(authService.authUser().getTenant());
            role.setCreatedBy(authService.authUser());
            role.setCreatedAt(new Date());
            roleRepo.saveAndFlush(role);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Role data) throws NotFoundException {
        var opt = roleRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var role = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            role.setName(data.getName());
        }

        if (Objects.nonNull(data.getLabel()) && !"".equalsIgnoreCase(data.getLabel())) {
            role.setLabel(data.getLabel());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            role.setDescription(data.getDescription());
        }

        if (Objects.nonNull(data.getPermissions()) && !data.getPermissions().isEmpty()) {
            // Fetch permissions based on IDs
            List<Permission> permissions = permissionRepo.findAllById(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));
            role.setPermissions(permissions); // Set the fetched permissions
        }

        role.setUpdatedBy(authService.authUser());
        role.setUpdatedAt(new Date());

        try {
            roleRepo.saveAndFlush(role);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            roleRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            roleRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
