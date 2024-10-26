package io.nomard.spoty_api_v1.services.interfaces.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.TaxDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface TaxService {
    Page<TaxDTO> getAll(int pageNo, int pageSize);

    TaxDTO getById(Long id) throws NotFoundException;

    Tax getByIdInternal(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Tax tax);

    ResponseEntity<ObjectNode> update(Tax tax) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
