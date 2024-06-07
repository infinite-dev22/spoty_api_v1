package io.nomard.spoty_api_v1.services.implementations.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.stock_ins.StockInDetailService;
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
public class StockInDetailServiceImpl implements StockInDetailService {
    @Autowired
    private StockInDetailRepository stockInDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<StockInDetail> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<StockInDetail> page = stockInDetailRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public StockInDetail getById(Long id) throws NotFoundException {
        Optional<StockInDetail> stockInDetail = stockInDetailRepo.findById(id);
        if (stockInDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return stockInDetail.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(StockInDetail stockInDetail) {
        try {
            stockInDetail.setCreatedBy(authService.authUser());
            stockInDetail.setCreatedAt(new Date());
            stockInDetailRepo.saveAndFlush(stockInDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<StockInDetail> stockInDetailList) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(StockInDetail data) throws NotFoundException {
        var opt = stockInDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockInDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            stockInDetail.setProduct(data.getProduct());
        }

//        if (!Objects.equals(data.getNetTax(), stockInDetail.getNetTax())) {
//            stockInDetail.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            stockInDetail.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), stockInDetail.getDiscount())) {
//            stockInDetail.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            stockInDetail.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            stockInDetail.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), stockInDetail.getTotal())) {
//            stockInDetail.setTotal(data.getTotal());
//        }

        if (!Objects.equals(data.getQuantity(), stockInDetail.getQuantity())) {
            stockInDetail.setQuantity(data.getQuantity());
        }

        stockInDetail.setUpdatedBy(authService.authUser());
        stockInDetail.setUpdatedAt(new Date());

        try {
            stockInDetailRepo.saveAndFlush(stockInDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            stockInDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        try {
            stockInDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
