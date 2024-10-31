package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.PaymentTransactionDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.payments.CardModel;
import io.nomard.spoty_api_v1.models.payments.MoMoModel;
import io.nomard.spoty_api_v1.services.implementations.PaymentTransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payment/transactions")
public class PaymentTransactionController {
    @Autowired
    private PaymentTransactionServiceImpl paymentTransactionService;

    @GetMapping("/all")
    public Page<PaymentTransactionDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return paymentTransactionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PaymentTransactionDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return paymentTransactionService.getById(findModel.getId());
    }

    @PostMapping("/pay/card")
    public ResponseEntity<ObjectNode> payCard(@Valid @RequestBody CardModel cardModel) {
        return paymentTransactionService.payCard(cardModel);
    }

    @PostMapping("/pay/momo/initiate")
    public ResponseEntity<ObjectNode> initiateMomo(@Valid @RequestBody MoMoModel momoModel) {
        return paymentTransactionService.initiateMomoPayment(momoModel);
    }
}
