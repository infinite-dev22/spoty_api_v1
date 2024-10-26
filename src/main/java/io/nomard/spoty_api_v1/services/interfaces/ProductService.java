package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ProductDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface ProductService {
    Page<ProductDTO> getAll(int pageNo, int pageSize);

    List<ProductDTO> getAllNonPaged();

    ProductDTO getById(Long id) throws NotFoundException;

    Product getByIdInternally(Long id) throws NotFoundException;

    List<ProductDTO> getByContains(String search);

    List<ProductDTO> getWarning();

    ResponseEntity<ObjectNode> save(Product product, MultipartFile file);

    ResponseEntity<ObjectNode> save(Product product);

    ResponseEntity<ObjectNode> update(Product product, MultipartFile file) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
