package io.nomard.spoty_api_v1.services.implementations.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.stock_ins.StockInService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import io.nomard.spoty_api_v1.utils.CoreUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class StockInServiceImpl implements StockInService {
    @Autowired
    private StockInMasterRepository stockInRepo;
    @Autowired
    private StockInTransactionServiceImpl stockInTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<StockInMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return stockInRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public StockInMaster getById(Long id) throws NotFoundException {
        Optional<StockInMaster> stockIn = stockInRepo.findById(id);
        if (stockIn.isEmpty()) {
            throw new NotFoundException();
        }
        return stockIn.get();
    }

    @Override
    public ArrayList<StockInMaster> getByContains(String search) {
        return stockInRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(StockInMaster stockIn) {
        try {
            CoreCalculations.StockInCalculationService.calculate(stockIn);
            stockIn.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(stockIn.getBranch())) {
                stockIn.setBranch(authService.authUser().getBranch());
            }
            stockIn.setCreatedBy(authService.authUser());
            stockIn.setCreatedAt(LocalDateTime.now());
            stockIn.setRef(CoreUtils.referenceNumberGenerator("STK"));
            stockInRepo.save(stockIn);

            if (!stockIn.getStockInDetails().isEmpty()) {
                for (int i = 0; i < stockIn.getStockInDetails().size(); i++) {
                    stockInTransactionService.save(stockIn.getStockInDetails().get(i));
                }
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(StockInMaster data) throws NotFoundException {
        var opt = stockInRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockIn = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            stockIn.setRef(data.getRef());
        }

        if (!Objects.equals(stockIn.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
            stockIn.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getStockInDetails()) && !data.getStockInDetails().isEmpty()) {
            stockIn.setStockInDetails(data.getStockInDetails());
            CoreCalculations.StockInCalculationService.calculate(stockIn);
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            stockIn.setNotes(data.getNotes());
        }

        stockIn.setUpdatedBy(authService.authUser());
        stockIn.setUpdatedAt(LocalDateTime.now());

        try {
            stockInRepo.save(stockIn);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            stockInRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            stockInRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}