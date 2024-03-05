package io.nomard.spoty_api_v1.services.implementations.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.purchases.PurchaseDetailService;
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
public class PurchaseDetailServiceImpl implements PurchaseDetailService {
    @Autowired
    private PurchaseDetailRepository purchaseDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<PurchaseDetail> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<PurchaseDetail> page = purchaseDetailRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public PurchaseDetail getById(Long id) throws NotFoundException {
        Optional<PurchaseDetail> purchaseDetail = purchaseDetailRepo.findById(id);
        if (purchaseDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseDetail.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(PurchaseDetail purchaseDetail) {
        try {
            purchaseDetail.setCreatedBy(authService.authUser());
            purchaseDetail.setCreatedAt(new Date());
            purchaseDetailRepo.saveAndFlush(purchaseDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<PurchaseDetail> purchaseDetailSet) {
        return null;
    }

    @Override
    public ResponseEntity<ObjectNode> update(PurchaseDetail data) throws NotFoundException {
        var opt = purchaseDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseDetail = opt.get();

        if (!Objects.equals(data.getCost(), purchaseDetail.getCost())) {
            purchaseDetail.setCost(data.getCost());
        }

        if (Objects.nonNull(data.getProduct())) {
            purchaseDetail.setProduct(data.getProduct());
        }

        if (!Objects.equals(data.getNetTax(), purchaseDetail.getNetTax())) {
            purchaseDetail.setNetTax(data.getNetTax());
        }

        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
            purchaseDetail.setTaxType(data.getTaxType());
        }

        if (!Objects.equals(data.getDiscount(), purchaseDetail.getDiscount())) {
            purchaseDetail.setDiscount(data.getDiscount());
        }

        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
            purchaseDetail.setDiscountType(data.getDiscountType());
        }

        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
            purchaseDetail.setSerialNumber(data.getSerialNumber());
        }

        if (!Objects.equals(data.getTotal(), purchaseDetail.getTotal())) {
            purchaseDetail.setTotal(data.getTotal());
        }

        if (!Objects.equals(data.getQuantity(), purchaseDetail.getQuantity())) {
            purchaseDetail.setQuantity(data.getQuantity());
        }

        purchaseDetail.setUpdatedBy(authService.authUser());
        purchaseDetail.setUpdatedAt(new Date());

        try {
            purchaseDetailRepo.saveAndFlush(purchaseDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idSet) throws NotFoundException {
        try {
            purchaseDetailRepo.deleteAllById(idSet);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
