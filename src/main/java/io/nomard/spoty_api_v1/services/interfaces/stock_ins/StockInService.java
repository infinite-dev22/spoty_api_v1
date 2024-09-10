package io.nomard.spoty_api_v1.services.interfaces.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StockInService {
    Page<StockInMaster> getAll(int pageNo, int pageSize);

    StockInMaster getById(Long id) throws NotFoundException;

    List<StockInMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(StockInMaster stockInMaster);

    ResponseEntity<ObjectNode> update(StockInMaster stockInMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
