package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.UnitOfMeasureDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface UnitOfMeasureService {
    Page<UnitOfMeasureDTO.AsWholeDTO> getAll(int pageNo, int pageSize);

    UnitOfMeasureDTO.AsWholeDTO getById(Long id) throws NotFoundException;

    List<UnitOfMeasureDTO.AsWholeDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(UnitOfMeasure uom);

    ResponseEntity<ObjectNode> update(UnitOfMeasure uom) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
