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
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public Mono<AdjustmentTransaction> getById(Long id) {
        return adjustmentTransactionRepo.findByAdjustmentDetailId(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Adjustment detail not found")));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(AdjustmentDetail adjustmentDetail) {
        return productService.getById(adjustmentDetail.getProduct().getId())
                .flatMap(product -> {
                    long productQuantity;
                    if (adjustmentDetail.getAdjustmentType().equalsIgnoreCase("INCREMENT")) {
                        productQuantity = product.getQuantity() + adjustmentDetail.getQuantity();
                    } else if (adjustmentDetail.getAdjustmentType().equalsIgnoreCase("DECREMENT")) {
                        productQuantity = product.getQuantity() - adjustmentDetail.getQuantity();
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid adjustment type"));
                    }

                    product.setQuantity(productQuantity);
                    return productService.update(product, null)
                            .then(authService.authUser())
                            .flatMap(user -> {
                                AdjustmentTransaction adjustmentTransaction = new AdjustmentTransaction();
                                adjustmentTransaction.setBranch(adjustmentDetail.getAdjustment().getBranch());
                                adjustmentTransaction.setProduct(adjustmentDetail.getProduct());
                                adjustmentTransaction.setAdjustmentDetail(adjustmentDetail);
                                adjustmentTransaction.setDate(new Date());
                                adjustmentTransaction.setAdjustQuantity(adjustmentDetail.getQuantity());
                                adjustmentTransaction.setAdjustmentType(adjustmentDetail.getAdjustmentType());
                                adjustmentTransaction.setTenant(user.getTenant());
                                adjustmentTransaction.setBranch(user.getBranch());
                                adjustmentTransaction.setCreatedBy(user);
                                adjustmentTransaction.setCreatedAt(new Date());

                                return adjustmentTransactionRepo.save(adjustmentTransaction)
                                        .then(Mono.just(spotyResponseImpl.created()));
                            });
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
