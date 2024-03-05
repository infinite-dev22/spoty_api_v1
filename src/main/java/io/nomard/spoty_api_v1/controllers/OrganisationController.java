package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Organisation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.OrganisationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/organisations")
public class OrganisationController {
    @Autowired
    private OrganisationServiceImpl organisationService;


    @GetMapping("/all")
    public List<Organisation> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "50") Integer pageSize) {
        return organisationService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Organisation getById(@RequestBody FindModel findModel) throws NotFoundException {
        return organisationService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Organisation organisation) {
        organisation.setCreatedAt(new Date());
        return organisationService.save(organisation);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Organisation organisation) throws NotFoundException {
        return organisationService.update(organisation);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return organisationService.delete(findModel.getId());
    }
}
