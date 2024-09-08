package io.nomard.spoty_api_v1.services.implementations.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.DiscountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.TaxServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.purchases.PurchaseMasterService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.CoreUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
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
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private TaxServiceImpl taxService;
    @Autowired
    private DiscountServiceImpl discountService;

    @Override
    public Page<PurchaseMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return purchaseMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
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
    public ResponseEntity<ObjectNode> save(PurchaseMaster purchaseMaster) throws NotFoundException {
        // Perform calculations
        var calculationService = new CoreCalculations.PurchaseCalculationService(taxService, discountService);
        calculationService.calculate(purchaseMaster);

        // Set additional details
        purchaseMaster.setTenant(authService.authUser().getTenant());
        if (purchaseMaster.getBranch() == null) {
            purchaseMaster.setBranch(authService.authUser().getBranch());
        }
        purchaseMaster.setRef(CoreUtils.referenceNumberGenerator("PUR"));
        purchaseMaster.setCreatedBy(authService.authUser());
        purchaseMaster.setCreatedAt(LocalDateTime.now());

        try {
            purchaseMasterRepo.saveAndFlush(purchaseMaster);

            // Create account transaction of this purchase.
            var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(purchaseMaster.getTotal());
            accountTransaction.setTransactionType("Purchase");
            accountTransaction.setNote("Purchase made");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionService.save(accountTransaction);

            // Check if product cost price needs to be updated.
            for (PurchaseDetail detail : purchaseMaster.getPurchaseDetails()) {
                var product = productService.getById(detail.getProduct().getId());
                if (!Objects.equals(product.getCostPrice(), detail.getUnitCost())) {
                    product.setCostPrice(detail.getUnitCost());
                    productService.save(product);
                }
            }

            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(PurchaseMaster data) throws NotFoundException {
        var opt = purchaseMasterRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchaseMaster = opt.get();

        // Update fields as needed
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
        }

        // Perform calculations
        var calculationService = new CoreCalculations.PurchaseCalculationService(taxService, discountService);
        calculationService.calculate(purchaseMaster);

        // Update other fields
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

            // Check if product cost price needs to be updated.
            for (PurchaseDetail detail : purchaseMaster.getPurchaseDetails()) {
                var product = productService.getById(detail.getProduct().getId());
                if (!Objects.equals(product.getCostPrice(), detail.getUnitCost())) {
                    product.setCostPrice(detail.getUnitCost());
                    productService.save(product);
                }
            }

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    /*public ResponseEntity<ObjectNode> update(PurchaseMaster data) throws NotFoundException {
        var opt = purchaseMasterRepo.findById(data.getId());
        var subTotal = 0.00;
        var total = 0.00;
        var tax = 0.00;
        var discount = 0.00;
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
            for (int i = 0; i < data.getPurchaseDetails().size(); i++) {
                if (Objects.isNull(data.getPurchaseDetails().get(i).getPurchase())) {
                    data.getPurchaseDetails().get(i).setPurchase(purchaseMaster);
                }
                subTotal += data.getPurchaseDetails().get(i).getUnitCost() * purchaseMaster.getPurchaseDetails().get(i).getQuantity();
                total += subTotal;
            }
            if (Objects.nonNull(purchaseMaster.getTax())) {
                tax += total * (purchaseMaster.getTax().getPercentage() / 100);
            }
            if (Objects.nonNull(purchaseMaster.getDiscount())) {
                discount += total * (purchaseMaster.getDiscount().getPercentage() / 100);
            }
        }
        if (!Objects.equals(data.getTax(), purchaseMaster.getTax()) && Objects.nonNull(data.getTax())) {
            purchaseMaster.setTax(data.getTax());
            total += tax;
        }
        if (!Objects.equals(data.getDiscount(), purchaseMaster.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            purchaseMaster.setDiscount(data.getDiscount());
            total -= discount;
        }
        if (!Objects.equals(data.getDiscount(), purchaseMaster.getDiscount())) {
            purchaseMaster.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getAmountPaid(), purchaseMaster.getAmountPaid())) {
            purchaseMaster.setAmountPaid(data.getAmountPaid());
            total += total - purchaseMaster.getAmountPaid();
        }
        if (!Objects.equals(data.getShippingFee(), purchaseMaster.getShippingFee())) {
            total += purchaseMaster.getShippingFee();
            purchaseMaster.setShippingFee(data.getShippingFee());
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
            // Check if product cost price for each product in the purchase is still the same else update it.
            for (int i = 0; i < purchaseMaster.getPurchaseDetails().size(); i++) {
                var product = productService.getById(purchaseMaster.getPurchaseDetails().get(i).getProduct().getId());
                if (!Objects.equals(product.getCostPrice(), purchaseMaster.getPurchaseDetails().get(i).getUnitCost())) {
                    product.setCostPrice(purchaseMaster.getPurchaseDetails().get(i).getUnitCost());
                    productService.save(product);
                }
            }
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }*/

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            purchaseMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
