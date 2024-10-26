package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.EmploymentStatusDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.hrm.EmploymentStatusServiceImpl;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("employment/statuses")
public class EmploymentStatusController {
    @Autowired
    private EmploymentStatusServiceImpl employmentStatusService;

    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<EmploymentStatusDTO.EmploymentStatusAsWholeDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return employmentStatusService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public EmploymentStatusDTO.EmploymentStatusAsWholeDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return employmentStatusService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<EmploymentStatusDTO.EmploymentStatusAsWholeDTO> getByContains(@RequestBody SearchModel searchModel) {
        return employmentStatusService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody EmploymentStatus employmentStatus) {
        return employmentStatusService.save(employmentStatus);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody EmploymentStatus employmentStatus) throws NotFoundException {
        return employmentStatusService.update(employmentStatus);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return employmentStatusService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) throws NotFoundException {
        return employmentStatusService.deleteMultiple(idList);
    }
}
