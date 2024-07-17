package io.nomard.spoty_api_v1.services.implementations.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.transfers.TransferTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.transfers.TransferTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TransferTransactionServiceImpl implements TransferTransactionService {
    @Autowired
    private TransferTransactionRepository transferTransactionRepo;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("transfer_transactions")
    @Transactional(readOnly = true)
    public Mono<TransferTransaction> getById(Long id) {
        return transferTransactionRepo.findByTransferDetailId(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Transfer detail not found")));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(TransferDetail transferDetail) {
        if (transferDetail.getProduct().getQuantity() > 0 && transferDetail.getProduct().getQuantity() > transferDetail.getQuantity()) {
            return productService.getById(transferDetail.getProduct().getId())
                    .flatMap(product -> {
                        long newQuantity = product.getQuantity() - transferDetail.getQuantity();
                        product.setQuantity(newQuantity);

                        return productService.update(product, null)
                                .then(authService.authUser())
                                .flatMap(user -> {
                                    TransferTransaction transferTransaction = new TransferTransaction();
                                    transferTransaction.setFromBranch(transferDetail.getTransfer().getFromBranch());
                                    transferTransaction.setToBranch(transferDetail.getTransfer().getToBranch());
                                    transferTransaction.setProduct(transferDetail.getProduct());
                                    transferTransaction.setTransferDetail(transferDetail);
                                    transferTransaction.setDate(new Date());
                                    transferTransaction.setTransferQuantity(transferDetail.getQuantity());
                                    transferTransaction.setTenant(user.getTenant());
                                    transferTransaction.setCreatedBy(user);
                                    transferTransaction.setCreatedAt(new Date());

                                    return transferTransactionRepo.save(transferTransaction)
                                            .then(Mono.just(spotyResponseImpl.created()));
                                });
                    })
                    .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
        } else {
            return Mono.just(spotyResponseImpl.custom(HttpStatus.NOT_ACCEPTABLE, "Product quantity is too low"));
        }
    }


    @Override
    @CacheEvict(value = "transfer_transactions", key = "#data.id")
    public Mono<ResponseEntity<ObjectNode>> update(TransferDetail data) {
        return transferTransactionRepo.findByTransferDetailId(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(transferTransaction -> {
                    if (!Objects.equals(transferTransaction.getFromBranch(), data.getTransfer().getFromBranch()) && Objects.nonNull(data.getTransfer().getFromBranch())) {
                        transferTransaction.setFromBranch(data.getTransfer().getFromBranch());
                    }

                    if (!Objects.equals(transferTransaction.getToBranch(), data.getTransfer().getToBranch()) && Objects.nonNull(data.getTransfer().getToBranch())) {
                        transferTransaction.setToBranch(data.getTransfer().getToBranch());
                    }

                    if (!Objects.equals(transferTransaction.getProduct(), data.getProduct()) && Objects.nonNull(data.getProduct())) {
                        if (data.getProduct().getQuantity() > 0 && data.getProduct().getQuantity() > data.getQuantity()) {
                            var adjustQuantity = transferTransaction.getTransferQuantity();
                            return productService.getById(data.getProduct().getId())
                                    .flatMap(product -> {
                                        var currentProductQuantity = product.getQuantity();
                                        var productQuantity = (currentProductQuantity - adjustQuantity) + data.getQuantity();
                                        product.setQuantity(productQuantity);
                                        return productService.update(product, null);
                                    })
                                    .then(Mono.fromRunnable(() -> transferTransaction.setProduct(data.getProduct())))
                                    .thenReturn(transferTransaction);
                        } else {
                            return Mono.error(new IllegalArgumentException("Product quantity is too low"));
                        }
                    }

                    if (!Objects.equals(transferTransaction.getTransferDetail(), data)) {
                        transferTransaction.setTransferDetail(data);
                    }

                    if (!Objects.equals(transferTransaction.getTransferDetail().getCreatedAt(), data.getCreatedAt()) && Objects.nonNull(data.getCreatedAt())) {
                        transferTransaction.setDate(data.getCreatedAt());
                    }

                    if (!Objects.equals(transferTransaction.getTransferQuantity(), data.getQuantity())) {
                        transferTransaction.setTransferQuantity(data.getQuantity());
                    }

                    return Mono.just(transferTransaction);
                })
                .flatMap(transferTransaction -> authService.authUser()
                        .map(user -> {
                            transferTransaction.setUpdatedBy(user);
                            transferTransaction.setUpdatedAt(new Date());
                            return transferTransaction;
                        })
                )
                .flatMap(transferTransaction -> transferTransactionRepo.save(transferTransaction))
                .then(Mono.just(spotyResponseImpl.ok()))
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return transferTransactionRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return transferTransactionRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
