package io.nomard.spoty_api_v1.services.implementations.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.sale_returns.SaleReturnDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns.SaleReturnDetailService;
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
public class SaleReturnDetailServiceImpl implements SaleReturnDetailService {
    @Autowired
    private SaleReturnDetailRepository saleReturnDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<SaleReturnDetail> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        return saleReturnDetailRepo.findAll(pageRequest);
    }

    @Override
    public SaleReturnDetail getById(Long id) throws NotFoundException {
        Optional<SaleReturnDetail> saleReturnDetail = saleReturnDetailRepo.findById(id);
        if (saleReturnDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return saleReturnDetail.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(SaleReturnDetail saleReturnDetail) {
        try {
            saleReturnDetail.setCreatedBy(authService.authUser());
            saleReturnDetail.setCreatedAt(LocalDateTime.now());
            saleReturnDetailRepo.saveAndFlush(saleReturnDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<SaleReturnDetail> saleReturnDetailList) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(SaleReturnDetail data) throws NotFoundException {
        var opt = saleReturnDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleReturnDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            saleReturnDetail.setProduct(data.getProduct());
        }

//        if (!Objects.equals(data.getNetTax(), saleReturnDetail.getNetTax())) {
//            saleReturnDetail.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            saleReturnDetail.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), saleReturnDetail.getDiscount())) {
//            saleReturnDetail.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            saleReturnDetail.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            saleReturnDetail.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), saleReturnDetail.getTotal())) {
//            saleReturnDetail.setTotal(data.getTotal());
//        }

        if (!Objects.equals(data.getQuantity(), saleReturnDetail.getQuantity())) {
            saleReturnDetail.setQuantity(data.getQuantity());
        }

        saleReturnDetail.setUpdatedBy(authService.authUser());
        saleReturnDetail.setUpdatedAt(LocalDateTime.now());

        try {
            saleReturnDetailRepo.saveAndFlush(saleReturnDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleReturnDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        try {
            saleReturnDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
