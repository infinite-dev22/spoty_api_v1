package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.EmploymentStatusRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.hrm.EmploymentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmploymentStatusServiceImpl implements EmploymentStatusService {
    @Autowired
    private EmploymentStatusRepository employmentStatusRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<EmploymentStatus> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<EmploymentStatus> page = employmentStatusRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public EmploymentStatus getById(Long id) throws NotFoundException {
        Optional<EmploymentStatus> employmentStatus = employmentStatusRepo.findById(id);
        if (employmentStatus.isEmpty()) {
            throw new NotFoundException();
        }
        return employmentStatus.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(EmploymentStatus employmentStatus) {
        try {
            employmentStatus.setCreatedBy(authService.authUser());
            employmentStatus.setCreatedAt(new Date());
            employmentStatusRepo.saveAndFlush(employmentStatus);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(EmploymentStatus data) throws NotFoundException {
        var opt = employmentStatusRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var employmentStatus = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            employmentStatus.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), employmentStatus.getNetTax())) {
//            employmentStatus.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            employmentStatus.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), employmentStatus.getDiscount())) {
//            employmentStatus.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            employmentStatus.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            employmentStatus.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), employmentStatus.getTotal())) {
//            employmentStatus.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), employmentStatus.getQuantity())) {
//            employmentStatus.setQuantity(data.getQuantity());
//        }

        employmentStatus.setUpdatedBy(authService.authUser());
        employmentStatus.setUpdatedAt(new Date());

        try {
            employmentStatusRepo.saveAndFlush(employmentStatus);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            employmentStatusRepo.deleteById(id);
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
