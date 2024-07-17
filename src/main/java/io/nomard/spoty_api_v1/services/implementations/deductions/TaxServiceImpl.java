package io.nomard.spoty_api_v1.services.implementations.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.deductions.TaxRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.deductions.TaxService;
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
public class TaxServiceImpl implements TaxService {
    @Autowired
    private TaxRepository taxRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Tax>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> taxRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(taxRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Tax> getById(Long id) {
        return taxRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Tax tax) {
        return authService.authUser()
                .flatMap(user -> {
                    tax.setTenant(user.getTenant());
                    tax.setBranch(user.getBranch());
                    tax.setCreatedBy(user);
                    tax.setCreatedAt(new Date());
                    return taxRepo.save(tax)
                            .thenReturn(spotyResponseImpl.created());
                }).onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Tax data) {
        return taxRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Tax not found")))
                .flatMap(tax -> {
                    boolean updated = false;

                    if (!Objects.equals(tax.getName(), data.getName()) && Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        tax.setName(data.getName());
                        updated = true;
                    }
                    if (!Objects.equals(tax.getPercentage(), data.getPercentage())) {
                        tax.setPercentage(data.getPercentage());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    tax.setUpdatedBy(user);
                                    tax.setUpdatedAt(new Date());
                                    return taxRepo.save(tax)
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
        return taxRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return taxRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
