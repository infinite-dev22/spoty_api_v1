package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.PaySlipRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.PaySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaySlipServiceImpl implements PaySlipService {
    @Autowired
    private PaySlipRepository paySlipRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<PaySlip> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<PaySlip> page = paySlipRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public PaySlip getById(Long id) throws NotFoundException {
        Optional<PaySlip> paySlip = paySlipRepo.findById(id);
        if (paySlip.isEmpty()) {
            throw new NotFoundException();
        }
        return paySlip.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(PaySlip paySlip) {
        try {
            paySlip.setCreatedBy(authService.authUser());
            paySlip.setCreatedAt(new Date());
            paySlipRepo.saveAndFlush(paySlip);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(PaySlip data) throws NotFoundException {
        var opt = paySlipRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var paySlip = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            paySlip.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), paySlip.getNetTax())) {
//            paySlip.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTax()) && !"".equalsIgnoreCase(data.getTax())) {
//            paySlip.setTax(data.getTax());
//        }
//
//        if (!Objects.equals(data.getDiscount(), paySlip.getDiscount())) {
//            paySlip.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscount()) && !"".equalsIgnoreCase(data.getDiscount())) {
//            paySlip.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            paySlip.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), paySlip.getTotal())) {
//            paySlip.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), paySlip.getQuantity())) {
//            paySlip.setQuantity(data.getQuantity());
//        }

        paySlip.setUpdatedBy(authService.authUser());
        paySlip.setUpdatedAt(new Date());

        try {
            paySlipRepo.saveAndFlush(paySlip);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            paySlipRepo.deleteById(id);
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
