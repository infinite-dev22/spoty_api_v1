package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.PaySlipRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.PaySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PaySlipServiceImpl implements PaySlipService {
    @Autowired
    private PaySlipRepository paySlipRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<PaySlip>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> paySlipRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(paySlipRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<PaySlip> getById(Long id) {
        return paySlipRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(PaySlip paySlip) {
        return authService.authUser()
                .flatMap(user -> {
                    paySlip.setTenant(user.getTenant());
                    paySlip.setBranch(user.getBranch());
                    paySlip.setCreatedBy(user);
                    paySlip.setCreatedAt(new Date());
                    return paySlipRepo.save(paySlip)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(PaySlip data) {
        return paySlipRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(paySlip -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getPaySlipType())) {
                        paySlip.setPaySlipType(data.getPaySlipType());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getStartDate()) && !Objects.equals(data.getStartDate(), paySlip.getStartDate())) {
                        paySlip.setStartDate(data.getStartDate());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getEndDate()) && !Objects.equals(data.getEndDate(), paySlip.getEndDate())) {
                        paySlip.setEndDate(data.getEndDate());
                        updated = true;
                    }

                    if (!Objects.equals(data.getSalariesQuantity(), paySlip.getSalariesQuantity())) {
                        paySlip.setSalariesQuantity(data.getSalariesQuantity());
                        updated = true;
                    }

                    if (data.getStatus() != '\0') {
                        paySlip.setStatus(data.getStatus());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getMessage()) && !"".equalsIgnoreCase(data.getMessage())) {
                        paySlip.setMessage(data.getMessage());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    paySlip.setUpdatedBy(user);
                                    paySlip.setUpdatedAt(new Date());
                                    return paySlipRepo.save(paySlip)
                                            .thenReturn(spotyResponseImpl.ok());
                                });
                    } else {
                        return Mono.just(spotyResponseImpl.ok());
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return paySlipRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return paySlipRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
