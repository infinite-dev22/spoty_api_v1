package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.PaySlipTypeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.PaySlipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaySlipTypeServiceImpl implements PaySlipTypeService {
    @Autowired
    private PaySlipTypeRepository paySlipTypeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<PaySlipType> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<PaySlipType> page = paySlipTypeRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public PaySlipType getById(Long id) throws NotFoundException {
        Optional<PaySlipType> paySlipType = paySlipTypeRepo.findById(id);
        if (paySlipType.isEmpty()) {
            throw new NotFoundException();
        }
        return paySlipType.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(PaySlipType paySlipType) {
        try {
            paySlipType.setCreatedBy(authService.authUser());
            paySlipType.setCreatedAt(new Date());
            paySlipTypeRepo.saveAndFlush(paySlipType);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(PaySlipType data) throws NotFoundException {
        var opt = paySlipTypeRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var paySlipType = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            paySlipType.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), paySlipType.getNetTax())) {
//            paySlipType.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            paySlipType.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), paySlipType.getDiscount())) {
//            paySlipType.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            paySlipType.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            paySlipType.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), paySlipType.getTotal())) {
//            paySlipType.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), paySlipType.getQuantity())) {
//            paySlipType.setQuantity(data.getQuantity());
//        }

        paySlipType.setUpdatedBy(authService.authUser());
        paySlipType.setUpdatedAt(new Date());

        try {
            paySlipTypeRepo.saveAndFlush(paySlipType);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            paySlipTypeRepo.deleteById(id);
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
