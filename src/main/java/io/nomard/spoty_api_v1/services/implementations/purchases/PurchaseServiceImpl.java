package io.nomard.spoty_api_v1.services.implementations.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.DiscountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.TaxServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.purchases.PurchaseService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.CoreUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMasterRepository purchaseRepo;

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

    @Autowired
    private TenantSettingsServiceImpl settingsService;

    @Autowired
    private ApproverServiceImpl approverService;

    @Autowired
    private CoreCalculations.PurchaseCalculationService purchaseCalculationService;

    @Override
    public Page<PurchaseMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return purchaseRepo.findAllByTenantId(
                authService.authUser().getTenant().getId(),
                authService.authUser().getId(),
                pageRequest
        );
    }

    @Override
    public PurchaseMaster getById(Long id) throws NotFoundException {
        Optional<PurchaseMaster> purchase = purchaseRepo.findById(id);
        if (purchase.isEmpty()) {
            throw new NotFoundException();
        }
        return purchase.get();
    }

    @Override
    public ArrayList<PurchaseMaster> getByContains(String search) {
        return purchaseRepo.searchAll(
                authService.authUser().getTenant().getId(),
                search.toLowerCase()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(PurchaseMaster purchase)
            throws NotFoundException {
        // Perform calculations
        purchaseCalculationService.calculate(purchase);

        // Set additional details
        purchase.setTenant(authService.authUser().getTenant());
        if (purchase.getBranch() == null) {
            purchase.setBranch(authService.authUser().getBranch());
        }
        if (settingsService.getSettings().getReview() && settingsService.getSettings().getApproveAdjustments()) {
            Reviewer reviewer = null;
            try {
                reviewer = approverService.getByUserId(
                        authService.authUser().getId()
                );
            } catch (NotFoundException e) {
                log.log(Level.ALL, e.getMessage(), e);
            }
            if (Objects.nonNull(reviewer)) {
                purchase.getReviewers().add(reviewer);
                purchase.setNextApprovedLevel(reviewer.getLevel());
                if (
                        reviewer.getLevel() >=
                                settingsService.getSettings().getApprovalLevels()
                ) {
                    purchase.setApproved(true);
                    purchase.setApprovalStatus("Approved");
                    purchase.setPurchaseStatus("Ordered");
                }
            } else {
                purchase.setNextApprovedLevel(1);
                purchase.setApproved(false);
            }
            purchase.setApprovalStatus("Pending");
            purchase.setPurchaseStatus("Pending");
            purchase.setPaymentStatus("UnPaid");
        } else {
            purchase.setApproved(true);
            purchase.setApprovalStatus("Approved");

            // Create account transaction of this purchase.
            var account = accountService.getByContains(
                    authService.authUser().getTenant(),
                    "Default Account"
            );
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(purchase.getTotal());
            accountTransaction.setTransactionType("Purchase");
            accountTransaction.setNote("Purchase made");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionService.save(accountTransaction);

            // Check if product cost price needs to be updated.
            for (PurchaseDetail detail : purchase.getPurchaseDetails()) {
                var product = productService.getByIdInternally(
                        detail.getProduct().getId()
                );
                if (
                        !Objects.equals(
                                product.getCostPrice(),
                                detail.getUnitCost()
                        )
                ) {
                    product.setCostPrice(detail.getUnitCost());
                    productService.save(product);
                }
            }
        }
        purchase.setRef(CoreUtils.referenceNumberGenerator("PUR"));
        purchase.setCreatedBy(authService.authUser());
        purchase.setCreatedAt(LocalDateTime.now());

        try {
            purchaseRepo.save(purchase);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(PurchaseMaster data)
            throws NotFoundException {
        var opt = purchaseRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchase = opt.get();

        // Update fields as needed
        if (
                Objects.nonNull(data.getRef()) &&
                        !"".equalsIgnoreCase(data.getRef())
        ) {
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
        if (
                Objects.nonNull(data.getPurchaseDetails()) &&
                        !data.getPurchaseDetails().isEmpty()
        ) {
            purchase.setPurchaseDetails(data.getPurchaseDetails());
        }

        // Perform calculations
        purchaseCalculationService.calculate(purchase);

        // Update other fields
        if (
                Objects.nonNull(data.getPurchaseStatus()) &&
                        !"".equalsIgnoreCase(data.getPurchaseStatus())
        ) {
            purchase.setPurchaseStatus(data.getPurchaseStatus());
        }
        if (
                Objects.nonNull(data.getPaymentStatus()) &&
                        !"".equalsIgnoreCase(data.getPaymentStatus())
        ) {
            purchase.setPaymentStatus(data.getPaymentStatus());
        }
        if (
                Objects.nonNull(data.getNotes()) &&
                        !"".equalsIgnoreCase(data.getNotes())
        ) {
            purchase.setNotes(data.getNotes());
        }
        if (
                Objects.nonNull(data.getReviewers()) &&
                        !data.getReviewers().isEmpty()
        ) {
            purchase.getReviewers().add(data.getReviewers().getFirst());
            if (
                    purchase.getNextApprovedLevel() >=
                            settingsService.getSettings().getApprovalLevels()
            ) {
                purchase.setApproved(true);
                purchase.setApprovalStatus("Approved");
                purchase.setPurchaseStatus("Ordered");
            }
        }

        purchase.setUpdatedBy(authService.authUser());
        purchase.setUpdatedAt(LocalDateTime.now());

        try {
            purchaseRepo.save(purchase);

            // Check if product cost price needs to be updated.
            for (PurchaseDetail detail : purchase.getPurchaseDetails()) {
                var product = productService.getByIdInternally(
                        detail.getProduct().getId()
                );
                if (
                        !Objects.equals(
                                product.getCostPrice(),
                                detail.getUnitCost()
                        )
                ) {
                    product.setCostPrice(detail.getUnitCost());
                    productService.save(product);
                }
            }

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    /*public ResponseEntity<ObjectNode> update(PurchaseMaster data) throws NotFoundException {
        var opt = purchaseRepo.findById(data.getId());
        var subTotal = 0.00;
        var total = 0.00;
        var tax = 0.00;
        var discount = 0.00;
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchase = opt.get();

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
        if (Objects.nonNull(data.getPurchaseDetails()) && !data.getPurchaseDetails().isEmpty()) {
            purchase.setPurchaseDetails(data.getPurchaseDetails());
            for (int i = 0; i < data.getPurchaseDetails().size(); i++) {
                if (Objects.isNull(data.getPurchaseDetails().get(i).getPurchase())) {
                    data.getPurchaseDetails().get(i).setPurchase(purchase);
                }
                subTotal += data.getPurchaseDetails().get(i).getUnitCost() * purchase.getPurchaseDetails().get(i).getQuantity();
                total += subTotal;
            }
            if (Objects.nonNull(purchase.getTax())) {
                tax += total * (purchase.getTax().getPercentage() / 100);
            }
            if (Objects.nonNull(purchase.getDiscount())) {
                discount += total * (purchase.getDiscount().getPercentage() / 100);
            }
        }
        if (!Objects.equals(data.getTax(), purchase.getTax()) && Objects.nonNull(data.getTax())) {
            purchase.setTax(data.getTax());
            total += tax;
        }
        if (!Objects.equals(data.getDiscount(), purchase.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            purchase.setDiscount(data.getDiscount());
            total -= discount;
        }
        if (!Objects.equals(data.getDiscount(), purchase.getDiscount())) {
            purchase.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getAmountPaid(), purchase.getAmountPaid())) {
            purchase.setAmountPaid(data.getAmountPaid());
            total += total - purchase.getAmountPaid();
        }
        if (!Objects.equals(data.getShippingFee(), purchase.getShippingFee())) {
            total += purchase.getShippingFee();
            purchase.setShippingFee(data.getShippingFee());
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
        purchase.setUpdatedBy(authService.authUser());
        purchase.setUpdatedAt(LocalDateTime.now());
        try {
            purchaseRepo.save(purchase);
            // Check if product cost price for each product in the purchase is still the same else update it.
            for (int i = 0; i < purchase.getPurchaseDetails().size(); i++) {
                var product = productService.getById(purchase.getPurchaseDetails().get(i).getProduct().getId());
                if (!Objects.equals(product.getCostPrice(), purchase.getPurchaseDetails().get(i).getUnitCost())) {
                    product.setCostPrice(purchase.getPurchaseDetails().get(i).getUnitCost());
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
    @CacheEvict(value = "adjustment_masters", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel)
            throws NotFoundException {
        var opt = purchaseRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchase = opt.get();

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")
        ) {
            purchase.setApproved(false);
            purchase.setNextApprovedLevel(
                    purchase.getNextApprovedLevel() - 1
            );
            purchase.setApprovalStatus("Returned");
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")
        ) {
            var approver = approverService.getByUserId(
                    authService.authUser().getId()
            );
            purchase.getReviewers().add(approver);
            purchase.setNextApprovedLevel(approver.getLevel());
            if (
                    purchase.getNextApprovedLevel() >=
                            settingsService.getSettings().getApprovalLevels()
            ) {
                purchase.setApproved(true);
                purchase.setApprovalStatus("Approved");
                purchase.setPurchaseStatus("Ordered");

                // Create account transaction of this purchase.
                var account = accountService.getByContains(
                        authService.authUser().getTenant(),
                        "Default Account"
                );
                var accountTransaction = new AccountTransaction();
                accountTransaction.setTenant(
                        authService.authUser().getTenant()
                );
                accountTransaction.setTransactionDate(LocalDateTime.now());
                accountTransaction.setAccount(account);
                accountTransaction.setAmount(purchase.getTotal());
                accountTransaction.setTransactionType("Purchase");
                accountTransaction.setNote("Purchase made");
                accountTransaction.setCreatedBy(authService.authUser());
                accountTransaction.setCreatedAt(LocalDateTime.now());
                accountTransactionService.save(accountTransaction);

                // Check if product cost price needs to be updated.
                for (PurchaseDetail detail : purchase.getPurchaseDetails()) {
                    var product = productService.getByIdInternally(
                            detail.getProduct().getId()
                    );
                    if (
                            !Objects.equals(
                                    product.getCostPrice(),
                                    detail.getUnitCost()
                            )
                    ) {
                        product.setCostPrice(detail.getUnitCost());
                        productService.save(product);
                    }
                }
            }
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")
        ) {
            purchase.setApproved(false);
            purchase.setApprovalStatus("Rejected");
            purchase.setNextApprovedLevel(0);
        }

        purchase.setUpdatedBy(authService.authUser());
        purchase.setUpdatedAt(LocalDateTime.now());
        try {
            purchaseRepo.save(purchase);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            purchaseRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }
}
