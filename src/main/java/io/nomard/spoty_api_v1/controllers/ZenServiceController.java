package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ZenService;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.ZenServiceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("services")
public class ZenServiceController {
    @Autowired
    private ZenServiceServiceImpl serviceService;


    @GetMapping("/all")
    public List<ZenService> getAll() {
        return serviceService.getAll();
    }

    @GetMapping("/single")
    public ZenService getById(@RequestBody FindModel findModel) throws NotFoundException {
        return serviceService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<ZenService> getByContains(@RequestBody SearchModel searchModel) {
//        return serviceService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody ZenService service) {
        return serviceService.save(service);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody ZenService service) throws NotFoundException {
        return serviceService.update(service);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return serviceService.delete(findModel.getId());
    }
}
