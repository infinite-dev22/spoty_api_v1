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

import java.util.*;

@Service
public class AdjustmentDetailServiceImpl implements AdjustmentDetailService {
    @Autowired
    private AdjustmentDetailRepository adjustmentDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<AdjustmentDetail> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<AdjustmentDetail> page = adjustmentDetailRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
    public ResponseEntity<ObjectNode> save(AdjustmentDetail adjustmentDetail) {
        try {
            adjustmentDetail.setCreatedBy(authService.authUser());
            adjustmentDetail.setCreatedAt(new Date());
            adjustmentDetailRepo.saveAndFlush(adjustmentDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(ArrayList<AdjustmentDetail> adjustmentDetailList) {
        return null;
    }

    @Override
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
        adjustmentDetail.setUpdatedAt(new Date());

        try {
            adjustmentDetailRepo.saveAndFlush(adjustmentDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            adjustmentDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            adjustmentDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
