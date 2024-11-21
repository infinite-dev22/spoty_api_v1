package io.nomard.spoty_api_v1.services.implementations.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.stock_ins.StockInTransactionService;
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
public class StockInTransactionServiceImpl
        implements StockInTransactionService {

    @Autowired
    private StockInTransactionRepository stockInTransactionRepo;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("stock_in_transactions")
    @Transactional(readOnly = true)
    public StockInTransaction getById(Long id) throws NotFoundException {
        Optional<StockInTransaction> stockInTransaction =
                stockInTransactionRepo.findByStockInDetailId(id);
        if (stockInTransaction.isEmpty()) {
            throw new NotFoundException();
        }
        return stockInTransaction.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(StockInDetail stockInDetail) {
        try {
            var productQuantity =
                    productService
                            .getByIdInternally(stockInDetail.getProduct().getId())
                            .getQuantity() +
                            stockInDetail.getQuantity();

            var product = stockInDetail.getProduct();
            product.setQuantity(productQuantity);
            productService.update(product, null);

            StockInTransaction stockInTransaction = new StockInTransaction();
            stockInTransaction.setBranch(
                    stockInDetail.getStockIn().getBranch()
            );
            stockInTransaction.setProduct(stockInDetail.getProduct());
            stockInTransaction.setStockInDetail(stockInDetail);
            stockInTransaction.setDate(LocalDateTime.now());
            stockInTransaction.setStockInQuantity(stockInDetail.getQuantity());
            stockInTransaction.setTenant(authService.authUser().getTenant());
            stockInTransaction.setBranch(authService.authUser().getBranch());
            stockInTransaction.setCreatedBy(authService.authUser());
            stockInTransaction.setCreatedAt(LocalDateTime.now());
            stockInTransactionRepo.save(stockInTransaction);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "stock_in_transactions", key = "#data.id")
    public ResponseEntity<ObjectNode> update(StockInDetail data)
            throws NotFoundException {
        var opt = stockInTransactionRepo.findByStockInDetailId(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockInTransaction = opt.get();

        if (
                !Objects.equals(
                        stockInTransaction.getBranch(),
                        data.getStockIn().getBranch()
                ) &&
                        Objects.nonNull(data.getStockIn().getBranch())
        ) {
            stockInTransaction.setBranch(data.getStockIn().getBranch());
        }

        if (
                !Objects.equals(
                        stockInTransaction.getProduct(),
                        data.getProduct()
                ) &&
                        Objects.nonNull(data.getProduct())
        ) {
            var adjustQuantity = stockInTransaction.getStockInQuantity();
            var currentProductQuantity = productService
                    .getByIdInternally(data.getProduct().getId())
                    .getQuantity();
            var productQuantity =
                    (currentProductQuantity - adjustQuantity) + data.getQuantity();

            var product = data.getProduct();
            product.setQuantity(productQuantity);
            productService.update(product, null);
        }

        stockInTransaction.setProduct(data.getProduct());

        if (!Objects.equals(stockInTransaction.getStockInDetail(), data)) {
            stockInTransaction.setStockInDetail(data);
        }

        if (
                !Objects.equals(
                        stockInTransaction.getStockInDetail().getCreatedAt(),
                        data.getCreatedAt()
                ) &&
                        Objects.nonNull(data.getCreatedAt())
        ) {
            stockInTransaction.setDate(data.getCreatedAt());
        }

        if (
                !Objects.equals(
                        stockInTransaction.getStockInQuantity(),
                        data.getQuantity()
                )
        ) {
            stockInTransaction.setStockInQuantity(data.getQuantity());
        }

        stockInTransaction.setUpdatedBy(authService.authUser());
        stockInTransaction.setUpdatedAt(LocalDateTime.now());

        try {
            stockInTransactionRepo.save(stockInTransaction);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            stockInTransactionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            stockInTransactionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
