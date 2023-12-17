package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SaleMasterServiceImpl implements SaleMasterService {
    @Autowired
    private SaleMasterRepository saleMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<SaleMaster> getAll() {
        return saleMasterRepo.findAll();
    }

    @Override
    public SaleMaster getById(Long id) throws NotFoundException {
        Optional<SaleMaster> saleMaster = saleMasterRepo.findById(id);
        if (saleMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return saleMaster.get();
    }

    @Override
    public List<SaleMaster> getByContains(String search) {
        return saleMasterRepo.searchAllByRefContainingIgnoreCase(
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(SaleMaster saleMaster) {
        try {
            saleMaster.setCreatedBy(authService.authUser());
            saleMaster.setCreatedAt(new Date());
            saleMasterRepo.saveAndFlush(saleMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(SaleMaster data) throws NotFoundException {
        var opt = saleMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            saleMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate())) {
            saleMaster.setDate(data.getDate());
        }

//        if (Objects.nonNull(data.getCustomer())) {
//            saleMaster.setCustomer(data.getCustomer());
//        }

        if (Objects.nonNull(data.getBranch())) {
            saleMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getSaleDetails()) && !data.getSaleDetails().isEmpty()) {
            saleMaster.setSaleDetails(data.getSaleDetails());
        }

//        if (!Objects.equals(data.getTaxRate(), saleMaster.getTaxRate())) {
//            saleMaster.setTaxRate(data.getTaxRate());
//        }
//
//        if (!Objects.equals(data.getNetTax(), saleMaster.getNetTax())) {
//            saleMaster.setNetTax(data.getNetTax());
//        }
//
//        if (!Objects.equals(data.getDiscount(), saleMaster.getDiscount())) {
//            saleMaster.setDiscount(data.getDiscount());
//        }

//        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
//            saleMaster.setShipping(data.getShipping());
//        }

//        if (!Objects.equals(data.getPaid(), saleMaster.getPaid())) {
//            saleMaster.setPaid(data.getPaid());
//        }
//
//        if (!Objects.equals(data.getTotal(), saleMaster.getTotal())) {
//            saleMaster.setTotal(data.getTotal());
//        }
//
//        if (!Objects.equals(data.getDue(), saleMaster.getDue())) {
//            saleMaster.setDue(data.getDue());
//        }
//
//        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
//            saleMaster.setStatus(data.getStatus());
//        }
//
//        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
//            saleMaster.setPaymentStatus(data.getPaymentStatus());
//        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            saleMaster.setNotes(data.getNotes());
        }

        saleMaster.setUpdatedBy(authService.authUser());
        saleMaster.setUpdatedAt(new Date());

        try {
            saleMasterRepo.saveAndFlush(saleMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            saleMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
