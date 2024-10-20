package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.PermissionServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.RoleServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("roles")
public class RoleController {
    @Autowired
    private PermissionServiceImpl permissionService;
    @Autowired
    private RoleServiceImpl roleService;

    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<Role> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return roleService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Role getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return roleService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody Role role) {
        return roleService.save(role);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody Role role) throws NotFoundException {
        return roleService.update(role);
    }

    @DeleteMapping("delete/multiple")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return roleService.delete(findModel.getId());
    }

    @GetMapping("/permissions")
    public Page<Permission> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return permissionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/permission")
    public Permission getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return permissionService.getById(findModel.getId());
    }

    @PostMapping("/permission/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody Permission permission) {
        return permissionService.save(permission);
    }

    @PutMapping("/permission/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody Permission permission) throws NotFoundException {
        return permissionService.update(permission);
    }

    @DeleteMapping("/permission/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return permissionService.delete(findModel.getId());
    }
}
