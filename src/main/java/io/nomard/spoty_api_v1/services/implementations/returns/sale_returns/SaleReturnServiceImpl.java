package io.nomard.spoty_api_v1.services.implementations.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.returns.sale_returns.SaleReturnMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.DiscountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.deductions.TaxServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleTransactionServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns.SaleReturnService;
import io.nomard.spoty_api_v1.utils.CoreCalculations;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class SaleReturnServiceImpl implements SaleReturnService {
    @Autowired
    private SaleReturnMasterRepository saleReturnRepo;
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
    @Autowired
    private TaxServiceImpl taxService;
    @Autowired
    private DiscountServiceImpl discountService;

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public Page<SaleReturnMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return saleReturnRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public SaleReturnMaster getById(Long id) throws NotFoundException {
        Optional<SaleReturnMaster> saleMaster = saleReturnRepo.findById(id);
        if (saleMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return saleMaster.get();
    }

    @Override
    @Cacheable("sale_masters")
    @Transactional(readOnly = true)
    public ArrayList<SaleReturnMaster> getByContains(String search) {
        return saleReturnRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
//    @Transactional
    public ResponseEntity<ObjectNode> save(SaleReturnMaster sale) throws NotFoundException {
        // Perform calculations
        var calculationService = new CoreCalculations.SaleCalculationService(taxService, discountService);
        calculationService.calculate(sale);

        // Set additional details
        sale.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(sale.getBranch())) {
            sale.setBranch(authService.authUser().getBranch());
        }
        sale.setCreatedBy(authService.authUser());
        sale.setCreatedAt(LocalDateTime.now());

        try {
            saleReturnRepo.save(sale);

            // Create account transaction of this sale.
            var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(sale.getTotal());
            accountTransaction.setTransactionType("Sale");
            accountTransaction.setNote("Sale made");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionService.save(accountTransaction);

            return spotyResponseImpl.created();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "sale_masters", key = "#data.id")
    public ResponseEntity<ObjectNode> update(SaleReturnMaster data) throws NotFoundException {
        var opt = saleReturnRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var sale = opt.get();

        // Update fields as needed
        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            sale.setRef(data.getRef());
        }
        if (!Objects.equals(sale.getCustomer(), data.getCustomer()) && Objects.nonNull(data.getCustomer())) {
            sale.setCustomer(data.getCustomer());
        }
        if (!Objects.equals(sale.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
            sale.setBranch(data.getBranch());
        }
        if (Objects.nonNull(data.getSaleReturnDetails()) && !data.getSaleReturnDetails().isEmpty()) {
            sale.setSaleReturnDetails(data.getSaleReturnDetails());
        }

        // Perform calculations
        var calculationService = new CoreCalculations.SaleCalculationService(taxService, discountService);
        calculationService.calculate(sale);

        // Update other fields
        if (!Objects.equals(data.getTax(), sale.getTax()) && Objects.nonNull(data.getTax())) {
            sale.setTax(data.getTax());
        }
        if (!Objects.equals(data.getDiscount(), sale.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            sale.setDiscount(data.getDiscount());
        }
        if (!Objects.equals(data.getAmountPaid(), sale.getAmountPaid())) {
            sale.setAmountPaid(data.getAmountPaid());
        }
        if (!Objects.equals(data.getAmountDue(), sale.getAmountDue())) {
            sale.setAmountDue(data.getAmountDue());
        }
        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
            sale.setPaymentStatus(data.getPaymentStatus());
        }
        if (Objects.nonNull(data.getSaleStatus()) && !"".equalsIgnoreCase(data.getSaleStatus())) {
            sale.setSaleStatus(data.getSaleStatus());
        }
        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            sale.setNotes(data.getNotes());
        }
        sale.setUpdatedBy(authService.authUser());
        sale.setUpdatedAt(LocalDateTime.now());
        try {
            saleReturnRepo.save(sale);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleReturnRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            saleReturnRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
