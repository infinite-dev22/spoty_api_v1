package io.nomard.spoty_api_v1.services.implementations.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.transfers.TransferRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.transfers.TransferMasterService;
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
public class TransferMasterServiceImpl implements TransferMasterService {
    @Autowired
    private TransferRepository transferRepo;
    @Autowired
    private TransferTransactionServiceImpl transferTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public Flux<PageImpl<TransferMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> transferRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(transferRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public Mono<TransferMaster> getById(Long id) {
        return transferRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public Flux<UnitOfMeasure> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> transferRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(TransferMaster transfer) {
        return authService.authUser()
                .flatMap(user -> {
                    transfer.getTransferDetails().forEach(detail -> detail.setTransfer(transfer));
                    transfer.setTenant(user.getTenant());
                    transfer.setCreatedBy(user);
                    transfer.setCreatedAt(new Date());

                    return transferRepo.save(transfer)
                            .flatMap(savedMaster -> {
                                Flux<TransferDetail> saveDetailsFlux = Flux.fromIterable(transfer.getTransferDetails())
                                        .flatMap(detail -> transferTransactionService.save(detail).then(Mono.just(detail)));

                                return saveDetailsFlux.collectList()
                                        .then(Mono.just(savedMaster));
                            });
                })
                .map(savedMaster -> spotyResponseImpl.created())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @CacheEvict(value = "transfer_masters", key = "#data.id")
    public Mono<ResponseEntity<ObjectNode>> update(TransferMaster data) {
        return transferRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(transfer -> {
                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        transfer.setRef(data.getRef());
                    }

                    if (!Objects.equals(transfer.getDate(), data.getDate()) && Objects.nonNull(data.getDate())) {
                        transfer.setDate(data.getDate());
                    }

                    if (!Objects.equals(transfer.getFromBranch(), data.getFromBranch()) && Objects.nonNull(data.getFromBranch())) {
                        transfer.setFromBranch(data.getFromBranch());
                    }

                    if (!Objects.equals(transfer.getToBranch(), data.getToBranch()) && Objects.nonNull(data.getToBranch())) {
                        transfer.setToBranch(data.getToBranch());
                    }

                    if (Objects.nonNull(data.getTransferDetails()) && !data.getTransferDetails().isEmpty()) {
                        transfer.setTransferDetails(data.getTransferDetails());
                        return Flux.fromIterable(transfer.getTransferDetails())
                                .flatMap(detail -> {
                                    detail.setTransfer(transfer);
                                    return transferTransactionService.update(detail)
                                            .onErrorMap(NotFoundException.class, RuntimeException::new);
                                })
                                .then(Mono.just(transfer));
                    }

                    return Mono.just(transfer);
                })
                .flatMap(transfer -> {
                    if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
                        transfer.setShipping(data.getShipping());
                    }

                    if (!Objects.equals(data.getTotal(), transfer.getTotal())) {
                        transfer.setTotal(data.getTotal());
                    }

                    if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
                        transfer.setStatus(data.getStatus());
                    }

                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                        transfer.setNotes(data.getNotes());
                    }

                    return authService.authUser()
                            .flatMap(user -> {
                                transfer.setUpdatedBy(user);
                                transfer.setUpdatedAt(new Date());

                                return transferRepo.save(transfer);
                            });
                })
                .map(savedMaster -> spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return transferRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return transferRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
