package io.nomard.spoty_api_v1.services.implementations.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.leave.LeaveRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.leave.LeaveService;
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
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRepository leaveRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Leave>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> leaveRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(leaveRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Leave> getById(Long id) {
        return leaveRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Leave> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> leaveRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Leave leave) {
        return authService.authUser()
                .flatMap(user -> {
                    leave.setTenant(user.getTenant());
                    leave.setBranch(user.getBranch());
                    leave.setCreatedBy(user);
                    leave.setCreatedAt(new Date());
                    return leaveRepo.save(leave)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Leave data) {
        return leaveRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Leave not found")))
                .flatMap(leave -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getEmployee())) {
                        leave.setEmployee(data.getEmployee());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        leave.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        leave.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getStartDate())) {
                        leave.setStartDate(data.getStartDate());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getEndDate())) {
                        leave.setEndDate(data.getEndDate());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDuration())) {
                        leave.setDuration(data.getDuration());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getLeaveType())) {
                        leave.setLeaveType(data.getLeaveType());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getAttachment()) && !"".equalsIgnoreCase(data.getAttachment())) {
                        leave.setAttachment(data.getAttachment());
                        updated = true;
                    }

                    if (data.getStatus() != '\0') {
                        leave.setStatus(data.getStatus());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    leave.setUpdatedBy(user);
                                    leave.setUpdatedAt(new Date());
                                    return leaveRepo.save(leave)
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
        return leaveRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return leaveRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
