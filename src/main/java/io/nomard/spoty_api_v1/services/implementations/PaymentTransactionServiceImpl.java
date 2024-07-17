package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flutterwave.bean.Response;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.payments.CardModel;
import io.nomard.spoty_api_v1.models.payments.MoMoModel;
import io.nomard.spoty_api_v1.payments.FlutterWavePayments;
import io.nomard.spoty_api_v1.repositories.PaymentTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private FlutterWavePayments flutterWavePayments;
    @Autowired
    private PaymentTransaction paymentTransaction;

    @Override
    public Flux<PageImpl<PaymentTransaction>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> paymentTransactionRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(paymentTransactionRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<PaymentTransaction> getById(Long id) {
        return paymentTransactionRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> payCard(CardModel payload) {
        return authService.authUser()
                .flatMap(user -> {
                    flutterWavePayments.initialize();
                    if (payload.isRecurring()) {
                        flutterWavePayments.tokenizeCard(payload, "flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
                    }
                    // flutterWavePayments.preAuth(payload);
                    flutterWavePayments.cardPayment(payload);

                    paymentTransaction.setTenant(user.getTenant());
                    paymentTransaction.setBranch(user.getBranch());
                    paymentTransaction.setTransactionReference("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
                    paymentTransaction.setPlanName(payload.getPlanName());
                    paymentTransaction.setPaidOn(new Date());
                    paymentTransaction.setAmount(payload.getAmount());
                    paymentTransaction.setRecurring(payload.isRecurring());
                    paymentTransaction.setPayMethod("CARD");
                    paymentTransaction.setPaySource(payload.getCard());
                    paymentTransaction.setTenant(user.getTenant());
                    paymentTransaction.setCreatedBy(user);
                    paymentTransaction.setCreatedAt(new Date());
                    return paymentTransactionRepo.save(paymentTransaction)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> initiateMomoPayment(MoMoModel momoModel) {
        return authService.authUser()
                .flatMap(user -> {
                    flutterWavePayments.initialize();
                    Response response = flutterWavePayments.initiateMomoPayment(momoModel.getPayload());
                    response.getCode();
                    response.getData();
                    response.getMeta();
                    response.getMessage();
                    response.getError_id();
                    if (Objects.equals(response.getStatus(), "success")) {
                        paymentTransaction.setTenant(user.getTenant());
                        paymentTransaction.setBranch(user.getBranch());
                        paymentTransaction.setTransactionReference("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
                        paymentTransaction.setPlanName(momoModel.getPlanName());
                        paymentTransaction.setPaidOn(new Date());
                        paymentTransaction.setAmount(momoModel.getPayload().getAmount());
                        paymentTransaction.setPayMethod("MOBILE MONEY");
                        paymentTransaction.setPaySource(momoModel.getPayload().getPhoneNumber());
                        paymentTransaction.setTenant(user.getTenant());
                        paymentTransaction.setCreatedBy(user);
                        paymentTransaction.setCreatedAt(new Date());
                        return paymentTransactionRepo.save(paymentTransaction)
                                .thenReturn(spotyResponseImpl.created());
                    } else {
                        return Mono.just(spotyResponseImpl.custom(HttpStatus.BAD_REQUEST, "Could not initiate mobile money payment."));
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.custom(HttpStatus.BAD_REQUEST, "Could not initiate mobile money payment.")));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return paymentTransactionRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return paymentTransactionRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
