package io.nomard.spoty_api_v1.services.implementations.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.stock_ins.StockInRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.stock_ins.StockInMasterService;
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
public class StockInMasterServiceImpl implements StockInMasterService {
    @Autowired
    private StockInRepository stockInRepo;
    @Autowired
    private StockInTransactionServiceImpl stockInTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<StockInMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> stockInRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(stockInRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<StockInMaster> getById(Long id) {
        return stockInRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<StockInMaster> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> stockInRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(StockInMaster stockInMaster) {
        return authService.authUser()
                .flatMap(user -> {
                    stockInMaster.setTenant(user.getTenant());
                    if (Objects.isNull(stockInMaster.getBranch())) {
                        stockInMaster.setBranch(user.getBranch());
                    }
                    stockInMaster.setCreatedBy(user);
                    stockInMaster.setCreatedAt(new Date());

                    if (!stockInMaster.getStockInDetails().isEmpty()) {
                        stockInMaster.getStockInDetails().forEach(detail -> detail.setStockIn(stockInMaster));
                    }

                    return stockInRepo.save(stockInMaster)
                            .flatMapMany(savedMaster -> {
                                if (!savedMaster.getStockInDetails().isEmpty()) {
                                    return Flux.fromIterable(savedMaster.getStockInDetails())
                                            .flatMap(detail -> stockInTransactionService.save(detail));
                                }
                                return Flux.empty();
                            })
                            .then(Mono.just(spotyResponseImpl.created()))
                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(StockInMaster data) {
        return stockInRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Stock in  not found")))
                .flatMap(stockInMaster -> {
                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        stockInMaster.setRef(data.getRef());
                    }

                    if (!Objects.equals(stockInMaster.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
                        stockInMaster.setBranch(data.getBranch());
                    }

                    if (Objects.nonNull(data.getStockInDetails()) && !data.getStockInDetails().isEmpty()) {
                        stockInMaster.setStockInDetails(data.getStockInDetails());
                        return Flux.fromIterable(stockInMaster.getStockInDetails())
                                .flatMap(detail -> {
                                    detail.setStockIn(stockInMaster);
                                    return stockInTransactionService.update(detail)
                                            .onErrorResume(NotFoundException.class, e -> Mono.error(new RuntimeException(e)));
                                })
                                .then(Mono.just(stockInMaster));
                    }

                    return Mono.just(stockInMaster);
                })
                .flatMap(stockInMaster -> {
                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                        stockInMaster.setNotes(data.getNotes());
                    }
                    return authService.authUser()
                            .flatMap(user -> {
                                stockInMaster.setUpdatedBy(user);
                                stockInMaster.setUpdatedAt(new Date());

                                return stockInRepo.save(stockInMaster)
                                        .then(Mono.just(spotyResponseImpl.ok()));
                            })
                            .then(Mono.just(spotyResponseImpl.ok()));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return stockInRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return stockInRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
