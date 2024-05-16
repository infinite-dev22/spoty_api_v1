package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface BrandService {
    List<Brand> getAll(int pageNo, int pageSize);

    Brand getById(Long id) throws NotFoundException;

    List<Brand> getByContains(String search);

    ResponseEntity<ObjectNode> save(Brand brand);

    ResponseEntity<ObjectNode> update(Brand data) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
