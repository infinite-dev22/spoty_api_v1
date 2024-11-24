package io.nomard.spoty_api_v1.services.implementations.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.SupplierRepository;
import io.nomard.spoty_api_v1.repositories.deductions.DiscountRepository;
import io.nomard.spoty_api_v1.repositories.deductions.TaxRepository;
import io.nomard.spoty_api_v1.repositories.returns.purchase_returns.PurchaseReturnMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns.PurchaseReturnService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PurchaseReturnDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.PurchaseReturnMapper;
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
import java.util.stream.Collectors;

@Service
@Log
public class PurchaseReturnServiceImpl implements PurchaseReturnService {
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private TaxRepository taxRepo;
    @Autowired
    private DiscountRepository discountRepo;
    @Autowired
    private PurchaseReturnMasterRepository purchaseReturnRepo;
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
    private PurchaseReturnMapper purchaseReturnMapper;

    @Override
    public Page<PurchaseReturnDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return purchaseReturnRepo.findAllByTenantId(authService.authUser().getTenant().getId(), authService.authUser().getId(), pageRequest).map(purchaseReturn -> purchaseReturnMapper.toMasterDTO(purchaseReturn));
    }

    @Override
    public PurchaseReturnDTO getById(Long id) throws NotFoundException {
        Optional<PurchaseReturnMaster> purchase = purchaseReturnRepo.findById(id);
        if (purchase.isEmpty()) {
            throw new NotFoundException();
        }
        return purchaseReturnMapper.toMasterDTO(purchase.get());
    }

    @Override
    public List<PurchaseReturnDTO> getByContains(String search) {
        return purchaseReturnRepo.searchAll(authService.authUser().getTenant().getId(), authService.authUser().getId(), search.toLowerCase())
                .stream()
                .map(purchaseReturn -> purchaseReturnMapper.toMasterDTO(purchaseReturn))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(PurchaseReturnMaster purchase) throws NotFoundException {
        var supplier = supplierRepo.findById(purchase.getSupplier().getId())
                .orElseThrow(() -> new NotFoundException("Supplier not found with ID: " + purchase.getSupplier().getId()));
        var tax = taxRepo.findById(purchase.getTax().getId())
                .orElseThrow(() -> new NotFoundException("Tax not found with ID: " + purchase.getTax().getId()));
        var discount = discountRepo.findById(purchase.getDiscount().getId())
                .orElseThrow(() -> new NotFoundException("Discount not found with ID: " + purchase.getDiscount().getId()));

        ArrayList<PurchaseReturnDetail> list = (ArrayList<PurchaseReturnDetail>) purchase.getPurchaseReturnDetails().stream().peek(detail -> detail.setId(null)).collect(Collectors.toList());
        purchase.getPurchaseReturnDetails().clear();
        purchase.setPurchaseReturnDetails(list);

        // Perform calculations
        purchaseCalculationService.calculate(purchase);

        // Set additional details
        purchase.setTenant(authService.authUser().getTenant());
        if (purchase.getBranch() == null) {
            purchase.setBranch(authService.authUser().getBranch());
        }
        if (supplier != null) {
            purchase.setSupplier(supplier);
        }
        if (tax != null) {
            purchase.setTax(tax);
        }
        if (discount != null) {
            purchase.setDiscount(discount);
        }
        if (settingsService.getSettingsInternal().getReview() && settingsService.getSettingsInternal().getApprovePurchaseReturns()) {
            Reviewer reviewer = null;
            try {
                reviewer = approverService.getByUserId(authService.authUser().getId());
            } catch (NotFoundException e) {
                log.severe(e.getMessage());
            }
            if (Objects.nonNull(reviewer)) {
                purchase.getReviewers().add(reviewer);
                purchase.setNextApprovedLevel(reviewer.getLevel());
                if (reviewer.getLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                    purchase.setApproved(true);
                    purchase.setApprovalStatus("Approved");
                    purchase.setPurchaseStatus("Returned");
                    createAccountTransaction(purchase);
                    updateProductCost(purchase);
                }
            } else {
                purchase.setNextApprovedLevel(1);
                purchase.setApproved(false);
            }
            purchase.setApprovalStatus("Pending");
            purchase.setPurchaseStatus("Pending");
        } else {
            purchase.setApproved(true);
            purchase.setApprovalStatus("Approved");
            purchase.setPurchaseStatus("Returned");
            createAccountTransaction(purchase);
            updateProductCost(purchase);
        }
        purchase.setCreatedBy(authService.authUser());
        purchase.setCreatedAt(LocalDateTime.now());

        try {
            purchaseReturnRepo.save(purchase);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(PurchaseReturnMaster data) throws NotFoundException {
        var opt = purchaseReturnRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchase = opt.get();

        // Update fields as needed
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
        }

        // Perform calculations
        purchaseCalculationService.calculate(purchase);

        // Update other fields
        if (Objects.nonNull(data.getPurchaseStatus()) && !"".equalsIgnoreCase(data.getPurchaseStatus())) {
            purchase.setPurchaseStatus(data.getPurchaseStatus());
        }
        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            purchase.setPaymentStatus(data.getPaymentStatus());
        }
        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            purchase.setNotes(data.getNotes());
        }
        if (Objects.nonNull(data.getReviewers()) && !data.getReviewers().isEmpty()) {
            purchase.getReviewers().add(data.getReviewers().getFirst());
            if (purchase.getNextApprovedLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                purchase.setApproved(true);
                purchase.setApprovalStatus("Approved");
                createAccountTransaction(purchase);
                updateProductCost(purchase);
            }
        }

        purchase.setUpdatedBy(authService.authUser());
        purchase.setUpdatedAt(LocalDateTime.now());

        try {
            purchaseReturnRepo.save(purchase);

            // Check if product cost price needs to be updated.
            for (PurchaseReturnDetail detail : purchase.getPurchaseReturnDetails()) {
                var product = productService.getByIdInternally(detail.getProduct().getId());
                if (!Objects.equals(product.getCostPrice(), detail.getUnitCost())) {
                    product.setCostPrice(detail.getUnitCost());
                    productService.save(product);
                }
            }

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "purchase_masters", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException {
        var opt = purchaseReturnRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var purchase = opt.get();

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")) {
            purchase.setApproved(false);
            purchase.setNextApprovedLevel(purchase.getNextApprovedLevel() - 1);
            purchase.setApprovalStatus("Returned");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")) {
            var approver = approverService.getByUserId(authService.authUser().getId());
            purchase.getReviewers().add(approver);
            purchase.setNextApprovedLevel(approver.getLevel());
            if (purchase.getNextApprovedLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                purchase.setApproved(true);
                purchase.setApprovalStatus("Approved");
                purchase.setPurchaseStatus("Returned");
                createAccountTransaction(purchase);
                updateProductCost(purchase);
            }
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            purchase.setApproved(false);
            purchase.setApprovalStatus("Rejected");
            purchase.setNextApprovedLevel(0);
        }

        purchase.setUpdatedBy(authService.authUser());
        purchase.setUpdatedAt(LocalDateTime.now());
        try {
            purchaseReturnRepo.save(purchase);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            purchaseReturnRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            purchaseReturnRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public void createAccountTransaction(PurchaseReturnMaster purchase) {
        // Create account transaction of this purchase.
        var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
        var accountTransaction = new AccountTransaction();
        accountTransaction.setTenant(authService.authUser().getTenant());
        accountTransaction.setTransactionDate(LocalDateTime.now());
        accountTransaction.setAccount(account);
        accountTransaction.setAmount(purchase.getTotal());
        accountTransaction.setTransactionType("Purchase Returns");
        accountTransaction.setNote("Purchase return made");
        accountTransaction.setCreatedBy(authService.authUser());
        accountTransaction.setCreatedAt(LocalDateTime.now());
        accountTransactionService.save(accountTransaction);
    }

    public void updateProductCost(PurchaseReturnMaster purchase) throws NotFoundException {
        // Check if product cost price needs to be updated.
        for (PurchaseReturnDetail detail : purchase.getPurchaseReturnDetails()) {
            var product = productService.getByIdInternally(detail.getProduct().getId());
            if (!Objects.equals(product.getCostPrice(), detail.getUnitCost())) {
                product.setCostPrice(detail.getUnitCost());
                productService.save(product);
            }
        }
    }
}
