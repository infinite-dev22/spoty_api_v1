package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ExpenseCategoryRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {
    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<ExpenseCategory> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<ExpenseCategory> page = expenseCategoryRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public ExpenseCategory getById(Long id) throws NotFoundException {
        Optional<ExpenseCategory> expenseCategory = expenseCategoryRepo.findById(id);
        if (expenseCategory.isEmpty()) {
            throw new NotFoundException();
        }
        return expenseCategory.get();
    }

    @Override
    public List<ExpenseCategory> getByContains(String search) {
        return expenseCategoryRepo.searchAllByNameContainingIgnoreCase(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(ExpenseCategory expenseCategory) {
        try {
            expenseCategory.setCreatedBy(authService.authUser());
            expenseCategory.setCreatedAt(new Date());
            expenseCategoryRepo.saveAndFlush(expenseCategory);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(ExpenseCategory data) throws NotFoundException {
        var opt = expenseCategoryRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var expenseCategory = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            expenseCategory.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            expenseCategory.setDescription(data.getDescription());
        }

        expenseCategory.setUpdatedBy(authService.authUser());
        expenseCategory.setUpdatedAt(new Date());

        try {
            expenseCategoryRepo.saveAndFlush(expenseCategory);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            expenseCategoryRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            expenseCategoryRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
