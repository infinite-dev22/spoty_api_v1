package io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface SaleReturnDetailService {
    List<SaleReturnDetail> getAll(int pageNo, int pageSize);

    SaleReturnDetail getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(SaleReturnDetail saleReturnDetail);

    ResponseEntity<ObjectNode> saveMultiple(ArrayList<SaleReturnDetail> saleReturnDetailList);

    ResponseEntity<ObjectNode> update(SaleReturnDetail saleReturnDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
