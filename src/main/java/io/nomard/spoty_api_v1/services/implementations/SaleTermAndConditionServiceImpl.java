package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.SaleTermAndConditionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.SaleTermAndConditionService;
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
public class SaleTermAndConditionServiceImpl implements SaleTermAndConditionService {
    @Autowired
    private SaleTermAndConditionRepository saleTermAndConditionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<SaleTermAndCondition>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> saleTermAndConditionRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(saleTermAndConditionRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<SaleTermAndCondition> getById(Long id) {
        return saleTermAndConditionRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(SaleTermAndCondition saleTermAndCondition) {
        return authService.authUser()
                .flatMap(user -> {
                    saleTermAndCondition.setTenant(user.getTenant());
                    saleTermAndCondition.setCreatedBy(user);
                    saleTermAndCondition.setCreatedAt(new Date());
                    return saleTermAndConditionRepo.save(saleTermAndCondition)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(SaleTermAndCondition data) {
        return saleTermAndConditionRepo.findById(data.getId())
                .flatMap(saleTermAndCondition -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        saleTermAndCondition.setName(data.getName());
                        updated = true;
                    }
                    if (!Objects.equals(data.isActive(), saleTermAndCondition.isActive())) {
                        saleTermAndCondition.setActive(data.isActive());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    saleTermAndCondition.setUpdatedBy(user);
                                    saleTermAndCondition.setUpdatedAt(new Date());
                                    return saleTermAndConditionRepo.save(saleTermAndCondition)
                                            .thenReturn(spotyResponseImpl.ok());
                                });
                    } else {
                        return Mono.just(spotyResponseImpl.ok());
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return saleTermAndConditionRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return saleTermAndConditionRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
