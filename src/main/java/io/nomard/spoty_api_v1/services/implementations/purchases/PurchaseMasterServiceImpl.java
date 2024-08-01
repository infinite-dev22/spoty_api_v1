package io.nomard.spoty_api_v1.services.implementations.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.purchases.PurchaseMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PurchaseMasterServiceImpl implements PurchaseMasterService {
    @Autowired
    private PurchaseMasterRepository purchaseMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AccountServiceImpl accountService;

    @Override
    public List<PurchaseMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<PurchaseMaster> page = purchaseMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        return page.getContent();
    }

    @Override
    public PurchaseMaster getById(Long id) throws NotFoundException {
        Optional<PurchaseMaster> purchaseMaster = purchaseMasterRepo.findById(id);
        if (purchaseMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseMaster.get();
    }

    @Override
    public ArrayList<PurchaseMaster> getByContains(String search) {
        return purchaseMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(PurchaseMaster purchaseMaster) {
        var subTotal = 0.00;
        var total = 0.00;
        for (int i = 0; i < purchaseMaster.getPurchaseDetails().size(); i++) {
            purchaseMaster.getPurchaseDetails().get(i).setPurchase(purchaseMaster);
            subTotal += purchaseMaster.getPurchaseDetails().get(i).getSubTotalCost();
            total = subTotal;
        }
        if (Objects.nonNull(purchaseMaster.getTax()) && purchaseMaster.getTax().getPercentage() > 0.0) {
            total += total * (purchaseMaster.getTax().getPercentage() / 100);
        }
        if (Objects.nonNull(purchaseMaster.getDiscount()) && purchaseMaster.getDiscount().getPercentage() > 0.0) {
            total += total * (purchaseMaster.getDiscount().getPercentage() / 100);
        }
        purchaseMaster.setSubTotal(subTotal);
        purchaseMaster.setTotal(total);
        purchaseMaster.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(purchaseMaster.getBranch())) {
            purchaseMaster.setBranch(authService.authUser().getBranch());
        }
        if (!Objects.equals(total, 0d)) {
            var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(total);
            accountTransaction.setTransactionType("Purchase");
            accountTransaction.setNote("Purchase made");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionService.save(accountTransaction);
        }
        purchaseMaster.setCreatedBy(authService.authUser());
        purchaseMaster.setCreatedAt(LocalDateTime.now());
        try {
            purchaseMasterRepo.saveAndFlush(purchaseMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
//    @Transactional
    public ResponseEntity<ObjectNode> update(PurchaseMaster data) throws NotFoundException {
        var opt = purchaseMasterRepo.findById(data.getId());
        var subTotal = 0.00;
        var total = 0.00;
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            purchaseMaster.setRef(data.getRef());
        }
        if (Objects.nonNull(data.getDate())) {
            purchaseMaster.setDate(data.getDate());
        }
        if (Objects.nonNull(data.getSupplier())) {
            purchaseMaster.setSupplier(data.getSupplier());
        }
        if (Objects.nonNull(data.getBranch())) {
            purchaseMaster.setBranch(data.getBranch());
        }
        if (Objects.nonNull(data.getPurchaseDetails()) && !data.getPurchaseDetails().isEmpty()) {
            purchaseMaster.setPurchaseDetails(data.getPurchaseDetails());
            if (Objects.nonNull(data.getTax())
                    && !Objects.equals(purchaseMaster.getTax().getPercentage(), data.getTax().getPercentage())
                    && purchaseMaster.getTax().getPercentage() > 0.0) {
                total += subTotal * (data.getTax().getPercentage() / 100);
            }
            if (Objects.nonNull(data.getDiscount())
                    && !Objects.equals(purchaseMaster.getDiscount().getPercentage(), data.getDiscount().getPercentage())
                    && purchaseMaster.getTax().getPercentage() > 0.0) {
                total += subTotal * (data.getDiscount().getPercentage() / 100);
            }
            for (int i = 0; i < data.getPurchaseDetails().size(); i++) {
                if (Objects.isNull(data.getPurchaseDetails().get(i).getPurchase())) {
                    data.getPurchaseDetails().get(i).setPurchase(purchaseMaster);
                }
                subTotal += data.getPurchaseDetails().get(i).getSubTotalCost();
            }
        }
        if (!Objects.equals(data.getTax(), purchaseMaster.getTax()) && Objects.nonNull(data.getTax())) {
            purchaseMaster.setTax(data.getTax());
        }
        if (!Objects.equals(data.getDiscount(), purchaseMaster.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            purchaseMaster.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getDiscount(), purchaseMaster.getDiscount())) {
            purchaseMaster.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getAmountPaid(), purchaseMaster.getAmountPaid())) {
            purchaseMaster.setAmountPaid(data.getAmountPaid());
        }
        if (!Objects.equals(data.getTotal(), total)) {
            purchaseMaster.setTotal(total);
        }
        if (!Objects.equals(data.getSubTotal(), subTotal)) {
            purchaseMaster.setSubTotal(subTotal);
        }
        if (!Objects.equals(data.getAmountDue(), purchaseMaster.getAmountDue())) {
            purchaseMaster.setAmountDue(data.getAmountDue());
        }
        if (Objects.nonNull(data.getPurchaseStatus()) && !"".equalsIgnoreCase(data.getPurchaseStatus())) {
            purchaseMaster.setPurchaseStatus(data.getPurchaseStatus());
        }
        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            purchaseMaster.setPaymentStatus(data.getPaymentStatus());
        }
        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            purchaseMaster.setNotes(data.getNotes());
        }
        purchaseMaster.setUpdatedBy(authService.authUser());
        purchaseMaster.setUpdatedAt(LocalDateTime.now());
        try {
            purchaseMasterRepo.saveAndFlush(purchaseMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            purchaseMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
