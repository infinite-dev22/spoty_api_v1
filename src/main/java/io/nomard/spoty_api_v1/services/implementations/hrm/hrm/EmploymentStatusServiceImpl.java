package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.EmploymentStatusRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.hrm.EmploymentStatusService;
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
public class EmploymentStatusServiceImpl implements EmploymentStatusService {
    @Autowired
    private EmploymentStatusRepository employmentStatusRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<EmploymentStatus>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> employmentStatusRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(employmentStatusRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<EmploymentStatus> getById(Long id) {
        return employmentStatusRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<EmploymentStatus> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> employmentStatusRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(EmploymentStatus employmentStatus) {
        return authService.authUser()
                .flatMap(user -> {
                    employmentStatus.setTenant(user.getTenant());
                    employmentStatus.setCreatedBy(user);
                    employmentStatus.setCreatedAt(new Date());
                    return employmentStatusRepo.save(employmentStatus)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(EmploymentStatus data) {
        return employmentStatusRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Employment status not found")))
                .flatMap(employmentStatus -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        employmentStatus.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
                        employmentStatus.setColor(data.getColor());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        employmentStatus.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    employmentStatus.setUpdatedBy(user);
                                    employmentStatus.setUpdatedAt(new Date());
                                    return employmentStatusRepo.save(employmentStatus)
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
        return employmentStatusRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return employmentStatusRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
