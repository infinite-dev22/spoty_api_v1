package io.nomard.spoty_api_v1.services.implementations.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.leave.LeaveStatusRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.leave.LeaveStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveStatusServiceImpl implements LeaveStatusService {
    @Autowired
    private LeaveStatusRepository leaveStatusRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<LeaveStatus> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<LeaveStatus> page = leaveStatusRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public LeaveStatus getById(Long id) throws NotFoundException {
        Optional<LeaveStatus> leaveStatus = leaveStatusRepo.findById(id);
        if (leaveStatus.isEmpty()) {
            throw new NotFoundException();
        }
        return leaveStatus.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(LeaveStatus leaveStatus) {
        try {
            leaveStatus.setCreatedBy(authService.authUser());
            leaveStatus.setCreatedAt(new Date());
            leaveStatusRepo.saveAndFlush(leaveStatus);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(LeaveStatus data) throws NotFoundException {
        var opt = leaveStatusRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var leaveStatus = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            leaveStatus.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), leaveStatus.getNetTax())) {
//            leaveStatus.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            leaveStatus.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), leaveStatus.getDiscount())) {
//            leaveStatus.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            leaveStatus.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            leaveStatus.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), leaveStatus.getTotal())) {
//            leaveStatus.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), leaveStatus.getQuantity())) {
//            leaveStatus.setQuantity(data.getQuantity());
//        }

        leaveStatus.setUpdatedBy(authService.authUser());
        leaveStatus.setUpdatedAt(new Date());

        try {
            leaveStatusRepo.saveAndFlush(leaveStatus);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            leaveStatusRepo.deleteById(id);
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
