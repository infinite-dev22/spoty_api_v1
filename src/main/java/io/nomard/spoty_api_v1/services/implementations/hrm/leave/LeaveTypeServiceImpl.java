package io.nomard.spoty_api_v1.services.implementations.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.leave.LeaveTypeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.leave.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {
    @Autowired
    private LeaveTypeRepository leaveTypeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<LeaveType> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<LeaveType> page = leaveTypeRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public LeaveType getById(Long id) throws NotFoundException {
        Optional<LeaveType> leaveType = leaveTypeRepo.findById(id);
        if (leaveType.isEmpty()) {
            throw new NotFoundException();
        }
        return leaveType.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(LeaveType leaveType) {
        try {
            leaveType.setCreatedBy(authService.authUser());
            leaveType.setCreatedAt(new Date());
            leaveTypeRepo.saveAndFlush(leaveType);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(LeaveType data) throws NotFoundException {
        var opt = leaveTypeRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var leaveType = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            leaveType.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), leaveType.getNetTax())) {
//            leaveType.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            leaveType.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), leaveType.getDiscount())) {
//            leaveType.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            leaveType.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            leaveType.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), leaveType.getTotal())) {
//            leaveType.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), leaveType.getQuantity())) {
//            leaveType.setQuantity(data.getQuantity());
//        }

        leaveType.setUpdatedBy(authService.authUser());
        leaveType.setUpdatedAt(new Date());

        try {
            leaveTypeRepo.saveAndFlush(leaveType);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            leaveTypeRepo.deleteById(id);
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
