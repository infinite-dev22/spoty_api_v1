package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.PermissionRepository;
import io.nomard.spoty_api_v1.repositories.RoleRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.RoleService;
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
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PermissionRepository permissionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Role>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> roleRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(roleRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Role> getById(Long id) {
        return roleRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Role> search(String search) {
        return authService.authUser()
                .flatMapMany(user -> roleRepo.search(search.toLowerCase()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Role role) {
        return authService.authUser()
                .flatMap(user -> {
                    // Fetch permissions based on IDs
                    return permissionRepo.findAllById(role.getPermissions()
                                    .stream()
                                    .map(Permission::getId)
                                    .collect(Collectors.toList()))
                            .collectList()
                            .flatMap(permissions -> {
                                role.setPermissions(permissions); // Set the fetched permissions
                                role.setTenant(user.getTenant());
                                role.setCreatedBy(user);
                                role.setCreatedAt(new Date());
                                return roleRepo.save(role);
                            })
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Role data) {
        return roleRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Role not found")))
                .flatMap(role -> {
                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        role.setName(data.getName());
                    }

                    if (Objects.nonNull(data.getLabel()) && !"".equalsIgnoreCase(data.getLabel())) {
                        role.setLabel(data.getLabel());
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        role.setDescription(data.getDescription());
                    }
                    return permissionRepo.findAllById(role.getPermissions()
                                    .stream()
                                    .map(Permission::getId)
                                    .collect(Collectors.toList()))
                            .collectList()
                            .flatMap(permissions -> {

                                if (Objects.nonNull(data.getPermissions())
                                        && !data.getPermissions().isEmpty()
                                        && !Objects.deepEquals(role.getPermissions(), data.getPermissions())) {
                                    role.setPermissions(permissions);
                                }
                                return authService.authUser()
                                        .flatMap(user -> {
                                            role.setUpdatedBy(user);
                                            role.setUpdatedAt(new Date());
                                            return roleRepo.save(role)
                                                    .thenReturn(spotyResponseImpl.ok());
                                        });
                            });
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return roleRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return roleRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
