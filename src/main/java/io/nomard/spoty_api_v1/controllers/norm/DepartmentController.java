package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DepartmentDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.hrm.DepartmentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("departments")
public class DepartmentController {
    @Autowired
    private DepartmentServiceImpl departmentService;

    @GetMapping("/all")
    public Page<DepartmentDTO.DepartmentAsWholeDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                           @RequestParam(defaultValue = "50") Integer pageSize) {
        return departmentService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public DepartmentDTO.DepartmentAsWholeDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return departmentService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<DepartmentDTO.DepartmentAsWholeDTO> getByContains(@RequestBody SearchModel searchModel) {
        return departmentService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Department department) {
        return departmentService.save(department);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Department department) throws NotFoundException {
        return departmentService.update(department);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return departmentService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody ArrayList<Long> idList) {
        return departmentService.delete(idList);
    }
}
