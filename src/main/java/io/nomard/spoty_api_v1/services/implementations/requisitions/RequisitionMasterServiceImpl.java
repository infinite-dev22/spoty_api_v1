package io.nomard.spoty_api_v1.services.implementations.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.requisitions.RequisitionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.requisitions.RequisitionMasterService;
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
public class RequisitionMasterServiceImpl implements RequisitionMasterService {
    @Autowired
    private RequisitionRepository requisitionMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<RequisitionMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> requisitionMasterRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(requisitionMasterRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<RequisitionMaster> getById(Long id) {
        return requisitionMasterRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<RequisitionMaster> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> requisitionMasterRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(RequisitionMaster requisitionMaster) {
        return authService.authUser()
                .flatMap(user -> {
                    if (!requisitionMaster.getRequisitionDetails().isEmpty()) {
                        for (int i = 0; i < requisitionMaster.getRequisitionDetails().size(); i++) {
                            requisitionMaster.getRequisitionDetails().get(i).setRequisition(requisitionMaster);
                        }
                    }
                    requisitionMaster.setTenant(user.getTenant());
                    if (Objects.isNull(requisitionMaster.getBranch())) {
                        requisitionMaster.setBranch(user.getBranch());
                    }
                    requisitionMaster.setCreatedBy(user);
                    requisitionMaster.setCreatedAt(new Date());
                    return requisitionMasterRepo.save(requisitionMaster)
                            .then(Mono.just(spotyResponseImpl.created()))
                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(RequisitionMaster data) {
        return requisitionMasterRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Requisition in  not found")))
                .flatMap(requisitionMaster -> {
                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        requisitionMaster.setRef(data.getRef());
                    }

                    if (Objects.nonNull(data.getDate()) && !Objects.equals(data.getDate(), requisitionMaster.getDate())) {
                        requisitionMaster.setDate(data.getDate());
                    }

                    if (Objects.nonNull(data.getSupplier()) && !Objects.equals(data.getSupplier(), requisitionMaster.getSupplier())) {
                        requisitionMaster.setSupplier(data.getSupplier());
                    }

                    if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), requisitionMaster.getBranch())) {
                        requisitionMaster.setBranch(data.getBranch());
                    }

                    if (Objects.nonNull(data.getRequisitionDetails()) && !data.getRequisitionDetails().isEmpty()) {
                        requisitionMaster.setRequisitionDetails(data.getRequisitionDetails());

                        for (int i = 0; i < requisitionMaster.getRequisitionDetails().size(); i++) {
                            requisitionMaster.getRequisitionDetails().get(i).setRequisition(requisitionMaster);
                        }
                    }

                    if (Objects.nonNull(data.getShipVia()) && !"".equalsIgnoreCase(data.getShipVia())) {
                        requisitionMaster.setShipVia(data.getShipVia());
                    }

                    if (Objects.nonNull(data.getShipMethod()) && !"".equalsIgnoreCase(data.getShipMethod())) {
                        requisitionMaster.setShipMethod(data.getShipMethod());
                    }

                    if (Objects.nonNull(data.getShippingTerms()) && !"".equalsIgnoreCase(data.getShippingTerms())) {
                        requisitionMaster.setShippingTerms(data.getShippingTerms());
                    }

                    if (Objects.nonNull(data.getDeliveryDate()) && !Objects.equals(data.getDeliveryDate(), requisitionMaster.getDeliveryDate())) {
                        requisitionMaster.setDeliveryDate(data.getDeliveryDate());
                    }

                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                        requisitionMaster.setNotes(data.getNotes());
                    }

                    if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
                        requisitionMaster.setStatus(data.getStatus());
                    }

                    if (!Objects.equals(data.getTotalCost(), requisitionMaster.getTotalCost())) {
                        requisitionMaster.setTotalCost(data.getTotalCost());
                    }

                    return authService.authUser()
                            .flatMap(user -> {
                                requisitionMaster.setUpdatedBy(user);
                                requisitionMaster.setUpdatedAt(new Date());
                                return requisitionMasterRepo.save(requisitionMaster)
                                        .then(Mono.just(spotyResponseImpl.ok()));
                            })
                            .then(Mono.just(spotyResponseImpl.ok()));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return requisitionMasterRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return requisitionMasterRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
