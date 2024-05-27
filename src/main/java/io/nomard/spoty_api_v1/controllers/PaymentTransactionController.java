package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.CardModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.MoMoModel;
import io.nomard.spoty_api_v1.services.implementations.PaymentTransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("payment/transactions")
public class PaymentTransactionController {
    @Autowired
    private PaymentTransactionServiceImpl paymentTransactionService;


    @GetMapping("/all")
    public List<PaymentTransaction> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "50") Integer pageSize) {
        return paymentTransactionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PaymentTransaction getById(@RequestBody FindModel findModel) throws NotFoundException {
        return paymentTransactionService.getById(findModel.getId());
    }

    @PostMapping("/pay/card")
    public ResponseEntity<ObjectNode> payCard(@Valid @RequestBody CardModel cardModel) {
        return paymentTransactionService.payCard(cardModel);
    }

    @PostMapping("/pay/momo")
    public ResponseEntity<ObjectNode> payMoMo(@Valid @RequestBody MoMoModel momoModel) {
        return paymentTransactionService.payMoMo(momoModel);
    }
}
