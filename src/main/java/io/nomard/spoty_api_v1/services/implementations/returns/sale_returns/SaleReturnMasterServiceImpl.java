package io.nomard.spoty_api_v1.services.implementations.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.SaleReturnRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns.SaleReturnMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SaleReturnMasterServiceImpl implements SaleReturnMasterService {
    @Autowired
    private SaleReturnRepository saleReturnRepo;
    @Autowired
    private SaleTransactionServiceImpl saleTransactionService;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public Flux<PageImpl<SaleReturnMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> saleReturnRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(saleReturnRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public Mono<SaleReturnMaster> getById(Long id) {
        return saleReturnRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public Flux<SaleReturnMaster> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> saleReturnRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(SaleReturnMaster sale) {
        return authService.authUser()
                .flatMap(user -> {
                    // Calculate subTotal and total
                    double subTotal = sale.getSaleDetails().stream()
                            .mapToDouble(SaleReturnDetail::getSubTotalPrice)
                            .sum();
                    double total = subTotal;

                    if (Objects.nonNull(sale.getTax()) && sale.getTax().getPercentage() > 0.0) {
                        total += total * (sale.getTax().getPercentage() / 100);
                    }
                    if (Objects.nonNull(sale.getDiscount()) && sale.getDiscount().getPercentage() > 0.0) {
                        total += total * (sale.getDiscount().getPercentage() / 100);
                    }

                    // Set calculated values and metadata
                    sale.setSubTotal(subTotal);
                    sale.setTotal(total);
                    sale.setTenant(user.getTenant());
                    sale.setBranch(user.getBranch());
                    sale.setCreatedBy(user);
                    sale.setCreatedAt(new Date());

                    double finalTotal = total;
                    return saleReturnRepo.save(sale)
                            .flatMap(savedSaleReturnMaster -> {
                                Mono<Void> transactionProcessing = Mono.empty();

                                if (!Objects.equals(finalTotal, 0d)) {
                                    transactionProcessing = accountService.getByContains(user.getTenant(), "Default Account")
                                            .flatMap(account -> {
                                                var accountTransaction = new AccountTransaction();
                                                accountTransaction.setTenant(user.getTenant());
                                                accountTransaction.setTransactionDate(new Date());
                                                accountTransaction.setAccount(account);
                                                accountTransaction.setAmount(finalTotal);
                                                accountTransaction.setDebit(finalTotal);
                                                accountTransaction.setTransactionType("Sale");
                                                accountTransaction.setNote("Sale made");
                                                accountTransaction.setCreatedBy(user);
                                                accountTransaction.setCreatedAt(new Date());

                                                return accountTransactionService.save(accountTransaction);
                                            }).then();
                                }

                                return transactionProcessing.then(
                                        Flux.fromIterable(savedSaleReturnMaster.getSaleDetails())
                                                .flatMap(saleTransactionService::saveReturn)
                                                .then()
                                ).then(Mono.just(spotyResponseImpl.created()));
                            })
                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @CacheEvict(value = "sale_masters", key = "#data.id")
    public Mono<ResponseEntity<ObjectNode>> update(SaleReturnMaster data) {
        return saleReturnRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Sale not found")))
                .flatMap(saleReturn -> {
                    final double[] subTotal = {0.00};
                    var total = 0.00;
                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        saleReturn.setRef(data.getRef());
                    }
                    if (!Objects.equals(saleReturn.getCustomer(), data.getCustomer()) && Objects.nonNull(data.getCustomer())) {
                        saleReturn.setCustomer(data.getCustomer());
                    }
                    if (!Objects.equals(saleReturn.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
                        saleReturn.setBranch(data.getBranch());
                    }
                    if (Objects.nonNull(data.getSaleDetails()) && !data.getSaleDetails().isEmpty()) {
                        saleReturn.setSaleDetails(data.getSaleDetails());
                        if (Objects.nonNull(data.getTax())
                                && !Objects.equals(saleReturn.getTax().getPercentage(), data.getTax().getPercentage())
                                && saleReturn.getTax().getPercentage() > 0.0) {
                            total += subTotal[0] * (data.getTax().getPercentage() / 100);
                        }
                        if (Objects.nonNull(data.getDiscount())
                                && !Objects.equals(saleReturn.getDiscount().getPercentage(), data.getDiscount().getPercentage())
                                && saleReturn.getTax().getPercentage() > 0.0) {
                            total += subTotal[0] * (data.getDiscount().getPercentage() / 100);
                        }
                        Flux.fromIterable(data.getSaleDetails())
                                .flatMap(saleReturnDetail -> {
                                    if (Objects.isNull(saleReturnDetail.getSale())) {
                                        saleReturnDetail.setSale(saleReturn);
                                    }
                                    subTotal[0] += saleReturnDetail.getSubTotalPrice();
                                    return saleTransactionService.saveReturn(saleReturnDetail)
                                            .thenReturn(spotyResponseImpl.ok());
                                })
                                .then(Mono.just(spotyResponseImpl.created()))
                                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                    }
                    if (!Objects.equals(data.getTax(), saleReturn.getTax()) && Objects.nonNull(data.getTax())) {
                        saleReturn.setTax(data.getTax());
                    }
                    if (!Objects.equals(data.getDiscount(), saleReturn.getDiscount()) && Objects.nonNull(data.getDiscount())) {
                        saleReturn.setDiscount(data.getDiscount());
                    }
                    if (!Objects.equals(data.getTotal(), total)) {
                        saleReturn.setTotal(total);
                    }
                    if (!Objects.equals(data.getSubTotal(), subTotal[0])) {
                        saleReturn.setSubTotal(subTotal[0]);
                    }
                    if (!Objects.equals(data.getAmountPaid(), saleReturn.getAmountPaid())) {
                        saleReturn.setAmountPaid(data.getAmountPaid());
                    }
                    if (!Objects.equals(data.getAmountDue(), saleReturn.getAmountDue())) {
                        saleReturn.setAmountDue(data.getAmountDue());
                    }
                    if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
                        saleReturn.setPaymentStatus(data.getPaymentStatus());
                    }
                    if (Objects.nonNull(data.getSaleStatus()) && !"".equalsIgnoreCase(data.getSaleStatus())) {
                        saleReturn.setSaleStatus(data.getSaleStatus());
                    }
                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                        saleReturn.setNotes(data.getNotes());
                    }

                    return authService.authUser()
                            .flatMap(user -> {
                                saleReturn.setUpdatedBy(user);
                                saleReturn.setUpdatedAt(new Date());

                                return saleReturnRepo.save(saleReturn)
                                        .thenReturn(spotyResponseImpl.ok());
                            })
                            .thenReturn(spotyResponseImpl.ok());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return saleReturnRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return saleReturnRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
