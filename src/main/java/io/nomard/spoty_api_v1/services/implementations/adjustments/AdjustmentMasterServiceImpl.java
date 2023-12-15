package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdjustmentMasterServiceImpl implements AdjustmentMasterService {
    @Autowired
    private AdjustmentMasterRepository adjustmentMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<AdjustmentMaster> getAll() {
        return adjustmentMasterRepo.findAll();
    }

    @Override
    public AdjustmentMaster getById(Long id) throws NotFoundException {
        Optional<AdjustmentMaster> adjustmentMaster = adjustmentMasterRepo.findById(id);
        if (adjustmentMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return adjustmentMaster.get();
    }

    @Override
    public List<AdjustmentMaster> getByContains(String search) {
        return adjustmentMasterRepo.searchAllByRefContainingIgnoreCase(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(AdjustmentMaster adjustmentMaster) {
        try {
            adjustmentMaster.setCreatedBy(authService.authUser());
            adjustmentMaster.setCreatedAt(new Date());
            adjustmentMasterRepo.saveAndFlush(adjustmentMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
        return null;
    }
}
