package io.nomard.spoty_api_v1.services.implementations.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.SaleReturnDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.SaleReturnMapper;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.returns.sale_returns.SaleReturnMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns.SaleReturnService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Log
public class SaleReturnServiceImpl implements SaleReturnService {
    @Autowired
    private SaleReturnMasterRepository saleReturnRepo;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;
    @Autowired
    private CoreCalculations.SaleCalculationService saleCalculationService;
    @Autowired
    private SaleReturnMapper saleReturnMapper;

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public Page<SaleReturnDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return saleReturnRepo.findAllByTenantId(authService.authUser().getTenant().getId(), authService.authUser().getId(), pageRequest).map(saleReturn -> saleReturnMapper.toMasterDTO(saleReturn));
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public SaleReturnDTO getById(Long id) throws NotFoundException {
        Optional<SaleReturnMaster> saleReturn = saleReturnRepo.findById(id);
        if (saleReturn.isEmpty()) {
            throw new NotFoundException();
        }
        return saleReturnMapper.toMasterDTO(saleReturn.get());
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public List<SaleReturnDTO> getByContains(String search) {
        return saleReturnRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase())
                .stream()
                .map(saleReturn -> saleReturnMapper.toMasterDTO(saleReturn))
                .collect(Collectors.toList());
    }

    @Override
//    @Transactional
    public ResponseEntity<ObjectNode> save(SaleReturnMaster sale) throws NotFoundException {
        // Perform calculations
        saleCalculationService.calculate(sale);

        // Set additional details
        sale.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(sale.getBranch())) {
            sale.setBranch(authService.authUser().getBranch());
        }
        if (settingsService.getSettingsInternal().getReview() && settingsService.getSettingsInternal().getApproveSaleReturns()) {
            Reviewer reviewer = null;
            try {
                reviewer = approverService.getByUserId(authService.authUser().getId());
            } catch (NotFoundException e) {
                log.log(Level.ALL, e.getMessage(), e);
            }
            if (Objects.nonNull(reviewer)) {
                sale.getReviewers().add(reviewer);
                sale.setNextApprovedLevel(reviewer.getLevel());
                if (reviewer.getLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                    sale.setApproved(true);
                    sale.setApprovalStatus("Approved");
                }
            } else {
                sale.setNextApprovedLevel(1);
                sale.setApproved(false);
            }
            sale.setApprovalStatus("Pending");
        } else {
            sale.setApproved(true);
            sale.setApprovalStatus("Approved");
        }
        sale.setCreatedBy(authService.authUser());
        sale.setCreatedAt(LocalDateTime.now());

        try {
            saleReturnRepo.save(sale);
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }

        try {
            if (sale.getApprovalStatus().toLowerCase().contains("approved")) {
                createAccountTransaction(sale);
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "sale_masters", key = "#data.id")
    public ResponseEntity<ObjectNode> update(SaleReturnMaster data) throws NotFoundException {
        var opt = saleReturnRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var sale = opt.get();

        // Update fields as needed
        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            sale.setRef(data.getRef());
        }
        if (!Objects.equals(sale.getCustomer(), data.getCustomer()) && Objects.nonNull(data.getCustomer())) {
            sale.setCustomer(data.getCustomer());
        }
        if (!Objects.equals(sale.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
            sale.setBranch(data.getBranch());
        }
        if (Objects.nonNull(data.getSaleReturnDetails()) && !data.getSaleReturnDetails().isEmpty()) {
            sale.setSaleReturnDetails(data.getSaleReturnDetails());
        }

        // Perform calculations
        saleCalculationService.calculate(sale);

        // Update other fields
        if (!Objects.equals(data.getTax(), sale.getTax()) && Objects.nonNull(data.getTax())) {
            sale.setTax(data.getTax());
        }
        if (!Objects.equals(data.getDiscount(), sale.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            sale.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getAmountPaid(), sale.getAmountPaid())) {
            sale.setAmountPaid(data.getAmountPaid());
        }
        if (!Objects.equals(data.getAmountDue(), sale.getAmountDue())) {
            sale.setAmountDue(data.getAmountDue());
        }
        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            sale.setPaymentStatus(data.getPaymentStatus());
        }
        if (Objects.nonNull(data.getSaleStatus()) && !"".equalsIgnoreCase(data.getSaleStatus())) {
            sale.setSaleStatus(data.getSaleStatus());
        }
        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            sale.setNotes(data.getNotes());
        }
        sale.setUpdatedBy(authService.authUser());
        sale.setUpdatedAt(LocalDateTime.now());
        try {
            saleReturnRepo.save(sale);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "sale_masters", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException {
        var opt = saleReturnRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var sale = opt.get();

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")) {
            sale.setApproved(false);
            sale.setNextApprovedLevel(sale.getNextApprovedLevel() - 1);
            sale.setApprovalStatus("Returned");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")) {
            var approver = approverService.getByUserId(authService.authUser().getId());
            sale.getReviewers().add(approver);
            sale.setNextApprovedLevel(approver.getLevel());
            if (sale.getNextApprovedLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                sale.setApproved(true);
                sale.setApprovalStatus("Approved");
            }
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            sale.setApproved(false);
            sale.setApprovalStatus("Rejected");
            sale.setNextApprovedLevel(0);
        }

        sale.setUpdatedBy(authService.authUser());
        sale.setUpdatedAt(LocalDateTime.now());
        try {
            saleReturnRepo.save(sale);
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }

        try {
            if (sale.getApprovalStatus().toLowerCase().contains("approved")) {
                createAccountTransaction(sale);
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleReturnRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            saleReturnRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public void createAccountTransaction(SaleReturnMaster sale) {
        // Create account transaction of this sale.
        var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
        var accountTransaction = new AccountTransaction();
        accountTransaction.setTenant(authService.authUser().getTenant());
        accountTransaction.setTransactionDate(LocalDateTime.now());
        accountTransaction.setAccount(account);
        accountTransaction.setAmount(sale.getTotal());
        accountTransaction.setTransactionType("Sale Returns");
        accountTransaction.setNote("Sale return made");
        accountTransaction.setCreatedBy(authService.authUser());
        accountTransaction.setCreatedAt(LocalDateTime.now());
        accountTransactionService.save(accountTransaction);
    }
}
