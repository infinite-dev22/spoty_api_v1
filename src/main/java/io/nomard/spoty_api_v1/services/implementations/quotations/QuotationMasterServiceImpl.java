package io.nomard.spoty_api_v1.services.implementations.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.quotations.QuotationMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.quotations.QuotationMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<QuotationMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<QuotationMaster> page = quotationMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        return page.getContent();
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
        return quotationMasterRepo.searchAllByRefContainingIgnoreCaseOrStatusContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase()
        );
    }

    @Override
//    @Transactional
    public ResponseEntity<ObjectNode> save(QuotationMaster quotationMaster) {
        var total = 0.00;
        if (!quotationMaster.getQuotationDetails().isEmpty()) {
            for (int i = 0; i < quotationMaster.getQuotationDetails().size(); i++) {
                var quotationDetail = quotationMaster.getQuotationDetails().get(i);
                quotationDetail.setQuotation(quotationMaster);
                total += quotationDetail.getSubTotal();
            }
        }
        quotationMaster.setTotal(total);
        quotationMaster.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(quotationMaster.getBranch())) {
            quotationMaster.setBranch(authService.authUser().getBranch());
        }
        quotationMaster.setCreatedBy(authService.authUser());
        quotationMaster.setCreatedAt(LocalDateTime.now());
        try {
            quotationMasterRepo.saveAndFlush(quotationMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(QuotationMaster data) throws NotFoundException {
        var opt = quotationMasterRepo.findById(data.getId());
        var total = 0.00;

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var quotationMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            quotationMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getCustomer()) && !Objects.equals(data.getCustomer(), quotationMaster.getCustomer())) {
            quotationMaster.setCustomer(data.getCustomer());
        }

        if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), quotationMaster.getBranch())) {
            quotationMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getQuotationDetails()) && !data.getQuotationDetails().isEmpty()) {
            quotationMaster.setQuotationDetails(data.getQuotationDetails());

            for (int i = 0; i < data.getQuotationDetails().size(); i++) {
                var quotationDetail = data.getQuotationDetails().get(i);
                if (Objects.isNull(quotationDetail.getQuotation())) {
                    quotationDetail.setQuotation(quotationMaster);
                }
                total += quotationDetail.getSubTotal();
            }
        }

        if (!Objects.equals(data.getTax(), quotationMaster.getTax())) {
            quotationMaster.setTax(data.getTax());
        }

        if (!Objects.equals(data.getDiscount(), quotationMaster.getDiscount())) {
            quotationMaster.setDiscount(data.getDiscount());
        }

        if (!Objects.equals(data.getTotal(), quotationMaster.getTotal())) {
            quotationMaster.setTotal(total);
        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            quotationMaster.setStatus(data.getStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            quotationMaster.setNotes(data.getNotes());
        }

        quotationMaster.setUpdatedBy(authService.authUser());
        quotationMaster.setUpdatedAt(LocalDateTime.now());

        try {
            quotationMasterRepo.saveAndFlush(quotationMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
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
        try {
            quotationMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
