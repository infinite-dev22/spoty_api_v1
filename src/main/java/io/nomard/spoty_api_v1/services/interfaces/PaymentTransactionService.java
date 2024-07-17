package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.payments.CardModel;
import io.nomard.spoty_api_v1.models.payments.MoMoModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface PaymentTransactionService {
    Flux<PageImpl<PaymentTransaction>> getAll(int pageNo, int pageSize);

    Mono<PaymentTransaction> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> payCard(CardModel cardModel);

    Mono<ResponseEntity<ObjectNode>> initiateMomoPayment(MoMoModel momoModel);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
