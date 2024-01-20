package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Bank;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BankRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankRepository bankRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Bank> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Bank> page = bankRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Bank getById(Long id) throws NotFoundException {
        Optional<Bank> bank = bankRepo.findById(id);
        if (bank.isEmpty()) {
            throw new NotFoundException();
        }
        return bank.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(Bank bank) {
        try {
            bank.setCreatedBy(authService.authUser());
            bank.setCreatedAt(new Date());
            bankRepo.saveAndFlush(bank);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Bank data) throws NotFoundException {
        var opt = bankRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var bank = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            bank.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), bank.getNetTax())) {
//            bank.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            bank.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), bank.getDiscount())) {
//            bank.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            bank.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            bank.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), bank.getTotal())) {
//            bank.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), bank.getQuantity())) {
//            bank.setQuantity(data.getQuantity());
//        }

        bank.setUpdatedBy(authService.authUser());
        bank.setUpdatedAt(new Date());

        try {
            bankRepo.saveAndFlush(bank);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            bankRepo.deleteById(id);
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
