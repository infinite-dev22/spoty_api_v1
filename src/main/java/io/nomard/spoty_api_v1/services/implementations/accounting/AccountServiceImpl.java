package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.AccountDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.AccountMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.AccountService;
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
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Page<AccountDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return accountRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(account -> accountMapper.toDTO(account));
    }

    @Override
    public AccountDTO getById(Long id) throws NotFoundException {
        Optional<Account> account = accountRepo.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException();
        }
        return accountMapper.toDTO(account.get());
    }

    @Override
    public List<AccountDTO> getByContains(String search) {
        return accountRepo.searchAll(authService.authUser().getTenant().getId(), search)
                .stream()
                .map(account -> accountMapper.toDTO(account))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Account account) {
        try {
            account.setTenant(authService.authUser().getTenant());
            var amount = account.getBalance();
            account.setBalance(0d);
            account.setCreatedBy(authService.authUser());
            account.setCreatedAt(LocalDateTime.now());
            accountRepo.save(account);
            if (Objects.nonNull(amount) && !Objects.equals(amount, 0d)) {
                var accountTransaction = new AccountTransaction();
                accountTransaction.setTenant(authService.authUser().getTenant());
                accountTransaction.setTransactionDate(LocalDateTime.now());
                accountTransaction.setAccount(account);
                accountTransaction.setAmount(amount);
                accountTransaction.setTransactionType("Deposit");
                accountTransaction.setNote("Initial deposit");
                accountTransaction.setCreatedBy(authService.authUser());
                accountTransaction.setCreatedAt(LocalDateTime.now());
                accountTransactionService.save(accountTransaction);
            }
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Account data) throws NotFoundException {
        var opt = accountRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var account = opt.get();

        if (Objects.nonNull(data.getAccountName()) && !Objects.equals(account.getAccountName(), data.getAccountName()) && !"".equalsIgnoreCase(data.getAccountName())) {
            account.setAccountName(data.getAccountName());
        }

        if (Objects.nonNull(data.getAccountNumber()) && !Objects.equals(account.getAccountNumber(), data.getAccountNumber()) && !"".equalsIgnoreCase(data.getAccountNumber())) {
            account.setAccountNumber(data.getAccountNumber());
        }

        if (Objects.nonNull(data.getCredit()) && !Objects.equals(account.getCredit(), data.getCredit()) && !Objects.equals(data.getCredit(), 0d)) {
            account.setCredit(data.getCredit());
        }

        if (Objects.nonNull(data.getDebit()) && !Objects.equals(account.getDebit(), data.getDebit()) && !Objects.equals(data.getDebit(), 0d)) {
            account.setDebit(data.getDebit());
        }

        if (Objects.nonNull(data.getDescription()) && !Objects.equals(account.getDescription(), data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            account.setDescription(data.getDescription());
        }

        account.setUpdatedBy(authService.authUser());
        account.setUpdatedAt(LocalDateTime.now());

        try {
            accountRepo.save(account);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> deposit(Account data) throws NotFoundException {
        var opt = accountRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var account = opt.get();
        var amount = data.getBalance();
        if (Objects.nonNull(amount) && !Objects.equals(amount, 0d)) {
            var accountTransaction = new AccountTransaction();
            accountTransaction.setTenant(authService.authUser().getTenant());
            accountTransaction.setTransactionDate(LocalDateTime.now());
            accountTransaction.setAccount(account);
            accountTransaction.setAmount(amount);
            accountTransaction.setCredit(data.getBalance());
            accountTransaction.setTransactionType("Deposit");
            accountTransaction.setNote("Top-Up Deposit");
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            try {
                accountTransactionService.save(accountTransaction);
            } catch (Exception e) {
                return spotyResponseImpl.error(e);
            }
        }
        return spotyResponseImpl.ok();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            accountRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            accountRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public Account getByContains(Tenant tenant, String search) {
        return accountRepo.findByTenantAndAccountNameContainingIgnoreCase(tenant, search);
    }
}
