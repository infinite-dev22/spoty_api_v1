package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ServiceInvoice;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServiceInvoiceService {
    List<ServiceInvoice> getAll();

    ServiceInvoice getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(ServiceInvoice serviceInvoice);

    ResponseEntity<ObjectNode> update(ServiceInvoice serviceInvoice) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
