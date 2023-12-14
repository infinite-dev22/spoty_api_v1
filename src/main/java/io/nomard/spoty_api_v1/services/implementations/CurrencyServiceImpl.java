package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CurrencyRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyServiceImpl implements CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Currency> getAll() {
        return currencyRepo.findAll();
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
        return currencyRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Currency currency) {
        try {
            currency.setCreatedBy(authService.authUser());
            currency.setCreatedAt(new Date());
            currencyRepo.saveAndFlush(currency);
            return spotyResponseImpl.created();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, Currency currency) {
        try {
            currency.setUpdatedBy(authService.authUser());
            currency.setUpdatedAt(new Date());
            currency.setId(id);
            currencyRepo.saveAndFlush(currency);
            return spotyResponseImpl.ok();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            currencyRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
