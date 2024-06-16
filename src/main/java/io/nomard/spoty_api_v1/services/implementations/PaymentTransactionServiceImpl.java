package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.CardModel;
import io.nomard.spoty_api_v1.models.MoMoModel;
import io.nomard.spoty_api_v1.payments.Payments;
import io.nomard.spoty_api_v1.repositories.PaymentTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private Payments payments;
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
//            payments.initialize();
//            if (payload.isRecurring()) {
//                payments.tokenizeCard(payload, "flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
//            }
////            payments.preAuth(payload);
//            payments.cardPayment(payload);

            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setBranch(authService.authUser().getBranch());
            paymentTransaction.setTransactionReference("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
            paymentTransaction.setPlanName(payload.getPlanName());
            paymentTransaction.setPaidOn(new Date());
            paymentTransaction.setAmount(payload.getAmount());
            paymentTransaction.setRecurring(payload.isRecurring());
            paymentTransaction.setPayMethod("CARD");
            paymentTransaction.setPaySource(payload.getCard());
            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setCreatedBy(authService.authUser());
            paymentTransaction.setCreatedAt(new Date());
            paymentTransactionRepo.saveAndFlush(paymentTransaction);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> payMoMo(MoMoModel momoModel) {
        try {
//            payments.initialize();
//            payments.momoPay("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k", momoModel);

            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setBranch(authService.authUser().getBranch());
            paymentTransaction.setTransactionReference("flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k");
            paymentTransaction.setPlanName(momoModel.getPlanName());
            paymentTransaction.setPaidOn(new Date());
            paymentTransaction.setAmount(momoModel.getAmount());
            paymentTransaction.setPayMethod("MOBILE MONEY");
            paymentTransaction.setPaySource(momoModel.getPhoneNumber());
            paymentTransaction.setTenant(authService.authUser().getTenant());
            paymentTransaction.setCreatedBy(authService.authUser());
            paymentTransaction.setCreatedAt(new Date());
            paymentTransactionRepo.saveAndFlush(paymentTransaction);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
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
