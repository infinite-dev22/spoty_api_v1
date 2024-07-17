package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaleDetailService {
    List<SaleDetail> getAll(int pageNo, int pageSize);

    SaleDetail getById(Long id);

    ResponseEntity<ObjectNode> save(SaleDetail saleDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<SaleDetail> saleDetailList);

    ResponseEntity<ObjectNode> update(SaleDetail saleDetail);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
