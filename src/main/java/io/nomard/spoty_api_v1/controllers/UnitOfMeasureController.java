package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.UnitOfMeasureServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class UnitOfMeasureController {
    @Autowired
    private UnitOfMeasureServiceImpl unit_of_measureService;


    @GetMapping("/unit_of_measures")
    public List<UnitOfMeasure> getAll() {
        return unit_of_measureService.getAll();
    }

    @PostMapping("/unit_of_measure")
    public UnitOfMeasure getById(@RequestBody Long id) throws NotFoundException {
        return unit_of_measureService.getById(id);
    }

    @PostMapping("/unit_of_measures/search")
    public List<UnitOfMeasure> getByContains(@RequestBody String search) {
        return unit_of_measureService.getByContains(search);
    }

    @PostMapping("/unit_of_measure/add")
    public UnitOfMeasure save(@Valid @RequestBody UnitOfMeasure unit_of_measure) {
        unit_of_measure.setCreatedAt(new Date());
        return unit_of_measureService.save(unit_of_measure);
    }

    @PutMapping("/unit_of_measure/update")
    public UnitOfMeasure update(@RequestBody Long id, @Valid @RequestBody UnitOfMeasure unit_of_measure) {
        unit_of_measure.setId(id);
        unit_of_measure.setUpdatedAt(new Date());
        return unit_of_measureService.update(id, unit_of_measure);
    }

    @PostMapping("/unit_of_measure/delete")
    public String delete(@RequestBody Long id) {
        return unit_of_measureService.delete(id);
    }
}
