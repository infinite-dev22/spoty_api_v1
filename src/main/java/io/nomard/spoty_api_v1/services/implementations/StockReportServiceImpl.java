package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.StockReport;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.StockReportRepository;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.StockReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockReportServiceImpl implements StockReportService {
    @Autowired
    private StockReportRepository stockReportRepo;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    public Flux<PageImpl<StockReport>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> stockReportRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(stockReportRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<StockReport> getById(Long id) {
        return stockReportRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }
}
