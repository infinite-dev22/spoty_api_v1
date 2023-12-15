package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CurrencyService {
    List<Currency> getAll();

    Currency getById(Long id) throws NotFoundException;

    List<Currency> getByContains(String search);

    ResponseEntity<ObjectNode> save(Currency currency);

    ResponseEntity<ObjectNode> update(Currency currency) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
