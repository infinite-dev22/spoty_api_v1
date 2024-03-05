package io.nomard.spoty_api_v1.services.implementations.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.requisitions.RequisitionDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.requisitions.RequisitionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RequisitionDetailServiceImpl implements RequisitionDetailService {
    @Autowired
    private RequisitionDetailRepository requisitionDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<RequisitionDetail> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<RequisitionDetail> page = requisitionDetailRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public RequisitionDetail getById(Long id) throws NotFoundException {
        Optional<RequisitionDetail> requisitionDetail = requisitionDetailRepo.findById(id);
        if (requisitionDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return requisitionDetail.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(RequisitionDetail requisitionDetail) {
        try {
            requisitionDetail.setCreatedBy(authService.authUser());
            requisitionDetail.setCreatedAt(new Date());
            requisitionDetailRepo.saveAndFlush(requisitionDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<RequisitionDetail> requisitionDetailList) {
        return null;
    }

    @Override
    public ResponseEntity<ObjectNode> update(RequisitionDetail data) throws NotFoundException {
        var opt = requisitionDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var requisitionDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            requisitionDetail.setProduct(data.getProduct());
        }

//        if (!Objects.equals(data.getNetTax(), requisitionDetail.getNetTax())) {
//            requisitionDetail.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            requisitionDetail.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), requisitionDetail.getDiscount())) {
//            requisitionDetail.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            requisitionDetail.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            requisitionDetail.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), requisitionDetail.getTotal())) {
//            requisitionDetail.setTotal(data.getTotal());
//        }

        if (!Objects.equals(data.getQuantity(), requisitionDetail.getQuantity())) {
            requisitionDetail.setQuantity(data.getQuantity());
        }

        requisitionDetail.setUpdatedBy(authService.authUser());
        requisitionDetail.setUpdatedAt(new Date());

        try {
            requisitionDetailRepo.saveAndFlush(requisitionDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            requisitionDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        try {
            requisitionDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
