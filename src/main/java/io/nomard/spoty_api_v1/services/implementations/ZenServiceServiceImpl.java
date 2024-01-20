package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ZenService;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ZenServiceRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ZenServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ZenServiceServiceImpl implements ZenServiceService {
    @Autowired
    private ZenServiceRepository zenServiceRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<ZenService> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<ZenService> page = zenServiceRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public ZenService getById(Long id) throws NotFoundException {
        Optional<ZenService> attendance = zenServiceRepo.findById(id);
        if (attendance.isEmpty()) {
            throw new NotFoundException();
        }
        return attendance.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(ZenService attendance) {
        try {
            attendance.setCreatedBy(authService.authUser());
            attendance.setCreatedAt(new Date());
            zenServiceRepo.saveAndFlush(attendance);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(ZenService data) throws NotFoundException {
        var opt = zenServiceRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var attendance = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            attendance.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), attendance.getNetTax())) {
//            attendance.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            attendance.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), attendance.getDiscount())) {
//            attendance.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            attendance.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            attendance.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), attendance.getTotal())) {
//            attendance.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), attendance.getQuantity())) {
//            attendance.setQuantity(data.getQuantity());
//        }

        attendance.setUpdatedBy(authService.authUser());
        attendance.setUpdatedAt(new Date());

        try {
            zenServiceRepo.saveAndFlush(attendance);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            zenServiceRepo.deleteById(id);
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
