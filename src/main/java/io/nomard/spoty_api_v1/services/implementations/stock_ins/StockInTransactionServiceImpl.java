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
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class StockInTransactionServiceImpl implements StockInTransactionService {
    @Autowired
    private StockInTransactionRepository stockInTransactionRepo;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("stockIn_transactions")
    @Transactional(readOnly = true)
    public Mono<StockInTransaction> getById(Long id) {
        return stockInTransactionRepo.findByStockInDetailId(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Adjustment detail not found")));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(StockInDetail stockInDetail) {
        return productService.getById(stockInDetail.getProduct().getId())
                .flatMap(product -> {
                    var productQuantity = product.getQuantity() + stockInDetail.getQuantity();
                    product.setQuantity(productQuantity);
                    return productService.update(product, null)
                            .then(authService.authUser())
                            .flatMap(user -> {
                                StockInTransaction stockInTransaction = new StockInTransaction();
                                stockInTransaction.setBranch(stockInDetail.getStockIn().getBranch());
                                stockInTransaction.setProduct(stockInDetail.getProduct());
                                stockInTransaction.setStockInDetail(stockInDetail);
                                stockInTransaction.setDate(new Date());
                                stockInTransaction.setStockInQuantity(stockInDetail.getQuantity());
                                stockInTransaction.setTenant(user.getTenant());
                                stockInTransaction.setBranch(user.getBranch());
                                stockInTransaction.setCreatedBy(user);
                                stockInTransaction.setCreatedAt(new Date());
                                return stockInTransactionRepo.save(stockInTransaction);
                            });
                })
                .map(savedTransaction -> spotyResponseImpl.created())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @CacheEvict(value = "stockIn_transactions", key = "#data.id")
    public Mono<ResponseEntity<ObjectNode>> update(StockInDetail data) {
        return stockInTransactionRepo.findByStockInDetailId(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(stockInTransaction -> {
                    Mono<Void> updateProduct = Mono.empty();

                    if (!Objects.equals(stockInTransaction.getBranch(), data.getStockIn().getBranch()) && Objects.nonNull(data.getStockIn().getBranch())) {
                        stockInTransaction.setBranch(data.getStockIn().getBranch());
                    }

                    if (!Objects.equals(stockInTransaction.getProduct(), data.getProduct()) && Objects.nonNull(data.getProduct())) {
                        var adjustQuantity = stockInTransaction.getStockInQuantity();
                        updateProduct = productService.getById(data.getProduct().getId())
                                .flatMap(product -> {
                                    var currentProductQuantity = product.getQuantity();
                                    var productQuantity = (currentProductQuantity - adjustQuantity) + data.getQuantity();
                                    product.setQuantity(productQuantity);
                                    return productService.update(product, null);
                                }).then();
                    }

                    stockInTransaction.setProduct(data.getProduct());

                    if (!Objects.equals(stockInTransaction.getStockInDetail(), data)) {
                        stockInTransaction.setStockInDetail(data);
                    }

                    if (!Objects.equals(stockInTransaction.getStockInDetail().getCreatedAt(), data.getCreatedAt()) && Objects.nonNull(data.getCreatedAt())) {
                        stockInTransaction.setDate(data.getCreatedAt());
                    }

                    if (!Objects.equals(stockInTransaction.getStockInQuantity(), data.getQuantity())) {
                        stockInTransaction.setStockInQuantity(data.getQuantity());
                    }

                    Mono<Void> finalUpdateProduct = updateProduct;
                    return authService.authUser()
                            .flatMap(user -> {
                                stockInTransaction.setUpdatedBy(user);
                                stockInTransaction.setUpdatedAt(new Date());
                                return finalUpdateProduct.then(stockInTransactionRepo.save(stockInTransaction));
                            });
                })
                .map(savedTransaction -> spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return stockInTransactionRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return stockInTransactionRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
