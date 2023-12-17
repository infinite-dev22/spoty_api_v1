package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ServiceInvoice;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.ServiceInvoiceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("service_invoices")
public class ServiceInvoiceController {
    @Autowired
    private ServiceInvoiceServiceImpl serviceInvoiceService;


    @GetMapping("/all")
    public List<ServiceInvoice> getAll() {
        return serviceInvoiceService.getAll();
    }

    @GetMapping("/single")
    public ServiceInvoice getById(@RequestBody FindModel findModel) throws NotFoundException {
        return serviceInvoiceService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<ServiceInvoice> getByContains(@RequestBody SearchModel searchModel) {
//        return serviceInvoiceService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody ServiceInvoice serviceInvoice) {
        return serviceInvoiceService.save(serviceInvoice);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody ServiceInvoice serviceInvoice) throws NotFoundException {
        return serviceInvoiceService.update(serviceInvoice);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return serviceInvoiceService.delete(findModel.getId());
    }
}
