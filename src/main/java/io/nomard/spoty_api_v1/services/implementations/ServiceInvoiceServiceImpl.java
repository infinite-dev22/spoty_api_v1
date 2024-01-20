package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ServiceInvoice;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ServiceInvoiceRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ServiceInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceInvoiceServiceImpl implements ServiceInvoiceService {
    @Autowired
    private ServiceInvoiceRepository serviceInvoiceRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<ServiceInvoice> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<ServiceInvoice> page = serviceInvoiceRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public ServiceInvoice getById(Long id) throws NotFoundException {
        Optional<ServiceInvoice> serviceInvoice = serviceInvoiceRepo.findById(id);
        if (serviceInvoice.isEmpty()) {
            throw new NotFoundException();
        }
        return serviceInvoice.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(ServiceInvoice serviceInvoice) {
        try {
            serviceInvoice.setCreatedBy(authService.authUser());
            serviceInvoice.setCreatedAt(new Date());
            serviceInvoiceRepo.saveAndFlush(serviceInvoice);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(ServiceInvoice data) throws NotFoundException {
        var opt = serviceInvoiceRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var serviceInvoice = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            serviceInvoice.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), serviceInvoice.getNetTax())) {
//            serviceInvoice.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            serviceInvoice.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), serviceInvoice.getDiscount())) {
//            serviceInvoice.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            serviceInvoice.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            serviceInvoice.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), serviceInvoice.getTotal())) {
//            serviceInvoice.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), serviceInvoice.getQuantity())) {
//            serviceInvoice.setQuantity(data.getQuantity());
//        }

        serviceInvoice.setUpdatedBy(authService.authUser());
        serviceInvoice.setUpdatedAt(new Date());

        try {
            serviceInvoiceRepo.saveAndFlush(serviceInvoice);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            serviceInvoiceRepo.deleteById(id);
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
