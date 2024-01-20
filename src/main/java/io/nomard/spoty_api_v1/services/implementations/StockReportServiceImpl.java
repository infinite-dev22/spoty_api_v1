package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.StockReport;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.StockReportRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.StockReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StockReportServiceImpl implements StockReportService {
    @Autowired
    private StockReportRepository stockReportRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<StockReport> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<StockReport> page = stockReportRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public StockReport getById(Long id) throws NotFoundException {
        Optional<StockReport> stockReport = stockReportRepo.findById(id);
        if (stockReport.isEmpty()) {
            throw new NotFoundException();
        }
        return stockReport.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(StockReport stockReport) {
        try {
            stockReport.setCreatedBy(authService.authUser());
            stockReport.setCreatedAt(new Date());
            stockReportRepo.saveAndFlush(stockReport);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(StockReport data) throws NotFoundException {
        var opt = stockReportRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockReport = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            stockReport.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), stockReport.getNetTax())) {
//            stockReport.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            stockReport.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), stockReport.getDiscount())) {
//            stockReport.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            stockReport.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            stockReport.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), stockReport.getTotal())) {
//            stockReport.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), stockReport.getQuantity())) {
//            stockReport.setQuantity(data.getQuantity());
//        }

        stockReport.setUpdatedBy(authService.authUser());
        stockReport.setUpdatedAt(new Date());

        try {
            stockReportRepo.saveAndFlush(stockReport);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            stockReportRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        return null;
    }
}
