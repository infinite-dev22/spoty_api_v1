package io.nomard.spoty_api_v1.services.implementations.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.quotations.QuotationMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.DiscountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.TaxServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.quotations.QuotationService;
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
public class QuotationServiceImpl implements QuotationService {
    @Autowired
    private QuotationMasterRepository quotationMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private TaxServiceImpl taxService;
    @Autowired
    private DiscountServiceImpl discountService;

    @Override
    public Page<QuotationMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return quotationMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
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
    public ArrayList<QuotationMaster> getByContains(String search) {
        return quotationMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
//    @Transactional
    public ResponseEntity<ObjectNode> save(QuotationMaster quotation) throws NotFoundException {
        // Perform calculations
        var calculationService = new CoreCalculations.QuotationCalculationService(taxService, discountService);
        calculationService.calculate(quotation);

        // Set additional details
        quotation.setRef(CoreUtils.referenceNumberGenerator("QUO"));
        quotation.setTenant(authService.authUser().getTenant());
        quotation.setCreatedBy(authService.authUser());
        quotation.setCreatedAt(LocalDateTime.now());
        if (Objects.isNull(quotation.getBranch())) {
            quotation.setBranch(authService.authUser().getBranch());
        }
        try {
            quotationMasterRepo.save(quotation);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(QuotationMaster data) throws NotFoundException {
        var opt = quotationMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var quotation = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            quotation.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getCustomer()) && !Objects.equals(data.getCustomer(), quotation.getCustomer())) {
            quotation.setCustomer(data.getCustomer());
        }

        if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), quotation.getBranch())) {
            quotation.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getQuotationDetails()) && !data.getQuotationDetails().isEmpty()) {
            quotation.setQuotationDetails(data.getQuotationDetails());
        }

        // Perform calculations
        var calculationService = new CoreCalculations.QuotationCalculationService(taxService, discountService);
        calculationService.calculate(quotation);

        // Update other fields
        if (!Objects.equals(data.getTax(), quotation.getTax())) {
            quotation.setTax(data.getTax());
        }

        if (!Objects.equals(data.getDiscount(), quotation.getDiscount())) {
            quotation.setDiscount(data.getDiscount());
        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            quotation.setStatus(data.getStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            quotation.setNotes(data.getNotes());
        }

        quotation.setUpdatedBy(authService.authUser());
        quotation.setUpdatedAt(LocalDateTime.now());

        try {
            quotationMasterRepo.save(quotation);
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
            quotationMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            quotationMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
