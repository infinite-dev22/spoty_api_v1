package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleTerm;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaleTermService {
    List<SaleTerm> getAll(int pageNo, int pageSize);

    SaleTerm getById(Long id) throws NotFoundException;

    List<SaleTerm> getByContains(String search);

    ResponseEntity<ObjectNode> save(SaleTerm bank);

    ResponseEntity<ObjectNode> update(SaleTerm bank) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
