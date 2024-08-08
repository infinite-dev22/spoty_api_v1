package io.nomard.spoty_api_v1.services.implementations.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.sales.SaleMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.sales.SaleMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class SaleMasterServiceImpl implements SaleMasterService {
    @Autowired
    private SaleMasterRepository saleMasterRepo;
    @Autowired
    private SaleTransactionServiceImpl saleTransactionService;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public Page<SaleMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return saleMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
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
    public ArrayList<SaleMaster> getByContains(String search) {
        return saleMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(SaleMaster saleMaster) {
        var subTotal = 0.00;
        var total = 0.00;
        for (int i = 0; i < saleMaster.getSaleDetails().size(); i++) {
            saleMaster.getSaleDetails().get(i).setSale(saleMaster);
            subTotal += saleMaster.getSaleDetails().get(i).getSubTotalPrice();
            total = subTotal;
        }
        if (Objects.nonNull(saleMaster.getTax()) && saleMaster.getTax().getPercentage() > 0.0) {
            total += total * (saleMaster.getTax().getPercentage() / 100);
        }
        if (Objects.nonNull(saleMaster.getDiscount()) && saleMaster.getDiscount().getPercentage() > 0.0) {
            total += total * (saleMaster.getDiscount().getPercentage() / 100);
        }
        saleMaster.setSubTotal(subTotal);
        saleMaster.setTotal(total);
        saleMaster.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(saleMaster.getBranch())) {
            saleMaster.setBranch(authService.authUser().getBranch());
        }
        if (!Objects.equals(total, 0d)) {
            var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(total);
            accountTransaction.setTransactionType("Sale");
            accountTransaction.setNote("Sale made");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionService.save(accountTransaction);
        }
        saleMaster.setCreatedBy(authService.authUser());
        saleMaster.setCreatedAt(LocalDateTime.now());
        try {
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
        var subTotal = 0.00;
        var total = 0.00;
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleMaster = opt.get();
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
            if (Objects.nonNull(data.getTax())
                    && !Objects.equals(saleMaster.getTax().getPercentage(), data.getTax().getPercentage())
                    && saleMaster.getTax().getPercentage() > 0.0) {
                total += subTotal * (data.getTax().getPercentage() / 100);
            }
            if (Objects.nonNull(data.getDiscount())
                    && !Objects.equals(saleMaster.getDiscount().getPercentage(), data.getDiscount().getPercentage())
                    && saleMaster.getTax().getPercentage() > 0.0) {
                total += subTotal * (data.getDiscount().getPercentage() / 100);
            }
            for (int i = 0; i < data.getSaleDetails().size(); i++) {
                if (Objects.isNull(data.getSaleDetails().get(i).getSale())) {
                    data.getSaleDetails().get(i).setSale(saleMaster);
                }
                subTotal += data.getSaleDetails().get(i).getSubTotalPrice();
                try {
                    saleTransactionService.update(saleMaster.getSaleDetails().get(i));
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!Objects.equals(data.getTax(), saleMaster.getTax()) && Objects.nonNull(data.getTax())) {
            saleMaster.setTax(data.getTax());
        }
        if (!Objects.equals(data.getDiscount(), saleMaster.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            saleMaster.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getTotal(), total)) {
            saleMaster.setTotal(total);
        }
        if (!Objects.equals(data.getSubTotal(), subTotal)) {
            saleMaster.setSubTotal(subTotal);
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
        saleMaster.setUpdatedAt(LocalDateTime.now());
        try {
            saleMasterRepo.saveAndFlush(saleMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
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
