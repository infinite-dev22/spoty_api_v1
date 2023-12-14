package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Organisation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.OrganisationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/expense_categories")
public class OrganisationController {
    @Autowired
    private OrganisationServiceImpl organisationService;


    @GetMapping("/all")
    public List<Organisation> getAll() {
        return organisationService.getAll();
    }

    @PostMapping("/single")
    public Organisation getById(@RequestBody Long id) throws NotFoundException {
        return organisationService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Organisation organisation) {
        organisation.setCreatedAt(new Date());
        return organisationService.save(organisation);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody Organisation organisation) {
        organisation.setId(id);
        organisation.setUpdatedAt(new Date());
        return organisationService.update(id, organisation);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return organisationService.delete(id);
    }
}
