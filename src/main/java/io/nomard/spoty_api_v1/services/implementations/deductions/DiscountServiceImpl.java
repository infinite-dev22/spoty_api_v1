package io.nomard.spoty_api_v1.services.implementations.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.deductions.DiscountRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.deductions.DiscountService;
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
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private DiscountRepository discountRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Discount>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> discountRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(discountRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Discount> getById(Long id) {
        return discountRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Discount discount) {
        return authService.authUser()
                .flatMap(user -> {
                    discount.setTenant(user.getTenant());
                    discount.setBranch(user.getBranch());
                    discount.setCreatedBy(user);
                    discount.setCreatedAt(new Date());
                    return discountRepo.save(discount)
                            .thenReturn(spotyResponseImpl.created());
                }).onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Discount data) {
        return discountRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Discount not found")))
                .flatMap(discount -> {
                    boolean updated = false;

                    if (!Objects.equals(discount.getName(), data.getName()) && Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        discount.setName(data.getName());
                        updated = true;
                    }
                    if (!Objects.equals(discount.getPercentage(), data.getPercentage())) {
                        discount.setPercentage(data.getPercentage());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    discount.setUpdatedBy(user);
                                    discount.setUpdatedAt(new Date());
                                    return discountRepo.save(discount)
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
        return discountRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return discountRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
