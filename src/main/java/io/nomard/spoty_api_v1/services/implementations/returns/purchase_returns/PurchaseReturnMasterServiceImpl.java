package io.nomard.spoty_api_v1.services.implementations.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.purchase_returns.PurchaseReturnMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns.PurchaseReturnMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PurchaseReturnMasterServiceImpl implements PurchaseReturnMasterService {
    @Autowired
    private PurchaseReturnMasterRepository purchaseReturnMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<PurchaseReturnMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<PurchaseReturnMaster> page = purchaseReturnMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public PurchaseReturnMaster getById(Long id) throws NotFoundException {
        Optional<PurchaseReturnMaster> purchaseReturnMaster = purchaseReturnMasterRepo.findById(id);
        if (purchaseReturnMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseReturnMaster.get();
    }

    @Override
    public List<PurchaseReturnMaster> getByContains(String search) {
        return purchaseReturnMasterRepo.searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(PurchaseReturnMaster purchaseReturnMaster) {
        try {
            purchaseReturnMaster.setTenant(authService.authUser().getTenant());
            purchaseReturnMaster.setCreatedBy(authService.authUser());
            purchaseReturnMaster.setCreatedAt(new Date());
            purchaseReturnMasterRepo.saveAndFlush(purchaseReturnMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(PurchaseReturnMaster data) throws NotFoundException {
        var opt = purchaseReturnMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseReturnMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            purchaseReturnMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate())) {
            purchaseReturnMaster.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getSupplier())) {
            purchaseReturnMaster.setSupplier(data.getSupplier());
        }

        if (Objects.nonNull(data.getBranch())) {
            purchaseReturnMaster.setBranch(data.getBranch());
        }

//        if (Objects.nonNull(data.getPurchaseDetails()) && !data.getPurchaseDetails().isEmpty()) {
//            purchaseReturnMaster.setPurchaseDetails(data.getPurchaseDetails());
//        }

        if (!Objects.equals(data.getTaxRate(), purchaseReturnMaster.getTaxRate())) {
            purchaseReturnMaster.setTaxRate(data.getTaxRate());
        }

        if (!Objects.equals(data.getNetTax(), purchaseReturnMaster.getNetTax())) {
            purchaseReturnMaster.setNetTax(data.getNetTax());
        }

        if (!Objects.equals(data.getDiscount(), purchaseReturnMaster.getDiscount())) {
            purchaseReturnMaster.setDiscount(data.getDiscount());
        }

        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
            purchaseReturnMaster.setShipping(data.getShipping());
        }

        if (!Objects.equals(data.getPaid(), purchaseReturnMaster.getPaid())) {
            purchaseReturnMaster.setPaid(data.getPaid());
        }

        if (!Objects.equals(data.getTotal(), purchaseReturnMaster.getTotal())) {
            purchaseReturnMaster.setTotal(data.getTotal());
        }

//        if (!Objects.equals(data.getDue(), purchaseReturnMaster.getDue())) {
//            purchaseReturnMaster.setDue(data.getDue());
//        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            purchaseReturnMaster.setStatus(data.getStatus());
        }

        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            purchaseReturnMaster.setPaymentStatus(data.getPaymentStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            purchaseReturnMaster.setNotes(data.getNotes());
        }

        purchaseReturnMaster.setUpdatedBy(authService.authUser());
        purchaseReturnMaster.setUpdatedAt(new Date());

        try {
            purchaseReturnMasterRepo.saveAndFlush(purchaseReturnMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseReturnMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            purchaseReturnMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
