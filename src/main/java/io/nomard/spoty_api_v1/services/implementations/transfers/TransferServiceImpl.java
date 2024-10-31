package io.nomard.spoty_api_v1.services.implementations.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.TransferDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.TransferMapper;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.transfers.TransferMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.transfers.TransferService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Log
public class TransferServiceImpl implements TransferService {
    @Autowired
    private TransferMasterRepository transferRepo;
    @Autowired
    private TransferTransactionServiceImpl transferTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;
    @Autowired
    private TransferMapper transferMapper;

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public Page<TransferDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return transferRepo.findAllByTenantId(
                authService.authUser().getTenant().getId(),
                authService.authUser().getId(),
                pageRequest
        ).map(transfer -> transferMapper.toMasterDTO(transfer));
    }

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public TransferDTO getById(Long id) throws NotFoundException {
        Optional<TransferMaster> transfer = transferRepo.findById(id);
        if (transfer.isEmpty()) {
            throw new NotFoundException();
        }
        return transferMapper.toMasterDTO(transfer.get());
    }

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public List<TransferDTO> getByContains(String search) {
        return transferRepo.searchAll(
                authService.authUser().getTenant().getId(),
                search.toLowerCase()
        ).stream()
                .map(transfer -> transferMapper.toMasterDTO(transfer))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(TransferMaster transfer) {
        CoreCalculations.TransferCalculationService.calculate(transfer);
        transfer.setRef(CoreUtils.referenceNumberGenerator("TRF"));
        transfer.setTenant(authService.authUser().getTenant());
        if (settingsService.getSettingsInternal().getReview() && settingsService.getSettingsInternal().getApproveTransfers()) {
            Reviewer reviewer = null;
            try {
                reviewer = approverService.getByUserId(
                        authService.authUser().getId()
                );
            } catch (NotFoundException e) {
                log.log(Level.ALL, e.getMessage(), e);
            }
            if (Objects.nonNull(reviewer)) {
                transfer.getReviewers().add(reviewer);
                transfer.setNextApprovedLevel(reviewer.getLevel());
                if (
                        reviewer.getLevel() >=
                                settingsService.getSettingsInternal().getApprovalLevels()
                ) {
                    transfer.setApproved(true);
                    transfer.setApprovalStatus("Approved");
                }
            } else {
                transfer.setNextApprovedLevel(1);
                transfer.setApproved(false);
            }
            transfer.setApprovalStatus("Pending");
        } else {
            transfer.setApproved(true);
            transfer.setApprovalStatus("Approved");
        }
        transfer.setCreatedBy(authService.authUser());
        transfer.setCreatedAt(LocalDateTime.now());
        try {
            transferRepo.save(transfer);
            for (int i = 0; i < transfer.getTransferDetails().size(); i++) {
                transferTransactionService.save(
                        transfer.getTransferDetails().get(i)
                );
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "transfer_masters", key = "#data.id")
    public ResponseEntity<ObjectNode> update(TransferMaster data)
            throws NotFoundException {
        var opt = transferRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var transfer = opt.get();
        if (
                Objects.nonNull(data.getRef()) &&
                        !"".equalsIgnoreCase(data.getRef())
        ) {
            transfer.setRef(data.getRef());
        }
        if (
                !Objects.equals(transfer.getDate(), data.getDate()) &&
                        Objects.nonNull(data.getDate())
        ) {
            transfer.setDate(data.getDate());
        }
        if (
                !Objects.equals(transfer.getFromBranch(), data.getFromBranch()) &&
                        Objects.nonNull(data.getFromBranch())
        ) {
            transfer.setFromBranch(data.getFromBranch());
        }
        if (
                !Objects.equals(transfer.getToBranch(), data.getToBranch()) &&
                        Objects.nonNull(data.getToBranch())
        ) {
            transfer.setToBranch(data.getToBranch());
        }
        if (
                Objects.nonNull(data.getTransferDetails()) &&
                        !data.getTransferDetails().isEmpty()
        ) {
            transfer.setTransferDetails(data.getTransferDetails());
            CoreCalculations.TransferCalculationService.calculate(transfer);
        }
        if (
                Objects.nonNull(data.getNotes()) &&
                        !"".equalsIgnoreCase(data.getNotes())
        ) {
            transfer.setNotes(data.getNotes());
        }
        if (
                Objects.nonNull(data.getNotes()) &&
                        !"".equalsIgnoreCase(data.getNotes())
        ) {
            transfer.setNotes(data.getNotes());
        }
        if (
                Objects.nonNull(data.getReviewers()) &&
                        !data.getReviewers().isEmpty()
        ) {
            transfer.getReviewers().add(data.getReviewers().getFirst());
            if (
                    transfer.getNextApprovedLevel() >=
                            settingsService.getSettingsInternal().getApprovalLevels()
            ) {
                transfer.setApproved(true);
                transfer.setApprovalStatus("Approved");
            }
        }
        transfer.setUpdatedBy(authService.authUser());
        transfer.setUpdatedAt(LocalDateTime.now());
        try {
            transferRepo.save(transfer);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "transfers", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel)
            throws NotFoundException {
        var opt = transferRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var transfer = opt.get();

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")
        ) {
            transfer.setApproved(false);
            transfer.setNextApprovedLevel(
                    transfer.getNextApprovedLevel() - 1
            );
            transfer.setApprovalStatus("Returned");
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")
        ) {
            var approver = approverService.getByUserId(
                    authService.authUser().getId()
            );
            transfer.getReviewers().add(approver);
            transfer.setNextApprovedLevel(approver.getLevel());
            if (
                    transfer.getNextApprovedLevel() >=
                            settingsService.getSettingsInternal().getApprovalLevels()
            ) {
                transfer.setApproved(true);
                transfer.setApprovalStatus("Approved");
            }
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")
        ) {
            transfer.setApproved(false);
            transfer.setApprovalStatus("Rejected");
            transfer.setNextApprovedLevel(0);
        }

        transfer.setUpdatedBy(authService.authUser());
        transfer.setUpdatedAt(LocalDateTime.now());
        try {
            transferRepo.save(transfer);
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
            transferRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            transferRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
