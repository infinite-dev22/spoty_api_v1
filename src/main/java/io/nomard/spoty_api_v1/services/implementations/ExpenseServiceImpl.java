package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ExpenseRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Expense> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Expense> page = expenseRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Expense getById(Long id) throws NotFoundException {
        Optional<Expense> expense = expenseRepo.findById(id);
        if (expense.isEmpty()) {
            throw new NotFoundException();
        }
        return expense.get();
    }

    @Override
    public List<Expense> getByContains(String search) {
        return expenseRepo.searchAllByNameContainingIgnoreCase(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Expense expense) {
        try {
            expense.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(expense.getBranch())) {
                expense.setBranch(authService.authUser().getBranch());
            }
            expense.setCreatedBy(authService.authUser());
            expense.setCreatedAt(new Date());
            expenseRepo.saveAndFlush(expense);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Expense data) throws NotFoundException {
        var opt = expenseRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var expense = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            expense.setName(data.getName());
        }

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            expense.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDetails()) && !"".equalsIgnoreCase(data.getDetails())) {
            expense.setDetails(data.getDetails());
        }

        if (!(data.getAmount() == 0)) {
            expense.setAmount(data.getAmount());
        }

        if (Objects.nonNull(data.getBranch())) {
            expense.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getDate())) {
            expense.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getExpenseCategory())) {
            expense.setExpenseCategory(data.getExpenseCategory());
        }

        expense.setUpdatedBy(authService.authUser());
        expense.setUpdatedAt(new Date());

        try {
            expenseRepo.saveAndFlush(expense);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
