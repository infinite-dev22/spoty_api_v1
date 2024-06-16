package io.nomard.spoty_api_v1.services.implementations.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.quotations.QuotationDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.quotations.QuotationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuotationDetailServiceImpl implements QuotationDetailService {
    @Autowired
    private QuotationDetailRepository quotationDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<QuotationDetail> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<QuotationDetail> page = quotationDetailRepo.findAll(pageRequest);
        return page.getContent();
    }

    @Override
    public QuotationDetail getById(Long id) throws NotFoundException {
        Optional<QuotationDetail> quotationDetail = quotationDetailRepo.findById(id);
        if (quotationDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return quotationDetail.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(QuotationDetail quotationDetail) {
        try {
            quotationDetail.setCreatedBy(authService.authUser());
            quotationDetail.setCreatedAt(new Date());
            quotationDetailRepo.saveAndFlush(quotationDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<QuotationDetail> quotationDetailList) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(QuotationDetail data) throws NotFoundException {
        var opt = quotationDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var quotationDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            quotationDetail.setProduct(data.getProduct());
        }

        if (!Objects.equals(data.getTax(), quotationDetail.getTax()) && Objects.nonNull(data.getTax())) {
            quotationDetail.setTax(data.getTax());
        }

        if (!Objects.equals(data.getDiscount(), quotationDetail.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            quotationDetail.setDiscount(data.getDiscount());
        }

        if (!Objects.equals(data.getQuantity(), quotationDetail.getQuantity())) {
            quotationDetail.setQuantity(data.getQuantity());
        }

        quotationDetail.setUpdatedBy(authService.authUser());
        quotationDetail.setUpdatedAt(new Date());

        try {
            quotationDetailRepo.saveAndFlush(quotationDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            quotationDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        try {
            quotationDetailRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
