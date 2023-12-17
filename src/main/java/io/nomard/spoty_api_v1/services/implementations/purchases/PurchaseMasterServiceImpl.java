package io.nomard.spoty_api_v1.services.implementations.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.purchases.PurchaseMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchaseMasterServiceImpl implements PurchaseMasterService {
    @Autowired
    private PurchaseMasterRepository purchaseMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<PurchaseMaster> getAll() {
        return purchaseMasterRepo.findAll();
    }

    @Override
    public PurchaseMaster getById(Long id) throws NotFoundException {
        Optional<PurchaseMaster> purchaseMaster = purchaseMasterRepo.findById(id);
        if (purchaseMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseMaster.get();
    }

    @Override
    public List<PurchaseMaster> getByContains(String search) {
        return purchaseMasterRepo.searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(PurchaseMaster purchaseMaster) {
        try {
            purchaseMaster.setCreatedBy(authService.authUser());
            purchaseMaster.setCreatedAt(new Date());
            purchaseMasterRepo.saveAndFlush(purchaseMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(PurchaseMaster data) throws NotFoundException {
        var opt = purchaseMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            purchaseMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate())) {
            purchaseMaster.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getSupplier())) {
            purchaseMaster.setSupplier(data.getSupplier());
        }

        if (Objects.nonNull(data.getBranch())) {
            purchaseMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getPurchaseDetails()) && !data.getPurchaseDetails().isEmpty()) {
            purchaseMaster.setPurchaseDetails(data.getPurchaseDetails());
        }

        if (!Objects.equals(data.getTaxRate(), purchaseMaster.getTaxRate())) {
            purchaseMaster.setTaxRate(data.getTaxRate());
        }

        if (!Objects.equals(data.getNetTax(), purchaseMaster.getNetTax())) {
            purchaseMaster.setNetTax(data.getNetTax());
        }

        if (!Objects.equals(data.getDiscount(), purchaseMaster.getDiscount())) {
            purchaseMaster.setDiscount(data.getDiscount());
        }

        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
            purchaseMaster.setShipping(data.getShipping());
        }

        if (!Objects.equals(data.getPaid(), purchaseMaster.getPaid())) {
            purchaseMaster.setPaid(data.getPaid());
        }

        if (!Objects.equals(data.getTotal(), purchaseMaster.getTotal())) {
            purchaseMaster.setTotal(data.getTotal());
        }

        if (!Objects.equals(data.getDue(), purchaseMaster.getDue())) {
            purchaseMaster.setDue(data.getDue());
        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            purchaseMaster.setStatus(data.getStatus());
        }

        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            purchaseMaster.setPaymentStatus(data.getPaymentStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            purchaseMaster.setNotes(data.getNotes());
        }

        purchaseMaster.setUpdatedBy(authService.authUser());
        purchaseMaster.setUpdatedAt(new Date());

        try {
            purchaseMasterRepo.saveAndFlush(purchaseMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            purchaseMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
