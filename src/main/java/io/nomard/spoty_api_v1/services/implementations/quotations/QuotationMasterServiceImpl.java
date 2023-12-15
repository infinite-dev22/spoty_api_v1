package io.nomard.spoty_api_v1.services.implementations.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.quotations.QuotationMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.quotations.QuotationMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuotationMasterServiceImpl implements QuotationMasterService {
    @Autowired
    private QuotationMasterRepository quotationMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<QuotationMaster> getAll() {
        return quotationMasterRepo.findAll();
    }

    @Override
    public QuotationMaster getById(Long id) throws NotFoundException {
        Optional<QuotationMaster> quotationMaster = quotationMasterRepo.findById(id);
        if (quotationMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return quotationMaster.get();
    }

    @Override
    public List<QuotationMaster> getByContains(String search) {
        return quotationMasterRepo.searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(QuotationMaster quotationMaster) {
        try {
            quotationMaster.setCreatedBy(authService.authUser());
            quotationMaster.setCreatedAt(new Date());
            quotationMasterRepo.saveAndFlush(quotationMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(QuotationMaster data) throws NotFoundException {
        var opt = quotationMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var quotationMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            quotationMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate())) {
            quotationMaster.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getCustomer())) {
            quotationMaster.setCustomer(data.getCustomer());
        }

        if (Objects.nonNull(data.getBranch())) {
            quotationMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getQuotationDetails()) && !data.getQuotationDetails().isEmpty()) {
            quotationMaster.setQuotationDetails(data.getQuotationDetails());
        }

//        if (!Objects.equals(data.getTaxRate(), quotationMaster.getTaxRate())) {
//            quotationMaster.setTaxRate(data.getTaxRate());
//        }
//
//        if (!Objects.equals(data.getNetTax(), quotationMaster.getNetTax())) {
//            quotationMaster.setNetTax(data.getNetTax());
//        }
//
//        if (!Objects.equals(data.getDiscount(), quotationMaster.getDiscount())) {
//            quotationMaster.setDiscount(data.getDiscount());
//        }

        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
            quotationMaster.setShipping(data.getShipping());
        }

//        if (!Objects.equals(data.getPaid(), quotationMaster.getPaid())) {
//            quotationMaster.setPaid(data.getPaid());
//        }
//
//        if (!Objects.equals(data.getTotal(), quotationMaster.getTotal())) {
//            quotationMaster.setTotal(data.getTotal());
//        }
//
//        if (!Objects.equals(data.getDue(), quotationMaster.getDue())) {
//            quotationMaster.setDue(data.getDue());
//        }
//
//        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
//            quotationMaster.setStatus(data.getStatus());
//        }
//
//        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
//            quotationMaster.setPaymentStatus(data.getPaymentStatus());
//        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            quotationMaster.setNotes(data.getNotes());
        }

        quotationMaster.setUpdatedBy(authService.authUser());
        quotationMaster.setUpdatedAt(new Date());

        try {
            quotationMasterRepo.saveAndFlush(quotationMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            quotationMasterRepo.deleteById(id);
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
