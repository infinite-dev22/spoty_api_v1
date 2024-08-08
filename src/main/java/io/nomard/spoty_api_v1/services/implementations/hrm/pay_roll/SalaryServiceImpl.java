package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.Salary;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.SalaryRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.SalaryService;
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
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private SalaryRepository salaryRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<Salary> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return salaryRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public Salary getById(Long id) throws NotFoundException {
        Optional<Salary> salary = salaryRepo.findById(id);
        if (salary.isEmpty()) {
            throw new NotFoundException();
        }
        return salary.get();
    }

    @Override
    public ArrayList<Salary> getByContains(String search) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Salary salary) {
        try {
            salary.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(salary.getBranch())) {
                salary.setBranch(authService.authUser().getBranch());
            }
            salary.setCreatedBy(authService.authUser());
            salary.setCreatedAt(LocalDateTime.now());
            salaryRepo.saveAndFlush(salary);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Salary data) throws NotFoundException {
        var opt = salaryRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var salary = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            salary.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), salary.getNetTax())) {
//            salary.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            salary.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), salary.getDiscount())) {
//            salary.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            salary.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            salary.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), salary.getTotal())) {
//            salary.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), salary.getQuantity())) {
//            salary.setQuantity(data.getQuantity());
//        }

        salary.setUpdatedBy(authService.authUser());
        salary.setUpdatedAt(LocalDateTime.now());

        try {
            salaryRepo.saveAndFlush(salary);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            salaryRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            salaryRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
