package io.nomard.spoty_api_v1.services.implementations.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.RequisitionDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.RequisitionMapper;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.requisitions.RequisitionMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.requisitions.RequisitionService;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Log
public class RequisitionServiceImpl implements RequisitionService {
    @Autowired
    private RequisitionMasterRepository requisitionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;
    @Autowired
    private RequisitionMapper requisitionMapper;

    @Override
    public Page<RequisitionDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return requisitionRepo.findAllByTenantId(
                authService.authUser().getTenant().getId(),
                authService.authUser().getId(),
                pageRequest
        ).map(requisition -> requisitionMapper.toMasterDTO(requisition));
    }

    @Override
    public RequisitionDTO getById(Long id) throws NotFoundException {
        Optional<RequisitionMaster> requisition = requisitionRepo.findById(id);
        if (requisition.isEmpty()) {
            throw new NotFoundException();
        }
        return requisitionMapper.toMasterDTO(requisition.get());
    }

    @Override
    public List<RequisitionDTO> getByContains(String search) {
        return requisitionRepo.searchAll(
                        authService.authUser().getTenant().getId(),
                        authService.authUser().getId(),
                        search.toLowerCase()
                )
                .stream()
                .map(requisition -> requisitionMapper.toMasterDTO(requisition))
                .collect(Collectors.toList());
    }

    @Override
    //    @Transactional
    public ResponseEntity<ObjectNode> save(RequisitionMaster requisition) {
        try {
            CoreCalculations.RequisitionCalculationService.calculate(
                    requisition
            );
            requisition.setRef(CoreUtils.referenceNumberGenerator("REQ"));
            requisition.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(requisition.getBranch())) {
                requisition.setBranch(authService.authUser().getBranch());
            }
            if (settingsService.getSettingsInternal().getReview() && settingsService.getSettingsInternal().getApproveAdjustments()) {
                Reviewer reviewer = null;
                try {
                    reviewer = approverService.getByUserId(
                            authService.authUser().getId()
                    );
                } catch (NotFoundException e) {
                     log.severe(e.getMessage());
                }
                if (Objects.nonNull(reviewer)) {
                    requisition.getReviewers().add(reviewer);
                    requisition.setNextApprovedLevel(reviewer.getLevel());
                    if (
                            reviewer.getLevel() >=
                                    settingsService.getSettingsInternal().getApprovalLevels()
                    ) {
                        requisition.setApproved(true);
                        requisition.setApprovalStatus("Approved");
                    }
                } else {
                    requisition.setNextApprovedLevel(1);
                    requisition.setApproved(false);
                }
                requisition.setApprovalStatus("Pending");
            } else {
                requisition.setApproved(true);
                requisition.setApprovalStatus("Approved");
            }
            requisition.setStatus("Pending");
            requisition.setCreatedBy(authService.authUser());
            requisition.setCreatedAt(LocalDateTime.now());
            requisitionRepo.save(requisition);
            return spotyResponseImpl.created();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(RequisitionMaster data)
            throws NotFoundException {
        var opt = requisitionRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var requisition = opt.get();

        if (
                Objects.nonNull(data.getRef()) &&
                        !"".equalsIgnoreCase(data.getRef())
        ) {
            requisition.setRef(data.getRef());
        }

        if (
                Objects.nonNull(data.getSupplier()) &&
                        !Objects.equals(data.getSupplier(), requisition.getSupplier())
        ) {
            requisition.setSupplier(data.getSupplier());
        }

        if (
                Objects.nonNull(data.getBranch()) &&
                        !Objects.equals(data.getBranch(), requisition.getBranch())
        ) {
            requisition.setBranch(data.getBranch());
        }

        if (
                Objects.nonNull(data.getRequisitionDetails()) &&
                        !data.getRequisitionDetails().isEmpty()
        ) {
            requisition.setRequisitionDetails(data.getRequisitionDetails());
            CoreCalculations.RequisitionCalculationService.calculate(
                    requisition
            );
        }

        if (
                Objects.nonNull(data.getNotes()) &&
                        !"".equalsIgnoreCase(data.getNotes())
        ) {
            requisition.setNotes(data.getNotes());
        }

        if (
                Objects.nonNull(data.getStatus()) &&
                        !"".equalsIgnoreCase(data.getStatus())
        ) {
            requisition.setStatus(data.getStatus());
        }
        if (
                Objects.nonNull(data.getReviewers()) &&
                        !data.getReviewers().isEmpty()
        ) {
            requisition.getReviewers().add(data.getReviewers().getFirst());
            if (
                    requisition.getNextApprovedLevel() >=
                            settingsService.getSettingsInternal().getApprovalLevels()
            ) {
                requisition.setApproved(true);
                requisition.setApprovalStatus("Approved");
            }
        }

        requisition.setUpdatedBy(authService.authUser());
        requisition.setUpdatedAt(LocalDateTime.now());

        try {
            requisitionRepo.save(requisition);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    @CacheEvict(value = "requisitions", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel)
            throws NotFoundException {
        var opt = requisitionRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var requisition = opt.get();

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")
        ) {
            requisition.setApproved(false);
            requisition.setNextApprovedLevel(
                    requisition.getNextApprovedLevel() - 1
            );
            requisition.setApprovalStatus("Returned");
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")
        ) {
            var approver = approverService.getByUserId(
                    authService.authUser().getId()
            );
            requisition.getReviewers().add(approver);
            requisition.setNextApprovedLevel(approver.getLevel());
            if (
                    requisition.getNextApprovedLevel() >=
                            settingsService.getSettingsInternal().getApprovalLevels()
            ) {
                requisition.setApproved(true);
                requisition.setApprovalStatus("Approved");
            }
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")
        ) {
            requisition.setApproved(false);
            requisition.setApprovalStatus("Rejected");
            requisition.setNextApprovedLevel(0);
        }

        requisition.setUpdatedBy(authService.authUser());
        requisition.setUpdatedAt(LocalDateTime.now());
        try {
            requisitionRepo.save(requisition);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
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
            requisitionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            requisitionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }
}
