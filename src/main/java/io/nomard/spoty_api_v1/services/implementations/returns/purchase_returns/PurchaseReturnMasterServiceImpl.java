package io.nomard.spoty_api_v1.services.implementations.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.PurchaseReturnRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns.PurchaseReturnMasterService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PurchaseReturnMasterServiceImpl implements PurchaseReturnMasterService {
    @Autowired
    private PurchaseReturnRepository purchaseReturnMasterRepo;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<PurchaseReturnMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> purchaseReturnMasterRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(purchaseReturnMasterRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<PurchaseReturnMaster> getById(Long id) {
        return purchaseReturnMasterRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<PurchaseReturnMaster> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> purchaseReturnMasterRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(PurchaseReturnMaster purchase) {
        return authService.authUser()
                .flatMap(user -> {
                    // Calculate subTotal and total
                    double subTotal = purchase.getPurchaseReturnDetails().stream()
                            .mapToDouble(PurchaseReturnDetail::getSubTotalCost)
                            .sum();
                    double total = subTotal;

                    if (Objects.nonNull(purchase.getTax()) && purchase.getTax().getPercentage() > 0.0) {
                        total += total * (purchase.getTax().getPercentage() / 100);
                    }
                    if (Objects.nonNull(purchase.getDiscount()) && purchase.getDiscount().getPercentage() > 0.0) {
                        total += total * (purchase.getDiscount().getPercentage() / 100);
                    }

                    // Set calculated values and metadata
                    purchase.setSubTotal(subTotal);
                    purchase.setTotal(total);
                    purchase.setTenant(user.getTenant());
                    purchase.setBranch(user.getBranch());
                    purchase.setCreatedBy(user);
                    purchase.setCreatedAt(new Date());

                    double finalTotal = total;
                    return purchaseReturnMasterRepo.save(purchase)
                            .flatMap(savedPurchaseReturnMaster -> {
                                if (!Objects.equals(finalTotal, 0d)) {
                                    return accountService.getByContains(user.getTenant(), "Default Account")
                                            .flatMap(account -> {
                                                var accountTransaction = new AccountTransaction();
                                                accountTransaction.setTenant(user.getTenant());
                                                accountTransaction.setTransactionDate(new Date());
                                                accountTransaction.setAccount(account);
                                                accountTransaction.setAmount(finalTotal);
                                                accountTransaction.setCredit(finalTotal);
                                                accountTransaction.setTransactionType("Purchase Return");
                                                accountTransaction.setNote("Purchase returned");
                                                accountTransaction.setCreatedBy(user);
                                                accountTransaction.setCreatedAt(new Date());

                                                return accountTransactionService.save(accountTransaction)
                                                        .thenReturn(spotyResponseImpl.ok());
                                            })
                                            .thenReturn(spotyResponseImpl.ok());
                                } else {
                                    return Mono.just(spotyResponseImpl.ok());
                                }
                            })
                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                });
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(PurchaseReturnMaster data) {
        return purchaseReturnMasterRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Purchase not found")))
                .flatMap(purchase -> {
                    var subTotal = 0.00;
                    var total = 0.00;

                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        purchase.setRef(data.getRef());
                    }
                    if (Objects.nonNull(data.getDate())) {
                        purchase.setDate(data.getDate());
                    }
                    if (Objects.nonNull(data.getSupplier())) {
                        purchase.setSupplier(data.getSupplier());
                    }
                    if (Objects.nonNull(data.getBranch())) {
                        purchase.setBranch(data.getBranch());
                    }
                    if (Objects.nonNull(data.getPurchaseReturnDetails()) && !data.getPurchaseReturnDetails().isEmpty()) {
                        purchase.setPurchaseReturnDetails(data.getPurchaseReturnDetails());
                        if (Objects.nonNull(data.getTax())
                                && !Objects.equals(purchase.getTax().getPercentage(), data.getTax().getPercentage())
                                && purchase.getTax().getPercentage() > 0.0) {
                            total += subTotal * (data.getTax().getPercentage() / 100);
                        }
                        if (Objects.nonNull(data.getDiscount())
                                && !Objects.equals(purchase.getDiscount().getPercentage(), data.getDiscount().getPercentage())
                                && purchase.getTax().getPercentage() > 0.0) {
                            total += subTotal * (data.getDiscount().getPercentage() / 100);
                        }
                        for (int i = 0; i < data.getPurchaseReturnDetails().size(); i++) {
                            if (Objects.isNull(data.getPurchaseReturnDetails().get(i).getPurchaseReturn())) {
                                data.getPurchaseReturnDetails().get(i).setPurchaseReturn(purchase);
                            }
                            subTotal += data.getPurchaseReturnDetails().get(i).getSubTotalCost();
                        }
                    }
                    if (!Objects.equals(data.getTax(), purchase.getTax()) && Objects.nonNull(data.getTax())) {
                        purchase.setTax(data.getTax());
                    }
                    if (!Objects.equals(data.getDiscount(), purchase.getDiscount()) && Objects.nonNull(data.getDiscount())) {
                        purchase.setDiscount(data.getDiscount());
                    }
                    if (!Objects.equals(data.getDiscount(), purchase.getDiscount())) {
                        purchase.setDiscount(data.getDiscount());
                    }
                    if (!Objects.equals(data.getAmountPaid(), purchase.getAmountPaid())) {
                        purchase.setAmountPaid(data.getAmountPaid());
                    }
                    if (!Objects.equals(data.getTotal(), total)) {
                        purchase.setTotal(total);
                    }
                    if (!Objects.equals(data.getSubTotal(), subTotal)) {
                        purchase.setSubTotal(subTotal);
                    }
                    if (!Objects.equals(data.getAmountDue(), purchase.getAmountDue())) {
                        purchase.setAmountDue(data.getAmountDue());
                    }
                    if (Objects.nonNull(data.getPurchaseStatus()) && !"".equalsIgnoreCase(data.getPurchaseStatus())) {
                        purchase.setPurchaseStatus(data.getPurchaseStatus());
                    }
                    if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
                        purchase.setPaymentStatus(data.getPaymentStatus());
                    }
                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                        purchase.setNotes(data.getNotes());
                    }

                    return authService.authUser()
                            .flatMap(user -> {
                                purchase.setUpdatedBy(user);
                                purchase.setUpdatedAt(new Date());

                                return purchaseReturnMasterRepo.save(purchase)
                                        .thenReturn(spotyResponseImpl.ok());
                            })
                            .thenReturn(spotyResponseImpl.ok());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return purchaseReturnMasterRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return purchaseReturnMasterRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
