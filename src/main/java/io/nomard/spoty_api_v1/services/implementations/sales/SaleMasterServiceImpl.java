package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleMasterServiceImpl implements SaleMasterService {
    @Autowired
    private SaleMasterRepository saleMasterRepo;
    @Autowired
    private SaleTransactionServiceImpl saleTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public List<SaleMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<SaleMaster> page = saleMasterRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public SaleMaster getById(Long id) throws NotFoundException {
        Optional<SaleMaster> saleMaster = saleMasterRepo.findById(id);
        if (saleMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return saleMaster.get();
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public List<SaleMaster> getByContains(String search) {
        return saleMasterRepo.searchAllByRefContainingIgnoreCase(
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(SaleMaster saleMaster) {
        try {
            for (int i = 0; i < saleMaster.getSaleDetails().size(); i++) {
                saleMaster.getSaleDetails().get(i).setSale(saleMaster);
            }

            saleMaster.setCreatedBy(authService.authUser());
            saleMaster.setCreatedAt(new Date());
            saleMasterRepo.saveAndFlush(saleMaster);

            for (int i = 0; i < saleMaster.getSaleDetails().size(); i++) {
                saleTransactionService.save(saleMaster.getSaleDetails().get(i));
            }

            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "sale_masters", key = "#data.id")
    public ResponseEntity<ObjectNode> update(SaleMaster data) throws NotFoundException {
        var opt = saleMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleMaster = opt.get();

        if (!Objects.equals(saleMaster.getDate(), data.getDate()) && Objects.nonNull(data.getDate())) {
            saleMaster.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            saleMaster.setRef(data.getRef());
        }

        if (!Objects.equals(saleMaster.getCustomer(), data.getCustomer()) && Objects.nonNull(data.getCustomer())) {
            saleMaster.setCustomer(data.getCustomer());
        }

        if (!Objects.equals(saleMaster.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
            saleMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getSaleDetails()) && !data.getSaleDetails().isEmpty()) {
            saleMaster.setSaleDetails(data.getSaleDetails());

            for (int i = 0; i < saleMaster.getSaleDetails().size(); i++) {
                saleMaster.getSaleDetails().get(i).setSale(saleMaster);
                try {
                    saleTransactionService.update(saleMaster.getSaleDetails().get(i));
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (!Objects.equals(data.getTaxRate(), saleMaster.getTaxRate())) {
            saleMaster.setTaxRate(data.getTaxRate());
        }

        if (!Objects.equals(data.getNetTax(), saleMaster.getNetTax())) {
            saleMaster.setNetTax(data.getNetTax());
        }

        if (!Objects.equals(data.getDiscount(), saleMaster.getDiscount())) {
            saleMaster.setDiscount(data.getDiscount());
        }

        if (!Objects.equals(data.getTotal(), saleMaster.getTotal())) {
            saleMaster.setTotal(data.getTotal());
        }

        if (!Objects.equals(data.getAmountPaid(), saleMaster.getAmountPaid())) {
            saleMaster.setAmountPaid(data.getAmountPaid());
        }

        if (!Objects.equals(data.getAmountDue(), saleMaster.getAmountDue())) {
            saleMaster.setAmountDue(data.getAmountDue());
        }

        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            saleMaster.setPaymentStatus(data.getPaymentStatus());
        }

        if (Objects.nonNull(data.getSaleStatus()) && !"".equalsIgnoreCase(data.getSaleStatus())) {
            saleMaster.setSaleStatus(data.getSaleStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            saleMaster.setNotes(data.getNotes());
        }

        saleMaster.setUpdatedBy(authService.authUser());
        saleMaster.setUpdatedAt(new Date());

        try {
            saleMasterRepo.saveAndFlush(saleMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            saleMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
