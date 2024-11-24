package io.nomard.spoty_api_v1.services.implementations.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.StockInDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.StockInMapper;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.ApproverServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.stock_ins.StockInService;
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
public class StockInServiceImpl implements StockInService {
    @Autowired
    private StockInMasterRepository stockInRepo;
    @Autowired
    private StockInTransactionServiceImpl stockInTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TenantSettingsServiceImpl settingsService;
    @Autowired
    private ApproverServiceImpl approverService;
    @Autowired
    private StockInMapper stockInMapper;

    @Override
    public Page<StockInDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return stockInRepo.findAllByTenantId(
                authService.authUser().getTenant().getId(),
                authService.authUser().getId(),
                pageRequest
        ).map(stockIn -> stockInMapper.toMasterDTO(stockIn));
    }

    @Override
    public StockInDTO getById(Long id) throws NotFoundException {
        Optional<StockInMaster> stockIn = stockInRepo.findById(id);
        if (stockIn.isEmpty()) {
            throw new NotFoundException();
        }
        return stockInMapper.toMasterDTO(stockIn.get());
    }

    @Override
    public List<StockInDTO> getByContains(String search) {
        return stockInRepo.searchAll(
                        authService.authUser().getTenant().getId(),
                        authService.authUser().getId(),
                        search.toLowerCase()
                ).stream()
                .map(stockIn -> stockInMapper.toMasterDTO(stockIn))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(StockInMaster stockIn) {
        CoreCalculations.StockInCalculationService.calculate(stockIn);
        stockIn.setTenant(authService.authUser().getTenant());
        stockIn.setRef(CoreUtils.referenceNumberGenerator("STK"));
        if (Objects.isNull(stockIn.getBranch())) {
            stockIn.setBranch(authService.authUser().getBranch());
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
                stockIn.getReviewers().add(reviewer);
                stockIn.setNextApprovedLevel(reviewer.getLevel());
                if (
                        reviewer.getLevel() >=
                                settingsService.getSettingsInternal().getApprovalLevels()
                ) {
                    stockIn.setApproved(true);
                    stockIn.setApprovalStatus("Approved");
                }
            } else {
                stockIn.setNextApprovedLevel(1);
                stockIn.setApproved(false);
            }
            stockIn.setApprovalStatus("Pending");
        } else {
            stockIn.setApproved(true);
            stockIn.setApprovalStatus("Approved");
        }
        stockIn.setCreatedBy(authService.authUser());
        stockIn.setCreatedAt(LocalDateTime.now());

        try {
            stockInRepo.save(stockIn);
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }

        try {
            productStockUpdate(stockIn);
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
    public ResponseEntity<ObjectNode> update(StockInMaster data)
            throws NotFoundException {
        var opt = stockInRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockIn = opt.get();
        if (
                Objects.nonNull(data.getRef()) &&
                        !"".equalsIgnoreCase(data.getRef())
        ) {
            stockIn.setRef(data.getRef());
        }
        if (
                !Objects.equals(stockIn.getBranch(), data.getBranch()) &&
                        Objects.nonNull(data.getBranch())
        ) {
            stockIn.setBranch(data.getBranch());
        }
        if (
                Objects.nonNull(data.getStockInDetails()) &&
                        !data.getStockInDetails().isEmpty()
        ) {
            stockIn.setStockInDetails(data.getStockInDetails());
            CoreCalculations.StockInCalculationService.calculate(stockIn);
        }
        if (
                Objects.nonNull(data.getNotes()) &&
                        !"".equalsIgnoreCase(data.getNotes())
        ) {
            stockIn.setNotes(data.getNotes());
        }
        if (
                Objects.nonNull(data.getReviewers()) &&
                        !data.getReviewers().isEmpty()
        ) {
            stockIn.getReviewers().add(data.getReviewers().getFirst());
            if (
                    stockIn.getNextApprovedLevel() >=
                            settingsService.getSettingsInternal().getApprovalLevels()
            ) {
                stockIn.setApproved(true);
                stockIn.setApprovalStatus("Approved");
            }
        }
        stockIn.setUpdatedBy(authService.authUser());
        stockIn.setUpdatedAt(LocalDateTime.now());

        try {
            stockInRepo.save(stockIn);
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
    @CacheEvict(value = "stock_ins", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel)
            throws NotFoundException {
        var opt = stockInRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockIn = opt.get();

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")
        ) {
            stockIn.setApproved(false);
            stockIn.setNextApprovedLevel(stockIn.getNextApprovedLevel() - 1);
            stockIn.setApprovalStatus("Returned");
        }

        if (
                Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")
        ) {
            var approver = approverService.getByUserId(
                    authService.authUser().getId()
            );
            stockIn.getReviewers().add(approver);
            stockIn.setNextApprovedLevel(approver.getLevel());
            if (
                    stockIn.getNextApprovedLevel() >=
                            settingsService.getSettingsInternal().getApprovalLevels()
            ) {
                stockIn.setApproved(true);
                stockIn.setApprovalStatus("Approved");
            }
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            stockIn.setApproved(false);
            stockIn.setApprovalStatus("Rejected");
            stockIn.setNextApprovedLevel(0);
        }

        stockIn.setUpdatedBy(authService.authUser());
        stockIn.setUpdatedAt(LocalDateTime.now());

        try {
            stockInRepo.save(stockIn);
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
        try {
            productStockUpdate(stockIn);
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
            stockInRepo.deleteById(id);
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
            stockInRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    private void productStockUpdate(StockInMaster stockIn) {
        if (!stockIn.getStockInDetails().isEmpty()) {
            for (int i = 0; i < stockIn.getStockInDetails().size(); i++) {
                stockInTransactionService.save(
                        stockIn.getStockInDetails().get(i)
                );
            }
        }
    }
}
