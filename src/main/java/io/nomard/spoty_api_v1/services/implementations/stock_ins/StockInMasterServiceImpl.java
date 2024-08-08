package io.nomard.spoty_api_v1.services.implementations.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.stock_ins.StockInMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StockInMasterServiceImpl implements StockInMasterService {
    @Autowired
    private StockInMasterRepository stockInMasterRepo;
    @Autowired
    private StockInTransactionServiceImpl stockInTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<StockInMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return stockInMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public StockInMaster getById(Long id) throws NotFoundException {
        Optional<StockInMaster> stockInMaster = stockInMasterRepo.findById(id);
        if (stockInMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return stockInMaster.get();
    }

    @Override
    public ArrayList<StockInMaster> getByContains(String search) {
        return stockInMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(StockInMaster stockInMaster) {
        try {
            if (!stockInMaster.getStockInDetails().isEmpty()) {
                for (int i = 0; i < stockInMaster.getStockInDetails().size(); i++) {
                    stockInMaster.getStockInDetails().get(i).setStockIn(stockInMaster);
                }
            }
            stockInMaster.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(stockInMaster.getBranch())) {
                stockInMaster.setBranch(authService.authUser().getBranch());
            }
            stockInMaster.setCreatedBy(authService.authUser());
            stockInMaster.setCreatedAt(LocalDateTime.now());
            stockInMasterRepo.saveAndFlush(stockInMaster);

            if (!stockInMaster.getStockInDetails().isEmpty()) {
                for (int i = 0; i < stockInMaster.getStockInDetails().size(); i++) {
                    stockInTransactionService.save(stockInMaster.getStockInDetails().get(i));
                }
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(StockInMaster data) throws NotFoundException {
        var opt = stockInMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var stockInMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            stockInMaster.setRef(data.getRef());
        }

        if (!Objects.equals(stockInMaster.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
            stockInMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getStockInDetails()) && !data.getStockInDetails().isEmpty()) {
            stockInMaster.setStockInDetails(data.getStockInDetails());

            for (int i = 0; i < stockInMaster.getStockInDetails().size(); i++) {
                stockInMaster.getStockInDetails().get(i).setStockIn(stockInMaster);
                try {
                    stockInTransactionService.update(stockInMaster.getStockInDetails().get(i));
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            stockInMaster.setNotes(data.getNotes());
        }

        stockInMaster.setUpdatedBy(authService.authUser());
        stockInMaster.setUpdatedAt(LocalDateTime.now());

        try {
            stockInMasterRepo.saveAndFlush(stockInMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            stockInMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            stockInMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
