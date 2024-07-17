package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.UnitOfMeasureRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.UnitOfMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {
    @Autowired
    private UnitOfMeasureRepository uomRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<UnitOfMeasure>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> uomRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(uomRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<UnitOfMeasure> getById(Long id) {
        return uomRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<UnitOfMeasure> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> uomRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(UnitOfMeasure uom) {
        return authService.authUser()
                .flatMap(user -> {
                    uom.setTenant(user.getTenant());
                    uom.setCreatedBy(user);
                    uom.setCreatedAt(new Date());
                    return uomRepo.save(uom)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(UnitOfMeasure data) {
        return uomRepo.findById(data.getId())
                .flatMap(unitOfMeasure -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        unitOfMeasure.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getShortName()) && !"".equalsIgnoreCase(data.getShortName())) {
                        unitOfMeasure.setShortName(data.getShortName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getOperator()) && !"".equalsIgnoreCase(data.getOperator())) {
                        unitOfMeasure.setOperator(data.getOperator());
                        updated = true;
                    }

                    if (!Objects.equals(data.getOperatorValue(), 0)) {
                        unitOfMeasure.setOperatorValue(data.getOperatorValue());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getBaseUnit())) {
                        unitOfMeasure.setBaseUnit(data.getBaseUnit());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    unitOfMeasure.setUpdatedBy(user);
                                    unitOfMeasure.setUpdatedAt(new Date());
                                    return uomRepo.save(unitOfMeasure)
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
        return uomRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return uomRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
