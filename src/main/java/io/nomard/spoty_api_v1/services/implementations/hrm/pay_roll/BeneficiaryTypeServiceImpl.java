package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.BeneficiaryTypeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.BeneficiaryTypeService;
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
public class BeneficiaryTypeServiceImpl implements BeneficiaryTypeService {
    @Autowired
    private BeneficiaryTypeRepository beneficiaryTypeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<BeneficiaryType>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> beneficiaryTypeRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(beneficiaryTypeRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<BeneficiaryType> getById(Long id) {
        return beneficiaryTypeRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<BeneficiaryType> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> beneficiaryTypeRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(BeneficiaryType beneficiaryType) {
        return authService.authUser()
                .flatMap(user -> {
                    beneficiaryType.setTenant(user.getTenant());
                    beneficiaryType.setCreatedBy(user);
                    beneficiaryType.setCreatedAt(new Date());
                    return beneficiaryTypeRepo.save(beneficiaryType)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(BeneficiaryType data) {
        return beneficiaryTypeRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Beneficiary type not found")))
                .flatMap(beneficiaryType -> {
                    boolean updated = false;

                    if (!Objects.equals(data.getBranches(), beneficiaryType.getBranches())) {
                        beneficiaryType.setBranches(data.getBranches());
                        updated = true;
                    }

                    if (!Objects.equals(data.getName(), beneficiaryType.getName())) {
                        beneficiaryType.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
                        beneficiaryType.setColor(data.getColor());
                        updated = true;
                    }

                    if (!Objects.equals(data.getDescription(), beneficiaryType.getDescription())) {
                        beneficiaryType.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    beneficiaryType.setUpdatedBy(user);
                                    beneficiaryType.setUpdatedAt(new Date());
                                    return beneficiaryTypeRepo.save(beneficiaryType)
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
        return beneficiaryTypeRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return beneficiaryTypeRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
