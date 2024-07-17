package io.nomard.spoty_api_v1.services.implementations.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.leave.LeaveTypeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.leave.LeaveTypeService;
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
public class LeaveTypeServiceImpl implements LeaveTypeService {
    @Autowired
    private LeaveTypeRepository leaveTypeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<LeaveType>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> leaveTypeRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(leaveTypeRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<LeaveType> getById(Long id) {
        return leaveTypeRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(LeaveType leaveType) {
        return authService.authUser()
                .flatMap(user -> {
                    leaveType.setTenant(user.getTenant());
                    leaveType.setCreatedBy(user);
                    leaveType.setCreatedAt(new Date());
                    return leaveTypeRepo.save(leaveType)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(LeaveType data) {
        return leaveTypeRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(leaveType -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getBranches()) && !data.getBranches().isEmpty()) {
                        leaveType.setBranches(data.getBranches());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        leaveType.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        leaveType.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
                        leaveType.setColor(data.getColor());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    leaveType.setUpdatedBy(user);
                                    leaveType.setUpdatedAt(new Date());
                                    return leaveTypeRepo.save(leaveType)
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
        return leaveTypeRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return leaveTypeRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
