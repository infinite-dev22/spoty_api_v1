package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CurrencyRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CurrencyService;
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
public class CurrencyServiceImpl implements CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Currency>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> currencyRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(currencyRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Currency> getById(Long id) {
        return currencyRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Currency> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> currencyRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Currency currency) {
        return authService.authUser()
                .flatMap(user -> {
                    currency.setTenant(user.getTenant());
                    currency.setCreatedBy(user);
                    currency.setCreatedAt(new Date());
                    return currencyRepo.save(currency)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Currency data) {
        return currencyRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Currency not found")))
                .flatMap(currency -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        currency.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getCode()) && !"".equalsIgnoreCase(data.getCode())) {
                        currency.setCode(data.getCode());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getSymbol()) && !"".equalsIgnoreCase(data.getSymbol())) {
                        currency.setSymbol(data.getSymbol());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    currency.setUpdatedBy(user);
                                    currency.setUpdatedAt(new Date());
                                    return currencyRepo.save(currency)
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
        return currencyRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return currencyRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
