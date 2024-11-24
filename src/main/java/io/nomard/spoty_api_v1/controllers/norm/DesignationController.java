package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.DesignationDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.hrm.DesignationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("designations")
public class DesignationController {
    @Autowired
    private DesignationServiceImpl designationService;

    @GetMapping("/all")
    public Page<DesignationDTO.DesignationAsWholeDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                             @RequestParam(defaultValue = "50") Integer pageSize) {
        return designationService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public DesignationDTO.DesignationAsWholeDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return designationService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<DesignationDTO.DesignationAsWholeDTO> getByContains(@RequestBody SearchModel searchModel) {
        return designationService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Designation designation) {
        return designationService.save(designation);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Designation designation) throws NotFoundException {
        return designationService.update(designation);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return designationService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return designationService.deleteMultiple(idList);
    }
}
