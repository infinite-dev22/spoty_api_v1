package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.AdjustmentDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.AdjustmentMapper;
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
import java.util.stream.Collectors;

@Service
@Log
public class AdjustmentServiceImpl implements AdjustmentService {
    @Autowired
    private AdjustmentMasterRepository adjustmentRepo;
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
    @Autowired
    private AdjustmentMapper adjustmentMapper;

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public Page<AdjustmentDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return adjustmentRepo.findAllByTenantId(authService.authUser().getTenant().getId(), authService.authUser().getId(), pageRequest).map(adjustment -> adjustmentMapper.toMasterDTO(adjustment));
    }

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public AdjustmentDTO getById(Long id) throws NotFoundException {
        Optional<AdjustmentMaster> adjustment = adjustmentRepo.findById(id);
        if (adjustment.isEmpty()) {
            throw new NotFoundException();
        }
        return adjustmentMapper.toMasterDTO(adjustment.get());
    }

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public List<AdjustmentDTO> getByContains(String search) {
        return adjustmentRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase())
                .stream()
                .map(adjustment -> adjustmentMapper.toMasterDTO(adjustment))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(AdjustmentMaster adjustment) {
        CoreCalculations.AdjustmentCalculationService.calculate(adjustment);
        adjustment.setRef(CoreUtils.referenceNumberGenerator("ADJ"));
        adjustment.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(adjustment.getBranch())) {
            adjustment.setBranch(authService.authUser().getBranch());
        }
        if (settingsService.getSettingsInternal().getReview() && settingsService.getSettingsInternal().getApproveAdjustments()) {
            Reviewer reviewer = null;
            try {
                reviewer = approverService.getByUserId(authService.authUser().getId());
            } catch (NotFoundException e) {
                log.log(Level.ALL, e.getMessage(), e);
            }
            if (Objects.nonNull(reviewer)) {
                adjustment.getReviewers().add(reviewer);
                adjustment.setNextApprovedLevel(reviewer.getLevel() + 1);
                if (reviewer.getLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                    adjustment.setApproved(true);
                    adjustment.setApprovalStatus("Approved");
                }
            } else {
                adjustment.setNextApprovedLevel(1);
                adjustment.setApproved(false);
            }
            adjustment.setApprovalStatus("Pending");
        } else {
            adjustment.setApproved(true);
            adjustment.setApprovalStatus("Approved");
        }
        adjustment.setCreatedBy(authService.authUser());
        adjustment.setCreatedAt(LocalDateTime.now());

        try {
            adjustmentRepo.save(adjustment);
            for (int i = 0; i < adjustment.getAdjustmentDetails().size(); i++) {
                adjustmentTransactionService.save(adjustment.getAdjustmentDetails().get(i));
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
        var opt = adjustmentRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var adjustment = opt.get();
        if (Objects.nonNull(data.getBranch())) {
            adjustment.setBranch(data.getBranch());
        }
        if (Objects.nonNull(data.getAdjustmentDetails()) && !data.getAdjustmentDetails().isEmpty()) {
            adjustment.setAdjustmentDetails(data.getAdjustmentDetails());
            CoreCalculations.AdjustmentCalculationService.calculate(adjustment);
        }
        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            adjustment.setRef(data.getRef());
        }
        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            adjustment.setNotes(data.getNotes());
        }
        if (Objects.nonNull(data.getReviewers()) && !data.getReviewers().isEmpty()) {
            adjustment.getReviewers().add(data.getReviewers().getFirst());
            if (adjustment.getNextApprovedLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                adjustment.setApproved(true);
                adjustment.setApprovalStatus("Approved");
            }
        }
        adjustment.setUpdatedBy(authService.authUser());
        adjustment.setUpdatedAt(LocalDateTime.now());
        try {
            adjustmentRepo.save(adjustment);
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
        var opt = adjustmentRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var adjustment = opt.get();

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")) {
            adjustment.setApproved(false);
            adjustment.setNextApprovedLevel(adjustment.getNextApprovedLevel() - 1);
            adjustment.setApprovalStatus("Returned");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")) {
            var approver = approverService.getByUserId(authService.authUser().getId());
            adjustment.getReviewers().add(approver);
            adjustment.setNextApprovedLevel(approver.getLevel());
            if (adjustment.getNextApprovedLevel() >= settingsService.getSettingsInternal().getApprovalLevels()) {
                adjustment.setApproved(true);
                adjustment.setApprovalStatus("Approved");
            }
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            adjustment.setApproved(false);
            adjustment.setApprovalStatus("Rejected");
            adjustment.setNextApprovedLevel(0);
        }

        adjustment.setUpdatedBy(authService.authUser());
        adjustment.setUpdatedAt(LocalDateTime.now());
        try {
            adjustmentRepo.save(adjustment);
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
            adjustmentRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            adjustmentRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
