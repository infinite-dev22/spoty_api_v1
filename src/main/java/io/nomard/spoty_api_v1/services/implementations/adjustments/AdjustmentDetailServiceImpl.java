package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdjustmentDetailServiceImpl implements AdjustmentDetailService {
    @Autowired
    private AdjustmentDetailRepository adjustmentDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<AdjustmentDetail> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        return adjustmentDetailRepo.findAll(pageRequest);
    }

    @Override
    public AdjustmentDetail getById(Long id) throws NotFoundException {
        Optional<AdjustmentDetail> adjustmentDetail = adjustmentDetailRepo.findById(id);
        if (adjustmentDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return adjustmentDetail.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(AdjustmentDetail adjustmentDetail) {
        try {
            adjustmentDetail.setCreatedBy(authService.authUser());
            adjustmentDetail.setCreatedAt(LocalDateTime.now());
            adjustmentDetailRepo.saveAndFlush(adjustmentDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<AdjustmentDetail> adjustmentDetailList) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(AdjustmentDetail data) throws NotFoundException {
        var opt = adjustmentDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var adjustmentDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            adjustmentDetail.setProduct(data.getProduct());
        }

        if (Objects.nonNull(data.getAdjustment())) {
            adjustmentDetail.setAdjustment(data.getAdjustment());
        }

        if (!Objects.equals(data.getQuantity(), adjustmentDetail.getQuantity())) {
            adjustmentDetail.setQuantity(data.getQuantity());
        }

        if (Objects.nonNull(data.getAdjustmentType()) && !"".equalsIgnoreCase(data.getAdjustmentType())) {
            adjustmentDetail.setAdjustmentType(data.getAdjustmentType());
        }

        adjustmentDetail.setUpdatedBy(authService.authUser());
        adjustmentDetail.setUpdatedAt(LocalDateTime.now());

        try {
            adjustmentDetailRepo.saveAndFlush(adjustmentDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            adjustmentDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        try {
            adjustmentDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
