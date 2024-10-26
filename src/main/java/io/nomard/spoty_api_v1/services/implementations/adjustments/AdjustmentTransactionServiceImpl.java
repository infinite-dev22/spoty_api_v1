package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdjustmentTransactionServiceImpl implements AdjustmentTransactionService {
    @Autowired
    private AdjustmentTransactionRepository adjustmentTransactionRepo;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("adjustment_transactions")
    @Transactional(readOnly = true)
    public AdjustmentTransaction getById(Long id) throws NotFoundException {
        Optional<AdjustmentTransaction> adjustmentTransaction = adjustmentTransactionRepo.findByAdjustmentDetailId(id);
        if (adjustmentTransaction.isEmpty()) {
            throw new NotFoundException();
        }
        return adjustmentTransaction.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(AdjustmentDetail adjustmentDetail) {
        try {
            var productQuantity = 0L;

            if (adjustmentDetail.getAdjustmentType().equalsIgnoreCase("INCREMENT")) {
                productQuantity =
                        productService.getByIdInternally(adjustmentDetail.getProduct().getId()).getQuantity() + adjustmentDetail.getQuantity();
            } else if (adjustmentDetail.getAdjustmentType().equalsIgnoreCase("DECREMENT")) {
                productQuantity =
                        productService.getByIdInternally(adjustmentDetail.getProduct().getId()).getQuantity() - adjustmentDetail.getQuantity();
            }

            var product = adjustmentDetail.getProduct();
            product.setQuantity(productQuantity);
            productService.update(product, null);

            AdjustmentTransaction adjustmentTransaction = new AdjustmentTransaction();
            adjustmentTransaction.setBranch(adjustmentDetail.getAdjustment().getBranch());
            adjustmentTransaction.setProduct(adjustmentDetail.getProduct());
            adjustmentTransaction.setAdjustmentDetail(adjustmentDetail);
            adjustmentTransaction.setDate(LocalDateTime.now());
            adjustmentTransaction.setAdjustQuantity(adjustmentDetail.getQuantity());
            adjustmentTransaction.setAdjustmentType(adjustmentDetail.getAdjustmentType());

            adjustmentTransaction.setTenant(authService.authUser().getTenant());
            adjustmentTransaction.setBranch(authService.authUser().getBranch());
            adjustmentTransaction.setCreatedBy(authService.authUser());
            adjustmentTransaction.setCreatedAt(LocalDateTime.now());
            adjustmentTransactionRepo.save(adjustmentTransaction);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "adjustment_transactions", key = "#data.id")
    public ResponseEntity<ObjectNode> update(AdjustmentDetail data) throws NotFoundException {
        var opt = adjustmentTransactionRepo.findByAdjustmentDetailId(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var adjustmentTransaction = opt.get();

        if (!Objects.equals(adjustmentTransaction.getBranch(), data.getAdjustment().getBranch()) && Objects.nonNull(data.getAdjustment().getBranch())) {
            adjustmentTransaction.setBranch(data.getAdjustment().getBranch());
        }

        if (!Objects.equals(adjustmentTransaction.getProduct(), data.getProduct()) && Objects.nonNull(data.getProduct())) {
            var productQuantity = 0L;
            var currentProductQuantity = productService.getByIdInternally(data.getProduct().getId()).getQuantity();

            var adjustQuantity = adjustmentTransaction.getAdjustQuantity();

            if (data.getAdjustmentType().equalsIgnoreCase("INCREMENT")) {
                productQuantity =
                        (currentProductQuantity - adjustQuantity) + data.getQuantity();

                adjustmentTransaction.setAdjustmentType(data.getAdjustmentType());
            } else if (data.getAdjustmentType().equalsIgnoreCase("DECREMENT")) {
                productQuantity =
                        (currentProductQuantity + adjustQuantity) - data.getQuantity();

                adjustmentTransaction.setAdjustmentType(data.getAdjustmentType());
            }

            var product = data.getProduct();
            product.setQuantity(productQuantity);
            productService.update(product, null);

            adjustmentTransaction.setProduct(data.getProduct());
        }

        if (!Objects.equals(adjustmentTransaction.getAdjustmentDetail(), data)) {
            adjustmentTransaction.setAdjustmentDetail(data);
        }

        if (!Objects.equals(adjustmentTransaction.getAdjustmentDetail().getCreatedAt(), data.getCreatedAt()) && Objects.nonNull(data.getCreatedAt())) {
            adjustmentTransaction.setDate(data.getCreatedAt());
        }

        if (!Objects.equals(adjustmentTransaction.getAdjustQuantity(), data.getQuantity())) {
            adjustmentTransaction.setAdjustQuantity(data.getQuantity());
        }

        if (Objects.nonNull(data.getAdjustmentType()) && !"".equalsIgnoreCase(data.getAdjustmentType())) {
            adjustmentTransaction.setAdjustmentType(data.getAdjustmentType());
        }

        adjustmentTransaction.setUpdatedBy(authService.authUser());
        adjustmentTransaction.setUpdatedAt(LocalDateTime.now());

        try {
            adjustmentTransactionRepo.save(adjustmentTransaction);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            adjustmentTransactionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            adjustmentTransactionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
