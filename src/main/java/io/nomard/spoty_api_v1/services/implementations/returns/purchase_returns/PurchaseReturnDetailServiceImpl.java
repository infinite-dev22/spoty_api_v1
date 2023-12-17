package io.nomard.spoty_api_v1.services.implementations.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.purchase_returns.PurchaseReturnDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns.PurchaseReturnDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchaseReturnDetailServiceImpl implements PurchaseReturnDetailService {
    @Autowired
    private PurchaseReturnDetailRepository purchaseReturnDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<PurchaseReturnDetail> getAll() {
        return purchaseReturnDetailRepo.findAll();
    }

    @Override
    public PurchaseReturnDetail getById(Long id) throws NotFoundException {
        Optional<PurchaseReturnDetail> purchaseReturnDetail = purchaseReturnDetailRepo.findById(id);
        if (purchaseReturnDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseReturnDetail.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(PurchaseReturnDetail purchaseReturnDetail) {
        try {
            purchaseReturnDetail.setCreatedBy(authService.authUser());
            purchaseReturnDetail.setCreatedAt(new Date());
            purchaseReturnDetailRepo.saveAndFlush(purchaseReturnDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(ArrayList<PurchaseReturnDetail> purchaseReturnDetailList) {
        return null;
    }

    @Override
    public ResponseEntity<ObjectNode> update(PurchaseReturnDetail data) throws NotFoundException {
        var opt = purchaseReturnDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseReturnDetail = opt.get();

        if (!Objects.equals(data.getCost(), purchaseReturnDetail.getCost())) {
            purchaseReturnDetail.setCost(data.getCost());
        }

        if (Objects.nonNull(data.getProduct())) {
            purchaseReturnDetail.setProduct(data.getProduct());
        }

        if (!Objects.equals(data.getNetTax(), purchaseReturnDetail.getNetTax())) {
            purchaseReturnDetail.setNetTax(data.getNetTax());
        }

        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
            purchaseReturnDetail.setTaxType(data.getTaxType());
        }

        if (!Objects.equals(data.getDiscount(), purchaseReturnDetail.getDiscount())) {
            purchaseReturnDetail.setDiscount(data.getDiscount());
        }

        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
            purchaseReturnDetail.setDiscountType(data.getDiscountType());
        }

        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
            purchaseReturnDetail.setSerialNumber(data.getSerialNumber());
        }

        if (!Objects.equals(data.getTotal(), purchaseReturnDetail.getTotal())) {
            purchaseReturnDetail.setTotal(data.getTotal());
        }

        if (!Objects.equals(data.getQuantity(), purchaseReturnDetail.getQuantity())) {
            purchaseReturnDetail.setQuantity(data.getQuantity());
        }

        purchaseReturnDetail.setUpdatedBy(authService.authUser());
        purchaseReturnDetail.setUpdatedAt(new Date());

        try {
            purchaseReturnDetailRepo.saveAndFlush(purchaseReturnDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseReturnDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            purchaseReturnDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
