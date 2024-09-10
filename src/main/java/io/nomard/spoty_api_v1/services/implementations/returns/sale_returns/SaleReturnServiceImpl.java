package io.nomard.spoty_api_v1.services.implementations.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.sale_returns.SaleReturnMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns.SaleReturnService;
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
public class SaleReturnServiceImpl implements SaleReturnService {
    @Autowired
    private SaleReturnMasterRepository saleReturnMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<SaleReturnMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return saleReturnMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public SaleReturnMaster getById(Long id) throws NotFoundException {
        Optional<SaleReturnMaster> saleReturnMaster = saleReturnMasterRepo.findById(id);
        if (saleReturnMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return saleReturnMaster.get();
    }

    @Override
    public ArrayList<SaleReturnMaster> getByContains(String search) {
        return saleReturnMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(SaleReturnMaster saleReturnMaster) {
        try {
            saleReturnMaster.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(saleReturnMaster.getBranch())) {
                saleReturnMaster.setBranch(authService.authUser().getBranch());
            }
            saleReturnMaster.setCreatedBy(authService.authUser());
            saleReturnMaster.setCreatedAt(LocalDateTime.now());
            saleReturnMasterRepo.save(saleReturnMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(SaleReturnMaster data) throws NotFoundException {
        var opt = saleReturnMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleReturnMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            saleReturnMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate())) {
            saleReturnMaster.setDate(data.getDate());
        }

//        if (Objects.nonNull(data.getCustomer())) {
//            saleReturnMaster.setCustomer(data.getCustomer());
//        }

        if (Objects.nonNull(data.getBranch())) {
            saleReturnMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getSaleReturnDetails()) && !data.getSaleReturnDetails().isEmpty()) {
            saleReturnMaster.setSaleReturnDetails(data.getSaleReturnDetails());
        }

//        if (!Objects.equals(data.getTaxRate(), saleReturnMaster.getTaxRate())) {
//            saleReturnMaster.setTaxRate(data.getTaxRate());
//        }
//
//        if (!Objects.equals(data.getNetTax(), saleReturnMaster.getNetTax())) {
//            saleReturnMaster.setNetTax(data.getNetTax());
//        }
//
//        if (!Objects.equals(data.getDiscount(), saleReturnMaster.getDiscount())) {
//            saleReturnMaster.setDiscount(data.getDiscount());
//        }

//        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
//            saleReturnMaster.setShipping(data.getShipping());
//        }

//        if (!Objects.equals(data.getPaid(), saleReturnMaster.getPaid())) {
//            saleReturnMaster.setPaid(data.getPaid());
//        }
//
//        if (!Objects.equals(data.getTotal(), saleReturnMaster.getTotal())) {
//            saleReturnMaster.setTotal(data.getTotal());
//        }
//
//        if (!Objects.equals(data.getDue(), saleReturnMaster.getDue())) {
//            saleReturnMaster.setDue(data.getDue());
//        }
//
//        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
//            saleReturnMaster.setStatus(data.getStatus());
//        }
//
//        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
//            saleReturnMaster.setPaymentStatus(data.getPaymentStatus());
//        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            saleReturnMaster.setNotes(data.getNotes());
        }

        saleReturnMaster.setUpdatedBy(authService.authUser());
        saleReturnMaster.setUpdatedAt(LocalDateTime.now());

        try {
            saleReturnMasterRepo.save(saleReturnMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleReturnMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            saleReturnMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
