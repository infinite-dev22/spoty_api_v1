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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Page<PurchaseDetail> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        return purchaseDetailRepo.findAll(pageRequest);
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
    @Transactional
    public ResponseEntity<ObjectNode> save(PurchaseDetail purchaseDetail) {
        try {
            purchaseDetail.setSubTotalCost(purchaseDetail.getCost() * purchaseDetail.getQuantity());
            purchaseDetail.setCreatedBy(authService.authUser());
            purchaseDetail.setCreatedAt(LocalDateTime.now());
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
    @Transactional
    public ResponseEntity<ObjectNode> update(PurchaseDetail data) throws NotFoundException {
        var opt = purchaseDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseDetail = opt.get();

        if (!Objects.equals(data.getCost(), purchaseDetail.getCost())) {
            purchaseDetail.setCost(data.getCost());
            purchaseDetail.setSubTotalCost(purchaseDetail.getCost() * purchaseDetail.getQuantity());
        }

        if (Objects.nonNull(data.getProduct())) {
            purchaseDetail.setProduct(data.getProduct());
        }

        if (!Objects.equals(data.getPurchase(), purchaseDetail.getPurchase())) {
            purchaseDetail.setPurchase(data.getPurchase());
        }

        if (!Objects.equals(data.getQuantity(), purchaseDetail.getQuantity())) {
            purchaseDetail.setQuantity(data.getQuantity());
        }

        purchaseDetail.setUpdatedBy(authService.authUser());
        purchaseDetail.setUpdatedAt(LocalDateTime.now());

        try {
            purchaseDetailRepo.saveAndFlush(purchaseDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
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
