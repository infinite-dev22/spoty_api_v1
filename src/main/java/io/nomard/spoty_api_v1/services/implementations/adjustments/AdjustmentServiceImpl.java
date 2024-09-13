package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Approver;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.CoreUtils;
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

@Service
@Log
public class AdjustmentServiceImpl implements AdjustmentService {
    @Autowired
    private AdjustmentMasterRepository adjustmentMasterRepo;
    @Autowired
    private AdjustmentTransactionServiceImpl adjustmentTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public Page<AdjustmentMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return adjustmentMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), authService.authUser().getId(), pageRequest);
    }

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public AdjustmentMaster getById(Long id) throws NotFoundException {
        Optional<AdjustmentMaster> adjustmentMaster = adjustmentMasterRepo.findById(id);
        if (adjustmentMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return adjustmentMaster.get();
    }

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public List<AdjustmentMaster> getByContains(String search) {
        return adjustmentMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(AdjustmentMaster adjustmentMaster) {
        CoreCalculations.AdjustmentCalculationService.calculate(adjustmentMaster);
        adjustmentMaster.setRef(CoreUtils.referenceNumberGenerator("ADJ"));
        adjustmentMaster.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(adjustmentMaster.getBranch())) {
            adjustmentMaster.setBranch(authService.authUser().getBranch());
        }
        if (settingsService.getSettings().getApproveAdjustments()) {
            Approver approver = null;
            try {
                approver = approverService.getByUserId(authService.authUser().getId());
            } catch (NotFoundException e) {
                log.log(Level.ALL, e.getMessage(), e);
            }
            if (Objects.nonNull(approver)) {
                adjustmentMaster.getApprovers().add(approver);
                adjustmentMaster.setLatestApprovedLevel(approver.getLevel());
                if (approver.getLevel() >= settingsService.getSettings().getApprovalLevels()) {
                    adjustmentMaster.setApproved(true);
                }
            } else {
                adjustmentMaster.setApproved(false);
            }
            adjustmentMaster.setApprovalStatus("Pending");
        } else {
            adjustmentMaster.setApproved(true);
        }
        adjustmentMaster.setCreatedBy(authService.authUser());
        adjustmentMaster.setCreatedAt(LocalDateTime.now());

        try {
            adjustmentMasterRepo.save(adjustmentMaster);
            for (int i = 0; i < adjustmentMaster.getAdjustmentDetails().size(); i++) {
                adjustmentTransactionService.save(adjustmentMaster.getAdjustmentDetails().get(i));
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "adjustment_masters", key = "#data.id")
    @Transactional
    public ResponseEntity<ObjectNode> update(AdjustmentMaster data) throws NotFoundException {
        var opt = adjustmentMasterRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var adjustmentMaster = opt.get();
        if (Objects.nonNull(data.getBranch())) {
            adjustmentMaster.setBranch(data.getBranch());
        }
        if (Objects.nonNull(data.getAdjustmentDetails()) && !data.getAdjustmentDetails().isEmpty()) {
            adjustmentMaster.setAdjustmentDetails(data.getAdjustmentDetails());
            CoreCalculations.AdjustmentCalculationService.calculate(adjustmentMaster);
        }
        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            adjustmentMaster.setRef(data.getRef());
        }
        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            adjustmentMaster.setNotes(data.getNotes());
        }
        if (Objects.nonNull(data.getApprovers()) && !data.getApprovers().isEmpty()) {
            adjustmentMaster.getApprovers().add(data.getApprovers().getFirst());
            if (adjustmentMaster.getLatestApprovedLevel() >= settingsService.getSettings().getApprovalLevels()) {
                adjustmentMaster.setApproved(true);
            }
        }
        adjustmentMaster.setUpdatedBy(authService.authUser());
        adjustmentMaster.setUpdatedAt(LocalDateTime.now());
        try {
            adjustmentMasterRepo.save(adjustmentMaster);
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
        var opt = adjustmentMasterRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var adjustmentMaster = opt.get();

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")) {
            adjustmentMaster.setApproved(false);
            adjustmentMaster.setApprovalStatus("Returned");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")) {
            var approver = approverService.getByUserId(authService.authUser().getId());
            adjustmentMaster.getApprovers().add(approver);
            adjustmentMaster.setLatestApprovedLevel(approver.getLevel());
            if (adjustmentMaster.getLatestApprovedLevel() >= settingsService.getSettings().getApprovalLevels()) {
                adjustmentMaster.setApproved(true);
            }
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            adjustmentMaster.setApproved(false);
            adjustmentMaster.setApprovalStatus("Rejected");
            adjustmentMaster.setLatestApprovedLevel(0);
        }

        adjustmentMaster.setUpdatedBy(authService.authUser());
        adjustmentMaster.setUpdatedAt(LocalDateTime.now());
        try {
            adjustmentMasterRepo.save(adjustmentMaster);
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
            adjustmentMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            adjustmentMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
