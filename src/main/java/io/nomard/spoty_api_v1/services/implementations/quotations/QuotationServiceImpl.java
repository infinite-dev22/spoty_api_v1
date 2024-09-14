package io.nomard.spoty_api_v1.services.implementations.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Approver;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.quotations.QuotationMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.DiscountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.TaxServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.quotations.QuotationService;
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
public class QuotationServiceImpl implements QuotationService {
    @Autowired
    private QuotationMasterRepository quotationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TaxServiceImpl taxService;
    @Autowired
    private DiscountServiceImpl discountService;
    @Autowired
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;

    @Override
    public Page<QuotationMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return quotationRepo.findAllByTenantId(authService.authUser().getTenant().getId(), authService.authUser().getId(), pageRequest);
    }

    @Override
    public QuotationMaster getById(Long id) throws NotFoundException {
        Optional<QuotationMaster> quotation = quotationRepo.findById(id);
        if (quotation.isEmpty()) {
            throw new NotFoundException();
        }
        return quotation.get();
    }

    @Override
    public ArrayList<QuotationMaster> getByContains(String search) {
        return quotationRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
//    @Transactional
    public ResponseEntity<ObjectNode> save(QuotationMaster quotation) throws NotFoundException {
        // Perform calculations
        var calculationService = new CoreCalculations.QuotationCalculationService(taxService, discountService);
        calculationService.calculate(quotation);

        // Set additional details
        quotation.setRef(CoreUtils.referenceNumberGenerator("QUO"));
        quotation.setTenant(authService.authUser().getTenant());
        quotation.setCreatedBy(authService.authUser());
        quotation.setCreatedAt(LocalDateTime.now());
        if (Objects.isNull(quotation.getBranch())) {
            quotation.setBranch(authService.authUser().getBranch());
        }
        if (settingsService.getSettings().getApproveAdjustments()) {
            Approver approver = null;
            try {
                approver = approverService.getByUserId(authService.authUser().getId());
            } catch (NotFoundException e) {
                log.log(Level.ALL, e.getMessage(), e);
            }
            if (Objects.nonNull(approver)) {
                quotation.getApprovers().add(approver);
                quotation.setLatestApprovedLevel(approver.getLevel());
                if (approver.getLevel() >= settingsService.getSettings().getApprovalLevels()) {
                    quotation.setApproved(true);
                    quotation.setApprovalStatus("Approved");
                }
            } else {
                quotation.setApproved(false);
            }
            quotation.setApprovalStatus("Pending");
        } else {
            quotation.setApproved(true);
            quotation.setApprovalStatus("Approved");
        }
        try {
            quotationRepo.save(quotation);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(QuotationMaster data) throws NotFoundException {
        var opt = quotationRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var quotation = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            quotation.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getCustomer()) && !Objects.equals(data.getCustomer(), quotation.getCustomer())) {
            quotation.setCustomer(data.getCustomer());
        }

        if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), quotation.getBranch())) {
            quotation.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getQuotationDetails()) && !data.getQuotationDetails().isEmpty()) {
            quotation.setQuotationDetails(data.getQuotationDetails());
        }

        // Perform calculations
        var calculationService = new CoreCalculations.QuotationCalculationService(taxService, discountService);
        calculationService.calculate(quotation);

        // Update other fields
        if (!Objects.equals(data.getTax(), quotation.getTax())) {
            quotation.setTax(data.getTax());
        }

        if (!Objects.equals(data.getDiscount(), quotation.getDiscount())) {
            quotation.setDiscount(data.getDiscount());
        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            quotation.setStatus(data.getStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            quotation.setNotes(data.getNotes());
        }

        if (Objects.nonNull(data.getApprovers()) && !data.getApprovers().isEmpty()) {
            quotation.getApprovers().add(data.getApprovers().getFirst());
            if (quotation.getLatestApprovedLevel() >= settingsService.getSettings().getApprovalLevels()) {
                quotation.setApproved(true);
                quotation.setApprovalStatus("Approved");
            }
        }

        quotation.setUpdatedBy(authService.authUser());
        quotation.setUpdatedAt(LocalDateTime.now());

        try {
            quotationRepo.save(quotation);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "adjustment_masters", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException {
        var opt = quotationRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var quotation = opt.get();

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")) {
            quotation.setApproved(false);
            quotation.setApprovalStatus("Returned");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")) {
            var approver = approverService.getByUserId(authService.authUser().getId());
            quotation.getApprovers().add(approver);
            quotation.setLatestApprovedLevel(approver.getLevel());
            if (quotation.getLatestApprovedLevel() >= settingsService.getSettings().getApprovalLevels()) {
                quotation.setApproved(true);
                quotation.setApprovalStatus("Approved");
            }
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            quotation.setApproved(false);
            quotation.setApprovalStatus("Rejected");
            quotation.setLatestApprovedLevel(0);
        }

        quotation.setUpdatedBy(authService.authUser());
        quotation.setUpdatedAt(LocalDateTime.now());
        try {
            quotationRepo.save(quotation);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            quotationRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            quotationRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
