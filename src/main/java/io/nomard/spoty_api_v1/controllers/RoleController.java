package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.PermissionServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.RoleServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("roles")
public class RoleController {
    @Autowired
    private PermissionServiceImpl permissionService;
    @Autowired
    private RoleServiceImpl roleService;

    @GetMapping("/all")
    public Flux<PageImpl<Role>> getAllRoles(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return roleService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Role> getRolesById(@RequestBody FindModel findModel) {
        return roleService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> saveRole(@Valid @RequestBody Role role) {
        return roleService.save(role);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> updateRole(@Valid @RequestBody Role role) {
        return roleService.update(role);
    }

    @DeleteMapping("delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteRole(@RequestBody FindModel findModel) {
        return roleService.delete(findModel.getId());
    }

    @GetMapping("/permissions")
    public Flux<PageImpl<Permission>> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return permissionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/permission")
    public Mono<Permission> getDetailById(@RequestBody FindModel findModel) {
        return permissionService.getById(findModel.getId());
    }

    @PostMapping("/permission/add")
    public Mono<ResponseEntity<ObjectNode>> saveDetail(@Valid @RequestBody Permission permission) {
        return permissionService.save(permission);
    }

    @PutMapping("/permission/update")
    public Mono<ResponseEntity<ObjectNode>> updateDetail(@Valid @RequestBody Permission permission) {
        return permissionService.update(permission);
    }

    @DeleteMapping("/permission/delete")
    public Mono<ResponseEntity<ObjectNode>> deleteDetail(@RequestBody FindModel findModel) {
        return permissionService.delete(findModel.getId());
    }
}
