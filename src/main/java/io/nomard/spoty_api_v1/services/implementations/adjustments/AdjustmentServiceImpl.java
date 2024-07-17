package io.nomard.spoty_api_v1.services.implementations.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.adjustments.AdjustmentRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.adjustments.AdjustmentMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class AdjustmentServiceImpl implements AdjustmentMasterService {
    @Autowired
    private AdjustmentRepository adjustmentRepo;
    @Autowired
    private AdjustmentTransactionServiceImpl adjustmentTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public Flux<PageImpl<AdjustmentMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> adjustmentRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(adjustmentRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public Mono<AdjustmentMaster> getById(Long id) {
        return adjustmentRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Cacheable("adjustment_masters")
    @Transactional(readOnly = true)
    public Flux<AdjustmentMaster> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> adjustmentRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(AdjustmentMaster adjustmentMaster) {
        return Mono.defer(() -> {
                    // Set adjustment for each detail
                    adjustmentMaster.getAdjustmentDetails().forEach(detail -> detail.setAdjustment(adjustmentMaster));

                    // Set tenant, branch, createdBy, createdAt
                    return authService.authUser()
                            .flatMap(user -> {
                                adjustmentMaster.setTenant(user.getTenant());
                                if (Objects.isNull(adjustmentMaster.getBranch())) {
                                    adjustmentMaster.setBranch(user.getBranch());
                                }
                                adjustmentMaster.setCreatedBy(user);
                                adjustmentMaster.setCreatedAt(new Date());

                                // Save adjustmentMaster
                                return adjustmentRepo.save(adjustmentMaster)
                                        .flatMap(savedMaster -> {
                                            // Save each adjustment detail
                                            return Flux.fromIterable(savedMaster.getAdjustmentDetails())
                                                    .flatMap(adjustmentTransactionService::save)
                                                    .then(Mono.just(spotyResponseImpl.created()));
                                        });
                            })
                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @CacheEvict(value = "adjustment_masters", key = "#data.id")
    public Mono<ResponseEntity<ObjectNode>> update(AdjustmentMaster data) {
        return adjustmentRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(adjustmentMaster -> {
                    // Update branch if present
                    if (Objects.nonNull(data.getBranch())) {
                        adjustmentMaster.setBranch(data.getBranch());
                    }

                    // Update adjustment details if present
                    if (Objects.nonNull(data.getAdjustmentDetails()) && !data.getAdjustmentDetails().isEmpty()) {
                        adjustmentMaster.setAdjustmentDetails(data.getAdjustmentDetails());

                        return Flux.fromIterable(adjustmentMaster.getAdjustmentDetails())
                                .flatMap(detail -> {
                                    detail.setAdjustment(adjustmentMaster);
                                    return Mono.just(detail);
                                })
                                .then(authService.authUser().flatMap(user -> {
                                    adjustmentMaster.setUpdatedBy(user);
                                    adjustmentMaster.setUpdatedAt(new Date());

                                    // Update other fields
                                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                                        adjustmentMaster.setRef(data.getRef());
                                    }

                                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                                        adjustmentMaster.setNotes(data.getNotes());
                                    }

                                    return adjustmentRepo.save(adjustmentMaster)
                                            .then(Mono.just(spotyResponseImpl.ok()))
                                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                                }));
                    } else {
                        return authService.authUser().flatMap(user -> {
                            adjustmentMaster.setUpdatedBy(user);
                            adjustmentMaster.setUpdatedAt(new Date());

                            // Update other fields
                            if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                                adjustmentMaster.setRef(data.getRef());
                            }

                            if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                                adjustmentMaster.setNotes(data.getNotes());
                            }

                            return adjustmentRepo.save(adjustmentMaster)
                                    .then(Mono.just(spotyResponseImpl.ok()))
                                    .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                        });
                    }
                });
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return adjustmentRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return adjustmentRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
