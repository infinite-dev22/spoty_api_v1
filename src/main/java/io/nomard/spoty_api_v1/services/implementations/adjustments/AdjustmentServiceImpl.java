package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.CoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdjustmentServiceImpl implements AdjustmentService {
    @Autowired
    private AdjustmentMasterRepository adjustmentMasterRepo;
    @Autowired
    private AdjustmentTransactionServiceImpl adjustmentTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public Page<AdjustmentMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return adjustmentMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
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
//    @Transactional
    public ResponseEntity<ObjectNode> save(AdjustmentMaster adjustmentMaster) {
        try {
            CoreCalculations.AdjustmentCalculationService.calculate(adjustmentMaster);
            adjustmentMaster.setRef(CoreUtils.referenceNumberGenerator("ADJ"));
            adjustmentMaster.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(adjustmentMaster.getBranch())) {
                adjustmentMaster.setBranch(authService.authUser().getBranch());
            }
            adjustmentMaster.setCreatedBy(authService.authUser());
            adjustmentMaster.setCreatedAt(LocalDateTime.now());
            adjustmentMasterRepo.save(adjustmentMaster);
            for (int i = 0; i < adjustmentMaster.getAdjustmentDetails().size(); i++) {
                adjustmentTransactionService.save(adjustmentMaster.getAdjustmentDetails().get(i));
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "adjustment_masters", key = "#data.id")
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
        adjustmentMaster.setUpdatedBy(authService.authUser());
        adjustmentMaster.setUpdatedAt(LocalDateTime.now());
        try {
            adjustmentMasterRepo.save(adjustmentMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            adjustmentMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            adjustmentMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
