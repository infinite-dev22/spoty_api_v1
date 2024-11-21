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
import io.nomard.spoty_api_v1.services.interfaces.purchases.PurchaseService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.CoreUtils;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PurchaseDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.PurchaseMapper;
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
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;
    @Autowired
    private CoreCalculations.PurchaseCalculationService purchaseCalculationService;
    @Autowired
    private PurchaseMapper purchaseMapper;

    @Override
    public Page<PurchaseDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return purchaseRepo.findAllByTenantId(
                authService.authUser().getTenant().getId(),
                authService.authUser().getId(),
                pageRequest
        ).map(purchase -> purchaseMapper.toMasterDTO(purchase));
    }

    @Override
    public PurchaseDTO getById(Long id) throws NotFoundException {
        Optional<PurchaseMaster> purchase = purchaseRepo.findById(id);
        if (purchase.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseMapper.toMasterDTO(purchase.get());
    }

    @Override
    public List<PurchaseDTO> getByContains(String search) {
        return purchaseRepo.searchAll(
                        authService.authUser().getTenant().getId(),
                        authService.authUser().getId(),
                        search.toLowerCase()
                ).stream()
                .map(purchase -> purchaseMapper.toMasterDTO(purchase))
                .collect(Collectors.toList());
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
        if (settingsService.getSettingsInternal().getReview() && settingsService.getSettingsInternal().getApproveAdjustments()) {
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
                                settingsService.getSettingsInternal().getApprovalLevels()
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

    @Transactional
    @Override
    public ResponseEntity<ObjectNode> update(PurchaseMaster data) throws NotFoundException {
        var existingPurchase = purchaseRepo.findById(data.getId())
                .orElseThrow(() -> new NotFoundException("PurchaseMaster not found with ID: " + data.getId()));

        updateBasicFields(existingPurchase, data);
        updatePurchaseDetails(existingPurchase, data);
        purchaseCalculationService.calculate(existingPurchase);
        updateApprovalStatus(existingPurchase, data);

        existingPurchase.setUpdatedBy(authService.authUser());
        existingPurchase.setUpdatedAt(LocalDateTime.now());

        try {
            purchaseRepo.save(existingPurchase);
            updateProductCostPrice(existingPurchase);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.severe("Error updating PurchaseMaster: " + e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update PurchaseMaster. Please try again."
            );
        }
    }

    private void updateBasicFields(PurchaseMaster existingPurchase, PurchaseMaster data) {
        if (isNonEmpty(data.getRef())) {
            existingPurchase.setRef(data.getRef());
        }
        if (data.getDate() != null) {
            existingPurchase.setDate(data.getDate());
        }
        if (data.getSupplier() != null) {
            existingPurchase.setSupplier(data.getSupplier());
        }
        if (data.getBranch() != null) {
            existingPurchase.setBranch(data.getBranch());
        }
        if (data.getTax() != null) {
            existingPurchase.setTax(data.getTax());
        }
        if (data.getDiscount() != null) {
            existingPurchase.setDiscount(data.getDiscount());
        }
        if (data.getShippingFee() != 0) {
            existingPurchase.setShippingFee(data.getShippingFee());
        }
        if (data.getAmountPaid() != 0) {
            existingPurchase.setAmountPaid(data.getAmountPaid());
        }
        if (isNonEmpty(data.getPurchaseStatus())) {
            existingPurchase.setPurchaseStatus(data.getPurchaseStatus());
        }
        if (isNonEmpty(data.getPaymentStatus())) {
            existingPurchase.setPaymentStatus(data.getPaymentStatus());
        }
        if (isNonEmpty(data.getNotes())) {
            existingPurchase.setNotes(data.getNotes());
        }
    }

    // Update purchase details (handles additions, updates, and deletions)
    private void updatePurchaseDetails(PurchaseMaster existingPurchase, PurchaseMaster data) {List<PurchaseDetail> currentDetails = existingPurchase.getPurchaseDetails();
        List<PurchaseDetail> updatedDetails = data.getPurchaseDetails();

        // Map existing details by ID for quick lookup
        Map<Long, PurchaseDetail> existingDetailMap = currentDetails.stream()
                .collect(Collectors.toMap(PurchaseDetail::getId, detail -> detail));

        // Create a new list for updates
        List<PurchaseDetail> finalDetails = new ArrayList<>();

        // Process updated details
        for (PurchaseDetail newDetail : updatedDetails) {
            if (newDetail.getId() != null && existingDetailMap.containsKey(newDetail.getId())) {
                // Update existing detail
                var existingDetail = existingDetailMap.get(newDetail.getId());
                updateDetailFields(existingDetail, newDetail);
                finalDetails.add(existingDetail); // Retain reference to the existing object
            } else {
                // Add new detail
                newDetail.setPurchase(existingPurchase); // Set the parent reference
                finalDetails.add(newDetail);
            }
        }

        // Remove orphaned details
        currentDetails.removeIf(existingDetail -> updatedDetails.stream()
                .noneMatch(updatedDetail -> Objects.equals(existingDetail.getId(), updatedDetail.getId())));

        // Add remaining details to the collection (maintain the same reference)
        currentDetails.clear();
        currentDetails.addAll(finalDetails);
    }

    private void updateDetailFields(PurchaseDetail existingDetail, PurchaseDetail newDetail) {
        existingDetail.setProduct(newDetail.getProduct());
        existingDetail.setUnitCost(newDetail.getUnitCost());
        existingDetail.setQuantity(newDetail.getQuantity());
        existingDetail.setTotalCost(newDetail.getTotalCost());
        existingDetail.setPurchase(newDetail.getPurchase());
    }

    private void updateApprovalStatus(PurchaseMaster purchase, PurchaseMaster data) {
        if (data.getReviewers() != null && !data.getReviewers().isEmpty()) {
            purchase.getReviewers().add(data.getReviewers().getFirst());
            if (purchase.getNextApprovedLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                purchase.setApproved(true);
                purchase.setApprovalStatus("Approved");
                purchase.setPurchaseStatus("Ordered");
            }
        }
    }

    private void updateProductCostPrice(PurchaseMaster purchase) throws NotFoundException {
        for (PurchaseDetail detail : purchase.getPurchaseDetails()) {
            var product = productService.getByIdInternally(detail.getProduct().getId());
            if (!Objects.equals(product.getCostPrice(), detail.getUnitCost())) {
                product.setCostPrice(detail.getUnitCost());
                productService.save(product);
            }
        }
    }

    private boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
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
                            settingsService.getSettingsInternal().getApprovalLevels()
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
    public ResponseEntity<ObjectNode> cancel(Long id)
            throws NotFoundException {
        var opt = purchaseRepo.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchase = opt.get();
        purchase.setPurchaseStatus("Cancelled");
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
