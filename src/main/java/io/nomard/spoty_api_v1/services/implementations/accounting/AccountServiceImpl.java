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
        Optional<Account> bank = bankRepo.findById(id);
        if (bank.isEmpty()) {
            throw new NotFoundException();
        }
        return bank.get();
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
        var bank = opt.get();

        if (Objects.nonNull(data.getBankName()) && !"".equalsIgnoreCase(data.getBankName())) {
            bank.setBankName(data.getBankName());
        }

        if (Objects.nonNull(data.getAccountName()) && !"".equalsIgnoreCase(data.getAccountName())) {
            bank.setAccountName(data.getAccountName());
        }

        if (Objects.nonNull(data.getAccountNumber()) && !"".equalsIgnoreCase(data.getAccountNumber())) {
            bank.setAccountNumber(data.getAccountNumber());
        }

        if (Objects.nonNull(data.getBalance()) && !"".equalsIgnoreCase(data.getBalance())) {
            bank.setBalance(data.getBalance());
        }

        // TODO: Add image url only after successful upload of image in case any is provided.
        if (Objects.nonNull(data.getLogo()) && !"".equalsIgnoreCase(data.getLogo())) {
            bank.setLogo(data.getLogo());
        }

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
