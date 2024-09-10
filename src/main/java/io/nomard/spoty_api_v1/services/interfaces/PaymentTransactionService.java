package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.payments.CardModel;
import io.nomard.spoty_api_v1.models.payments.MoMoModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface PaymentTransactionService {
    Page<PaymentTransaction> getAll(int pageNo, int pageSize);

    PaymentTransaction getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> payCard(CardModel cardModel);

    ResponseEntity<ObjectNode> initiateMomoPayment(MoMoModel momoModel);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
