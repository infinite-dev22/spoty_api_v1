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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Page<StockReport> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return stockReportRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
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
    @Transactional
    public ResponseEntity<ObjectNode> save(StockReport stockReport) {
        try {
            stockReport.setTenant(authService.authUser().getTenant());
            stockReport.setCreatedBy(authService.authUser());
            stockReport.setCreatedAt(LocalDateTime.now());
            stockReportRepo.save(stockReport);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
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
        stockReport.setUpdatedAt(LocalDateTime.now());

        try {
            stockReportRepo.save(stockReport);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
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
