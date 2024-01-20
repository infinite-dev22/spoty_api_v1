package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.StockReport;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StockReportService {
    List<StockReport> getAll(int pageNo, int pageSize);

    StockReport getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(StockReport stockReport);

    ResponseEntity<ObjectNode> update(StockReport stockReport) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
