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

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public TransferTransaction getById(Long id) throws NotFoundException {
        Optional<TransferTransaction> transferTransaction = transferTransactionRepo.findByTransferDetailId(id);
        if (transferTransaction.isEmpty()) {
            throw new NotFoundException();
        }
        return transferTransaction.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(TransferDetail transferDetail) {
        if (transferDetail.getProduct().getQuantity() > 0 && transferDetail.getProduct().getQuantity() > transferDetail.getQuantity()) {
            try {
                var productQuantity =
                        productService.getById(transferDetail.getProduct().getId()).getQuantity() - transferDetail.getQuantity();

                var product = transferDetail.getProduct();
                product.setQuantity(productQuantity);
                productService.update(product);

                TransferTransaction transferTransaction = new TransferTransaction();
                transferTransaction.setFromBranch(transferDetail.getTransfer().getFromBranch());
                transferTransaction.setToBranch(transferDetail.getTransfer().getToBranch());
                transferTransaction.setProduct(transferDetail.getProduct());
                transferTransaction.setTransferDetail(transferDetail);
                transferTransaction.setDate(new Date());
                transferTransaction.setTransferQuantity(transferDetail.getQuantity());

                transferTransaction.setCreatedBy(authService.authUser());
                transferTransaction.setCreatedAt(new Date());
                transferTransactionRepo.saveAndFlush(transferTransaction);
                return spotyResponseImpl.created();
            } catch (Exception e) {
                return spotyResponseImpl.error(e);
            }
        }
        return spotyResponseImpl.custom(HttpStatus.NOT_ACCEPTABLE, "Product quantity is too low");
    }

    @Override
    @CacheEvict(value = "transfer_transactions", key = "#data.id")
    public ResponseEntity<ObjectNode> update(TransferDetail data) throws NotFoundException {
        var opt = transferTransactionRepo.findByTransferDetailId(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var transferTransaction = opt.get();

        if (!Objects.equals(transferTransaction.getFromBranch(), data.getTransfer().getFromBranch()) && Objects.nonNull(data.getTransfer().getFromBranch())) {
            transferTransaction.setFromBranch(data.getTransfer().getFromBranch());
        }

        if (!Objects.equals(transferTransaction.getToBranch(), data.getTransfer().getToBranch()) && Objects.nonNull(data.getTransfer().getToBranch())) {
            transferTransaction.setToBranch(data.getTransfer().getToBranch());
        }

        if (!Objects.equals(transferTransaction.getProduct(), data.getProduct()) && Objects.nonNull(data.getProduct())) {
            if (data.getProduct().getQuantity() > 0 && data.getProduct().getQuantity() > data.getQuantity()) {
                var adjustQuantity = transferTransaction.getTransferQuantity();
                var currentProductQuantity = productService.getById(data.getProduct().getId()).getQuantity();
                var productQuantity =
                        (currentProductQuantity - adjustQuantity) + data.getQuantity();

                var product = data.getProduct();
                product.setQuantity(productQuantity);
                productService.update(product);
            }

            transferTransaction.setProduct(data.getProduct());
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

        transferTransaction.setUpdatedBy(authService.authUser());
        transferTransaction.setUpdatedAt(new Date());

        try {
            transferTransactionRepo.saveAndFlush(transferTransaction);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            transferTransactionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            transferTransactionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
