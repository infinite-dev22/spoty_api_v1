package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BranchRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.BranchService;
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
public class BranchServiceImpl implements BranchService {
    @Autowired
    private BranchRepository branchRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Branch>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> branchRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(branchRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Branch> getById(Long id) {
        return branchRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Branch> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> branchRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Branch branch) {
        return authService.authUser()
                .flatMap(user -> {
                    branch.setTenant(user.getTenant());
                    branch.setCreatedBy(user);
                    branch.setCreatedAt(new Date());
                    return branchRepo.save(branch)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Branch data) {
        return branchRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(branch -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        branch.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
                        branch.setCity(data.getCity());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
                        branch.setPhone(data.getPhone());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
                        branch.setEmail(data.getEmail());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getTown()) && !"".equalsIgnoreCase(data.getTown())) {
                        branch.setTown(data.getTown());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getZipCode()) && !"".equalsIgnoreCase(data.getZipCode())) {
                        branch.setZipCode(data.getZipCode());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    branch.setUpdatedBy(user);
                                    branch.setUpdatedAt(new Date());
                                    return branchRepo.save(branch)
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
        return branchRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return branchRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
