package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SalaryAdvance;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.SalaryAdvanceRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.SalaryAdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryAdvanceServiceImpl implements SalaryAdvanceService {
    @Autowired
    private SalaryAdvanceRepository salaryAdvanceRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<SalaryAdvance> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<SalaryAdvance> page = salaryAdvanceRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public SalaryAdvance getById(Long id) throws NotFoundException {
        Optional<SalaryAdvance> salaryAdvance = salaryAdvanceRepo.findById(id);
        if (salaryAdvance.isEmpty()) {
            throw new NotFoundException();
        }
        return salaryAdvance.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(SalaryAdvance salaryAdvance) {
        try {
            salaryAdvance.setCreatedBy(authService.authUser());
            salaryAdvance.setCreatedAt(new Date());
            salaryAdvanceRepo.saveAndFlush(salaryAdvance);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(SalaryAdvance data) throws NotFoundException {
        var opt = salaryAdvanceRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var salaryAdvance = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            salaryAdvance.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), salaryAdvance.getNetTax())) {
//            salaryAdvance.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            salaryAdvance.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), salaryAdvance.getDiscount())) {
//            salaryAdvance.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            salaryAdvance.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            salaryAdvance.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), salaryAdvance.getTotal())) {
//            salaryAdvance.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), salaryAdvance.getQuantity())) {
//            salaryAdvance.setQuantity(data.getQuantity());
//        }

        salaryAdvance.setUpdatedBy(authService.authUser());
        salaryAdvance.setUpdatedAt(new Date());

        try {
            salaryAdvanceRepo.saveAndFlush(salaryAdvance);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            salaryAdvanceRepo.deleteById(id);
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
