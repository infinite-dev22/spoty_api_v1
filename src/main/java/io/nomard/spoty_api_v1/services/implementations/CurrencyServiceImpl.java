package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CurrencyRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CurrencyServiceImpl implements CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Currency> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Currency> page = currencyRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Currency getById(Long id) throws NotFoundException {
        Optional<Currency> currency = currencyRepo.findById(id);
        if (currency.isEmpty()) {
            throw new NotFoundException();
        }
        return currency.get();
    }

    @Override
    public List<Currency> getByContains(String search) {
        return currencyRepo.searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrSymbolContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Currency currency) {
        try {
            currency.setTenant(authService.authUser().getTenant());
            currency.setCreatedBy(authService.authUser());
            currency.setCreatedAt(new Date());
            currencyRepo.saveAndFlush(currency);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Currency data) throws NotFoundException {
        var opt = currencyRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var currency = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            currency.setName(data.getName());
        }

        if (Objects.nonNull(data.getCode()) && !"".equalsIgnoreCase(data.getCode())) {
            currency.setCode(data.getCode());
        }

        if (Objects.nonNull(data.getSymbol()) && !"".equalsIgnoreCase(data.getSymbol())) {
            currency.setSymbol(data.getSymbol());
        }

        currency.setUpdatedBy(authService.authUser());
        currency.setUpdatedAt(new Date());

        try {
            currencyRepo.saveAndFlush(currency);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            currencyRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            currencyRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
