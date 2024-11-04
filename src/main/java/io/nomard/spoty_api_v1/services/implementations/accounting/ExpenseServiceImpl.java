package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.ExpenseDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.ExpenseMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.ExpenseRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private ExpenseMapper expenseMapper;

    @Override
    public Page<ExpenseDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return expenseRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(expense -> expenseMapper.toDTO(expense));
    }

    @Override
    public ExpenseDTO getById(Long id) throws NotFoundException {
        Optional<Expense> expense = expenseRepo.findById(id);
        if (expense.isEmpty()) {
            throw new NotFoundException();
        }
        return expenseMapper.toDTO(expense.get());
    }

    @Override
    public List<ExpenseDTO> getByContains(String search) {
        return expenseRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase())
                .stream()
                .map(expense -> expenseMapper.toDTO(expense))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Expense expense) {
        try {
            expense.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(expense.getBranch())) {
                expense.setBranch(authService.authUser().getBranch());
            }
            var account = accountService.getByContains(authService.authUser().getTenant(), "Default Account");
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(expense.getAmount());
            accountTransaction.setTransactionType("Expense");
            accountTransaction.setNote("Expense made");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionService.save(accountTransaction);
            expense.setCreatedBy(authService.authUser());
            expense.setCreatedAt(LocalDateTime.now());
            expenseRepo.save(expense);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Expense data) throws NotFoundException {
        var opt = expenseRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var expense = opt.get();

        if (Objects.nonNull(data.getAccount()) && !Objects.equals(expense.getAccount(), data.getAccount())) {
            expense.setAccount(data.getAccount());
        }

        if (Objects.nonNull(data.getName()) && !Objects.equals(expense.getName(), data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            expense.setName(data.getName());
        }

        if (Objects.nonNull(data.getRef()) && !Objects.equals(expense.getRef(), data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            expense.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getNote()) && !Objects.equals(expense.getNote(), data.getNote()) && !"".equalsIgnoreCase(data.getNote())) {
            expense.setNote(data.getNote());
        }

        if (!Objects.equals(expense.getAmount(), data.getAmount()) && !Objects.equals(data.getAmount(), 0)) {
            expense.setAmount(data.getAmount());
        }

        if (!Objects.equals(expense.getDate(), data.getDate()) && Objects.nonNull(data.getDate())) {
            expense.setDate(data.getDate());
        }

        expense.setUpdatedBy(authService.authUser());
        expense.setUpdatedAt(LocalDateTime.now());

        try {
            expenseRepo.save(expense);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            expenseRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            expenseRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
