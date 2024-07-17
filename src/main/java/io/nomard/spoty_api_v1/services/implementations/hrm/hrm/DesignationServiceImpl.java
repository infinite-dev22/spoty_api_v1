package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.DesignationRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.DesignationService;
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
public class DesignationServiceImpl implements DesignationService {
    @Autowired
    private DesignationRepository designationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Designation>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> designationRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(designationRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Designation> getById(Long id) {
        return designationRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Designation> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> designationRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Designation designation) {
        return authService.authUser()
                .flatMap(user -> {
                    designation.setTenant(user.getTenant());
                    designation.setBranch(user.getBranch());
                    designation.setCreatedBy(user);
                    designation.setCreatedAt(new Date());
                    return designationRepo.save(designation)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Designation data) {
        return designationRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Designation not found")))
                .flatMap(designation -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getBranch())) {
                        designation.setBranch(data.getBranch());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        designation.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        designation.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    designation.setUpdatedBy(user);
                                    designation.setUpdatedAt(new Date());
                                    return designationRepo.save(designation)
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
        return designationRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return designationRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
