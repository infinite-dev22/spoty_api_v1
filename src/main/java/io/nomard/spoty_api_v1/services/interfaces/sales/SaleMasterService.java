package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface SaleMasterService {
    List<SaleMaster> getAll();

    SaleMaster getById(Long id) throws NotFoundException;

    List<SaleMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(SaleMaster saleMaster);

    ResponseEntity<ObjectNode> update(SaleMaster saleMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
