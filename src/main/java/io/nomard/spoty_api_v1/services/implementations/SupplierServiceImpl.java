package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.SupplierRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    @Override
    public Supplier getById(Long id) throws NotFoundException {
        Optional<Supplier> supplier = supplierRepo.findById(id);
        if (supplier.isEmpty()) {
            throw new NotFoundException();
        }
        return supplier.get();
    }

    @Override
    public List<Supplier> getByContains(String search) {
        return supplierRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Supplier supplier) {
        try {
            supplier.setCreatedBy(authService.authUser());
            supplier.setCreatedAt(new Date());
            supplierRepo.saveAndFlush(supplier);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, Supplier supplier) {
        try {
            supplier.setUpdatedBy(authService.authUser());
            supplier.setUpdatedAt(new Date());
            supplier.setId(id);
            supplierRepo.saveAndFlush(supplier);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            supplierRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
