package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleDetailService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SaleDetailServiceImpl implements SaleDetailService {
    @Autowired
    private SaleDetailRepository saleDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<SaleDetail> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<SaleDetail> page = saleDetailRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public SaleDetail getById(Long id) throws NotFoundException {
        Optional<SaleDetail> saleDetail = saleDetailRepo.findById(id);
        if (saleDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return saleDetail.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(SaleDetail saleDetail) {
        try {
            saleDetail.setCreatedBy(authService.authUser());
            saleDetail.setCreatedAt(new Date());
            saleDetailRepo.saveAndFlush(saleDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<SaleDetail> saleDetailList) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(SaleDetail data) throws NotFoundException {
        var opt = saleDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            saleDetail.setProduct(data.getProduct());
        }

//        if (!Objects.equals(data.getNetTax(), saleDetail.getNetTax())) {
//            saleDetail.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            saleDetail.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), saleDetail.getDiscount())) {
//            saleDetail.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            saleDetail.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            saleDetail.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), saleDetail.getTotal())) {
//            saleDetail.setTotal(data.getTotal());
//        }

        if (!Objects.equals(data.getQuantity(), saleDetail.getQuantity())) {
            saleDetail.setQuantity(data.getQuantity());
        }

        saleDetail.setUpdatedBy(authService.authUser());
        saleDetail.setUpdatedAt(new Date());

        try {
            saleDetailRepo.saveAndFlush(saleDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        try {
            saleDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
