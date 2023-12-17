package io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface SaleReturnMasterService {
    List<SaleReturnMaster> getAll();

    SaleReturnMaster getById(Long id) throws NotFoundException;

    List<SaleReturnMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(SaleReturnMaster saleMaster);

    ResponseEntity<ObjectNode> update(SaleReturnMaster saleMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
