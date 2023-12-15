package io.nomard.spoty_api_v1.services.implementations.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.sale_returns.SaleReturnMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns.SaleReturnMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleReturnMasterServiceImpl implements SaleReturnMasterService {
    @Autowired
    private SaleReturnMasterRepository saleReturnMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<SaleReturnMaster> getAll() {
        return saleReturnMasterRepo.findAll();
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
    public List<SaleReturnMaster> getByContains(String search) {
        return saleReturnMasterRepo.searchAllByRefContainingIgnoreCase(
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(SaleReturnMaster saleReturnMaster) {
        try {
            saleReturnMaster.setCreatedBy(authService.authUser());
            saleReturnMaster.setCreatedAt(new Date());
            saleReturnMasterRepo.saveAndFlush(saleReturnMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
        saleReturnMaster.setUpdatedAt(new Date());

        try {
            saleReturnMasterRepo.saveAndFlush(saleReturnMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
        return null;
    }
}
