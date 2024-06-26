package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository bankRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Account> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Account> page = bankRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        return page.getContent();
    }

    @Override
    public Account getById(Long id) throws NotFoundException {
        Optional<Account> account = bankRepo.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException();
        }
        return account.get();
    }

    @Override
    public List<Account> getByContains(String search) {
        return bankRepo.searchAllByBankNameContainingIgnoreCaseOrAccountNameContainingIgnoreCaseOrAccountNumberContainsIgnoreCase(
                search, search, search
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Account account) {
        try {
            account.setTenant(authService.authUser().getTenant());
            account.setCreatedBy(authService.authUser());
            account.setCreatedAt(new Date());
            bankRepo.saveAndFlush(account);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Account data) throws NotFoundException {
        var opt = bankRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var account = opt.get();

        if (Objects.nonNull(data.getBankName()) && !Objects.equals(account.getBankName(), data.getBankName()) && !"".equalsIgnoreCase(data.getBankName())) {
            account.setBankName(data.getBankName());
        }

        if (Objects.nonNull(data.getAccountName()) && !Objects.equals(account.getAccountName(), data.getAccountName()) && !"".equalsIgnoreCase(data.getAccountName())) {
            account.setAccountName(data.getAccountName());
        }

        if (Objects.nonNull(data.getAccountNumber()) && !Objects.equals(account.getAccountNumber(), data.getAccountNumber()) && !"".equalsIgnoreCase(data.getAccountNumber())) {
            account.setAccountNumber(data.getAccountNumber());
        }

        if (Objects.nonNull(data.getBalance()) && !Objects.equals(account.getBalance(), data.getBalance())&& !Objects.equals(data.getBalance(), 0d)) {
            account.setBalance(data.getBalance());
        }

        if (Objects.nonNull(data.getCredit()) && !Objects.equals(account.getCredit(), data.getCredit())&& !Objects.equals(data.getCredit(), 0d)) {
            account.setCredit(data.getCredit());
        }

        if (Objects.nonNull(data.getDebit()) && !Objects.equals(account.getDebit(), data.getDebit())&& !Objects.equals(data.getDebit(), 0d)) {
            account.setDebit(data.getDebit());
        }

        if (Objects.nonNull(data.getDescription()) && !Objects.equals(account.getDescription(), data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            account.setDescription(data.getDescription());
        }

        account.setUpdatedBy(authService.authUser());
        account.setUpdatedAt(new Date());

        try {
            bankRepo.saveAndFlush(account);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            bankRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            bankRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
