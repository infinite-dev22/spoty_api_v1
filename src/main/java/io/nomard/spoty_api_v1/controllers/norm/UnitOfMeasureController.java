package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.UnitOfMeasureDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.UnitOfMeasureServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/units_of_measure")
public class UnitOfMeasureController {
    @Autowired
    private UnitOfMeasureServiceImpl unit_of_measureService;

    @GetMapping("/all")
    public Page<UnitOfMeasureDTO.AsWholeDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return unit_of_measureService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public UnitOfMeasureDTO.AsWholeDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return unit_of_measureService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<UnitOfMeasureDTO.AsWholeDTO> getByContains(@RequestBody SearchModel searchModel) {
        return unit_of_measureService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody UnitOfMeasure unit_of_measure) {
        return unit_of_measureService.save(unit_of_measure);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody UnitOfMeasure unit_of_measure) throws NotFoundException {
        return unit_of_measureService.update(unit_of_measure);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return unit_of_measureService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return unit_of_measureService.deleteMultiple(idList);
    }
}
