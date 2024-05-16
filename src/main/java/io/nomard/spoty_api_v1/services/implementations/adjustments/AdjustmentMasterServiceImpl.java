package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdjustmentMasterServiceImpl implements AdjustmentMasterService {
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
    public List<AdjustmentMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<AdjustmentMaster> page = adjustmentMasterRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
        return adjustmentMasterRepo.searchAllByRefContainingIgnoreCase(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(AdjustmentMaster adjustmentMaster) {
        try {
            for (int i = 0; i < adjustmentMaster.getAdjustmentDetails().size(); i++) {
                adjustmentMaster.getAdjustmentDetails().get(i).setAdjustment(adjustmentMaster);
                adjustmentTransactionService.save(adjustmentMaster.getAdjustmentDetails().get(i));
            }

            adjustmentMaster.setCreatedBy(authService.authUser());
            adjustmentMaster.setCreatedAt(new Date());
            adjustmentMasterRepo.saveAndFlush(adjustmentMaster);
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

        if (Objects.nonNull(data.getDate())) {
            adjustmentMaster.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getBranch())) {
            adjustmentMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getAdjustmentDetails()) && !data.getAdjustmentDetails().isEmpty()) {
            adjustmentMaster.setAdjustmentDetails(data.getAdjustmentDetails());
        }

        if (!adjustmentMaster.getAdjustmentDetails().isEmpty()) {
            for (int i = 0; i < adjustmentMaster.getAdjustmentDetails().size(); i++) {
                adjustmentMaster.getAdjustmentDetails().get(i).setAdjustment(adjustmentMaster);
                try {
                    adjustmentTransactionService.update(adjustmentMaster.getAdjustmentDetails().get(i));
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            adjustmentMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            adjustmentMaster.setNotes(data.getNotes());
        }

        adjustmentMaster.setUpdatedBy(authService.authUser());
        adjustmentMaster.setUpdatedAt(new Date());

        try {
            adjustmentMasterRepo.saveAndFlush(adjustmentMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
