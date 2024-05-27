package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.CardModel;
import io.nomard.spoty_api_v1.models.MoMoModel;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface PaymentTransactionService {
    List<PaymentTransaction> getAll(int pageNo, int pageSize);

    PaymentTransaction getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> payCard(CardModel cardModel);

    ResponseEntity<ObjectNode> payMoMo(MoMoModel momoModel);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
