package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.BeneficiaryBadgeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.BeneficiaryBadgeService;
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
public class BeneficiaryBadgeServiceImpl implements BeneficiaryBadgeService {
    @Autowired
    private BeneficiaryBadgeRepository beneficiaryBadgeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<BeneficiaryBadge>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> beneficiaryBadgeRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(beneficiaryBadgeRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<BeneficiaryBadge> getById(Long id) {
        return beneficiaryBadgeRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<BeneficiaryBadge> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> beneficiaryBadgeRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(BeneficiaryBadge beneficiaryBadge) {
        return authService.authUser()
                .flatMap(user -> {
                    beneficiaryBadge.setTenant(user.getTenant());
                    beneficiaryBadge.setCreatedBy(user);
                    beneficiaryBadge.setCreatedAt(new Date());
                    return beneficiaryBadgeRepo.save(beneficiaryBadge)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(BeneficiaryBadge data) {
        return beneficiaryBadgeRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Beneficiary badge not found")))
                .flatMap(beneficiaryBadge -> {
                    boolean updated = false;

                    if (!Objects.equals(data.getBranches(), beneficiaryBadge.getBranches())) {
                        beneficiaryBadge.setBranches(data.getBranches());
                    }

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        beneficiaryBadge.setName(data.getName());
                    }

                    if (Objects.nonNull(data.getBeneficiaryType())) {
                        beneficiaryBadge.setBeneficiaryType(data.getBeneficiaryType());
                    }

                    if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
                        beneficiaryBadge.setColor(data.getColor());
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        beneficiaryBadge.setDescription(data.getDescription());
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    beneficiaryBadge.setUpdatedBy(user);
                                    beneficiaryBadge.setUpdatedAt(new Date());
                                    return beneficiaryBadgeRepo.save(beneficiaryBadge)
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
        return beneficiaryBadgeRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return beneficiaryBadgeRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
