package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.PermissionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.PermissionService;
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
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Permission>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> permissionRepo.findAll()
                        .collectList()
                        .zipWith(permissionRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Permission> getById(Long id) {
        return permissionRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Permission permission) {
        return authService.authUser()
                .flatMap(user -> {
                    permission.setCreatedBy(user);
                    permission.setCreatedAt(new Date());
                    return permissionRepo.save(permission)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Permission data) {
        return permissionRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Permission not found")))
                .flatMap(permission -> {
                    boolean updated = false;
                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        permission.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getLabel()) && !"".equalsIgnoreCase(data.getLabel())) {
                        permission.setLabel(data.getLabel());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        permission.setDescription(data.getDescription());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    permission.setUpdatedBy(user);
                                    permission.setUpdatedAt(new Date());
                                    return permissionRepo.save(permission)
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
        return permissionRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return permissionRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
