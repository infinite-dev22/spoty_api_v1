package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
import io.nomard.spoty_api_v1.repositories.accounting.AccountTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AccountTransactionServiceImpl implements AccountTransactionService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private AccountTransactionRepository accountTransactionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<AccountTransaction> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return accountTransactionRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(AccountTransaction accountTransaction) {
        try {
            accountTransaction.setTenant(authService.authUser().getTenant());
            var opt = accountRepo.findById(accountTransaction.getAccount().getId());
            if (opt.isEmpty()) {
                throw new NotFoundException();
            }
            var account = opt.get();

            if (Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "deposit")
                    || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "sale")
                    || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "purchase returns")) {
                accountTransaction.setCredit(accountTransaction.getAmount());
                account.setCredit(account.getCredit() + accountTransaction.getAmount());
                account.setBalance(account.getBalance() + accountTransaction.getAmount());
            }
            if (Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "expense")
                    || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "payroll")
                    || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "sale returns")
                    || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "purchase")) {
                accountTransaction.setDebit(accountTransaction.getAmount());
                account.setDebit(account.getDebit() + accountTransaction.getAmount());
                account.setBalance(account.getBalance() - accountTransaction.getAmount());
            }
            account.setUpdatedAt(LocalDateTime.now());
            account.setUpdatedBy(authService.authUser());
            accountRepo.save(account);
            accountTransaction.setCreatedBy(authService.authUser());
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransactionRepo.save(accountTransaction);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
