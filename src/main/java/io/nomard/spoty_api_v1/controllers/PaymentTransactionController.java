package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.payments.CardModel;
import io.nomard.spoty_api_v1.models.payments.MoMoModel;
import io.nomard.spoty_api_v1.services.implementations.PaymentTransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("payment/transactions")
public class PaymentTransactionController {
    @Autowired
    private PaymentTransactionServiceImpl paymentTransactionService;

    @GetMapping("/all")
    public Flux<PageImpl<PaymentTransaction>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                     @RequestParam(defaultValue = "50") Integer pageSize) {
        return paymentTransactionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<PaymentTransaction> getById(@RequestBody FindModel findModel) {
        return paymentTransactionService.getById(findModel.getId());
    }

    @PostMapping("/pay/card")
    public Mono<ResponseEntity<ObjectNode>> payCard(@Valid @RequestBody CardModel cardModel) {
        return paymentTransactionService.payCard(cardModel);
    }

    @PostMapping("/pay/momo/initiate")
    public Mono<ResponseEntity<ObjectNode>> initiateMomo(@Valid @RequestBody MoMoModel momoModel) {
        return paymentTransactionService.initiateMomoPayment(momoModel);
    }
}
