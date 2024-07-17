package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.PaySlipTypeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.PaySlipTypeService;
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
public class PaySlipTypeServiceImpl implements PaySlipTypeService {
    @Autowired
    private PaySlipTypeRepository paySlipTypeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<PaySlipType>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> paySlipTypeRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(paySlipTypeRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<PaySlipType> getById(Long id) {
        return paySlipTypeRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(PaySlipType paySlipType) {
        return authService.authUser()
                .flatMap(user -> {
                    paySlipType.setTenant(user.getTenant());
                    paySlipType.setCreatedBy(user);
                    paySlipType.setCreatedAt(new Date());
                    return paySlipTypeRepo.save(paySlipType)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(PaySlipType data) {
        return paySlipTypeRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(paySlipType -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getBranches()) && !data.getBranches().isEmpty()) {
                        paySlipType.setBranches(data.getBranches());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        paySlipType.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        paySlipType.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
                        paySlipType.setColor(data.getColor());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    paySlipType.setUpdatedBy(user);
                                    paySlipType.setUpdatedAt(new Date());
                                    return paySlipTypeRepo.save(paySlipType)
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
        return paySlipTypeRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return paySlipTypeRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
