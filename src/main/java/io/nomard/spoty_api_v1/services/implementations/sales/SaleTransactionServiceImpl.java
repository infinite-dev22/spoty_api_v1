package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

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
    public Flux<SaleTransaction> getById(Long saleDetailId) {
        return authService.authUser()
                .flatMapMany(user -> saleTransactionRepo.findSaleDetail(saleDetailId)
                        .switchIfEmpty(Mono.error(new NotFoundException("Sale detail detail not found"))
                        ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(SaleDetail saleDetail) {
        return productService.getById(saleDetail.getProduct().getId())
                .flatMap(product -> {
                    if (product.getQuantity() > 0 && product.getQuantity() >= saleDetail.getQuantity()) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    var productQuantity = product.getQuantity() - saleDetail.getQuantity();
                                    product.setQuantity(productQuantity);
                                    return productService.update(product, null)
                                            .flatMap(updatedProduct -> {
                                                SaleTransaction saleTransaction = new SaleTransaction();
                                                saleTransaction.setBranch(saleDetail.getSale().getBranch());
                                                saleTransaction.setProduct(saleDetail.getProduct());
                                                saleTransaction.setSaleDetail(saleDetail);
                                                saleTransaction.setDate(new Date());
                                                saleTransaction.setSaleQuantity(saleDetail.getQuantity());
                                                saleTransaction.setTenant(user.getTenant());
                                                saleTransaction.setBranch(user.getBranch());
                                                saleTransaction.setCreatedBy(user);
                                                saleTransaction.setCreatedAt(new Date());
                                                return saleTransactionRepo.save(saleTransaction)
                                                        .thenReturn(spotyResponseImpl.ok());
                                            })
                                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                                })
                                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                    } else {
                        return Mono.just(spotyResponseImpl.custom(HttpStatus.NOT_ACCEPTABLE, "Product quantity is too low"));
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> saveReturn(SaleReturnDetail saleReturnDetail) {
        return productService.getById(saleReturnDetail.getProduct().getId())
                .flatMap(product -> {
                    if (product.getQuantity() > 0 && product.getQuantity() >= saleReturnDetail.getQuantity()) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    var productQuantity = product.getQuantity() - saleReturnDetail.getQuantity();
                                    product.setQuantity(productQuantity);
                                    return productService.update(product, null)
                                            .flatMap(updatedProduct -> {
                                                SaleTransaction saleTransaction = new SaleTransaction();
                                                saleTransaction.setBranch(saleReturnDetail.getSale().getBranch());
                                                saleTransaction.setProduct(saleReturnDetail.getProduct());
                                                saleTransaction.setSaleReturnDetail(saleReturnDetail);
                                                saleTransaction.setDate(new Date());
                                                saleTransaction.setSaleQuantity(saleReturnDetail.getQuantity());
                                                saleTransaction.setTenant(user.getTenant());
                                                saleTransaction.setBranch(user.getBranch());
                                                saleTransaction.setCreatedBy(user);
                                                saleTransaction.setCreatedAt(new Date());
                                                return saleTransactionRepo.save(saleTransaction)
                                                        .thenReturn(spotyResponseImpl.ok());
                                            })
                                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                                })
                                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                    } else {
                        return Mono.just(spotyResponseImpl.custom(HttpStatus.NOT_ACCEPTABLE, "Product quantity is too low"));
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
