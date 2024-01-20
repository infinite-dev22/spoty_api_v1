package io.nomard.spoty_api_v1.services.interfaces.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface StockInDetailService {
    List<StockInDetail> getAll(int pageNo, int pageSize);

    StockInDetail getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(StockInDetail stockInDetail);

    ResponseEntity<ObjectNode> saveMultiple(ArrayList<StockInDetail> stockInDetailList);

    ResponseEntity<ObjectNode> update(StockInDetail stockInDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
