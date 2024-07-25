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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public List<PaymentTransaction> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<PaymentTransaction> page = paymentTransactionRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        return page.getContent();
    }

    @Override
    public PaymentTransaction getById(Long id) throws NotFoundException {
        Optional<PaymentTransaction> paymentTransaction = paymentTransactionRepo.findById(id);
        if (paymentTransaction.isEmpty()) {
            throw new NotFoundException();
        }
        return paymentTransaction.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> payCard(CardModel payload) {
        try {
            flutterWavePayments.initialize();
            if (payload.isRecurring()) {
                flutterWavePayments.tokenizeCard(payload, "flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
            }
//            flutterWavePayments.preAuth(payload);
            flutterWavePayments.cardPayment(payload);

            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setBranch(authService.authUser().getBranch());
            paymentTransaction.setTransactionReference("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
            paymentTransaction.setPlanName(payload.getPlanName());
            paymentTransaction.setPaidOn(LocalDateTime.now());
            paymentTransaction.setAmount(payload.getAmount());
            paymentTransaction.setRecurring(payload.isRecurring());
            paymentTransaction.setPayMethod("CARD");
            paymentTransaction.setPaySource(payload.getCard());
            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setCreatedBy(authService.authUser());
            paymentTransaction.setCreatedAt(LocalDateTime.now());
            paymentTransactionRepo.saveAndFlush(paymentTransaction);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> initiateMomoPayment(MoMoModel momoModel) {
        flutterWavePayments.initialize();
        Response response = flutterWavePayments.initiateMomoPayment(momoModel.getPayload());
        response.getCode();
        response.getData();
        response.getMeta();
        response.getMessage();
        response.getError_id();
        if (Objects.equals(response.getStatus(), "success")) {
            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setBranch(authService.authUser().getBranch());
            paymentTransaction.setTransactionReference("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
            paymentTransaction.setPlanName(momoModel.getPlanName());
            paymentTransaction.setPaidOn(LocalDateTime.now());
            paymentTransaction.setAmount(momoModel.getPayload().getAmount());
            paymentTransaction.setPayMethod("MOBILE MONEY");
            paymentTransaction.setPaySource(momoModel.getPayload().getPhoneNumber());
            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setCreatedBy(authService.authUser());
            paymentTransaction.setCreatedAt(LocalDateTime.now());
            paymentTransactionRepo.saveAndFlush(paymentTransaction);
            return spotyResponseImpl.created();
        }
        return spotyResponseImpl.custom(HttpStatus.BAD_REQUEST, "Could not initiate mobile money payment.");
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            paymentTransactionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            paymentTransactionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
