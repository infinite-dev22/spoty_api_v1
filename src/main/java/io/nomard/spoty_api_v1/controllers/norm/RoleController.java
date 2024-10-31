package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.PermissionDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.RoleDTO;
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
    public Page<RoleDTO.RoleAsWholeDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                      @RequestParam(defaultValue = "50") Integer pageSize) {
        return roleService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public RoleDTO.RoleAsWholeDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return roleService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Role role) {
        return roleService.save(role);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Role role) throws NotFoundException {
        return roleService.update(role);
    }

    @DeleteMapping("delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return roleService.delete(findModel.getId());
    }
}
