package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.DesignationRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DesignationServiceImpl implements DesignationService {
    @Autowired
    private DesignationRepository designationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Designation> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Designation> page = designationRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Designation getById(Long id) throws NotFoundException {
        Optional<Designation> designation = designationRepo.findById(id);
        if (designation.isEmpty()) {
            throw new NotFoundException();
        }
        return designation.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(Designation designation) {
        try {
            designation.setCreatedBy(authService.authUser());
            designation.setCreatedAt(new Date());
            designationRepo.saveAndFlush(designation);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Designation data) throws NotFoundException {
        var opt = designationRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var designation = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            designation.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), designation.getNetTax())) {
//            designation.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            designation.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), designation.getDiscount())) {
//            designation.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            designation.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            designation.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), designation.getTotal())) {
//            designation.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), designation.getQuantity())) {
//            designation.setQuantity(data.getQuantity());
//        }

        designation.setUpdatedBy(authService.authUser());
        designation.setUpdatedAt(new Date());

        try {
            designationRepo.saveAndFlush(designation);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            designationRepo.deleteById(id);
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
