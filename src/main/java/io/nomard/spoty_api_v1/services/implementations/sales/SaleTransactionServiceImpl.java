package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleTransactionServiceImpl implements SaleTransactionService {
    @Autowired
    private SaleTransactionRepository saleTransactionRepo;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("sale_transactions")
    @Transactional(readOnly = true)
    public SaleTransaction getById(Long id) throws NotFoundException {
        Optional<SaleTransaction> saleTransaction = saleTransactionRepo.findBySaleDetailId(id);
        if (saleTransaction.isEmpty()) {
            throw new NotFoundException();
        }
        return saleTransaction.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(SaleDetail saleDetail) {
        if (saleDetail.getProduct().getQuantity() > 0 && saleDetail.getProduct().getQuantity() >= saleDetail.getQuantity()) {
            try {
                var productQuantity =
                        productService.getById(saleDetail.getProduct().getId()).getQuantity() - saleDetail.getQuantity();

                var product = saleDetail.getProduct();
                product.setQuantity(productQuantity);
                productService.update(product, null);

                SaleTransaction saleTransaction = new SaleTransaction();
                saleTransaction.setBranch(saleDetail.getSale().getBranch());
                saleTransaction.setProduct(saleDetail.getProduct());
                saleTransaction.setSaleDetail(saleDetail);
                saleTransaction.setDate(LocalDateTime.now());
                saleTransaction.setSaleQuantity(saleDetail.getQuantity());
                saleTransaction.setTenant(authService.authUser().getTenant());
                saleTransaction.setBranch(authService.authUser().getBranch());
                saleTransaction.setCreatedBy(authService.authUser());
                saleTransaction.setCreatedAt(LocalDateTime.now());
                saleTransactionRepo.saveAndFlush(saleTransaction);
                return spotyResponseImpl.created();
            } catch (Exception e) {
                return spotyResponseImpl.error(e);
            }
        }
        return spotyResponseImpl.custom(HttpStatus.NOT_ACCEPTABLE, "Product quantity is too low");
    }

    @Override
    @CacheEvict(value = "sale_transactions", key = "#data.id")
    public ResponseEntity<ObjectNode> update(SaleDetail data) throws NotFoundException {
        var opt = saleTransactionRepo.findBySaleDetailId(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleTransaction = opt.get();

        if (!Objects.equals(saleTransaction.getBranch(), data.getSale().getBranch()) && Objects.nonNull(data.getSale().getBranch())) {
            saleTransaction.setBranch(data.getSale().getBranch());
        }

        if (!Objects.equals(saleTransaction.getProduct(), data.getProduct()) && Objects.nonNull(data.getProduct())) {
            if (data.getProduct().getQuantity() > 0 && data.getProduct().getQuantity() >= data.getQuantity()) {
                var adjustQuantity = saleTransaction.getSaleQuantity();
                var currentProductQuantity = productService.getById(data.getProduct().getId()).getQuantity();
                var productQuantity =
                        (currentProductQuantity + adjustQuantity) - data.getQuantity();

                var product = data.getProduct();
                product.setQuantity(productQuantity);
                productService.update(product, null);
            }

            saleTransaction.setProduct(data.getProduct());
        }

        if (!Objects.equals(saleTransaction.getSaleDetail(), data)) {
            saleTransaction.setSaleDetail(data);
        }

        if (!Objects.equals(saleTransaction.getSaleDetail().getCreatedAt(), data.getCreatedAt()) && Objects.nonNull(data.getCreatedAt())) {
            saleTransaction.setDate(data.getCreatedAt());
        }

        if (!Objects.equals(saleTransaction.getSaleQuantity(), data.getQuantity())) {
            saleTransaction.setSaleQuantity(data.getQuantity());
        }

        saleTransaction.setUpdatedBy(authService.authUser());
        saleTransaction.setUpdatedAt(LocalDateTime.now());

        try {
            saleTransactionRepo.saveAndFlush(saleTransaction);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleTransactionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            saleTransactionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
