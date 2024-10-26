package io.nomard.spoty_api_v1.services.interfaces.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DiscountDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface DiscountService {
    Page<DiscountDTO> getAll(int pageNo, int pageSize);

    DiscountDTO getById(Long id) throws NotFoundException;

    Discount getByIdInternal(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Discount discount);

    ResponseEntity<ObjectNode> update(Discount discount) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
