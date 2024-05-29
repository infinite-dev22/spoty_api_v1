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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public List<StockInMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<StockInMaster> page = stockInMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
    public List<StockInMaster> getByContains(String search) {
        return stockInMasterRepo.searchAllByRefContainingIgnoreCase(
                search.toLowerCase()
        );
    }

    @Override
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
            stockInMaster.setCreatedAt(new Date());
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
        stockInMaster.setUpdatedAt(new Date());

        try {
            stockInMasterRepo.saveAndFlush(stockInMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
