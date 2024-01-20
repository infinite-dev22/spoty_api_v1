package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ZenService;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ZenServiceService {
    List<ZenService> getAll(int pageNo, int pageSize);

    ZenService getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(ZenService service);

    ResponseEntity<ObjectNode> update(ZenService service) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
