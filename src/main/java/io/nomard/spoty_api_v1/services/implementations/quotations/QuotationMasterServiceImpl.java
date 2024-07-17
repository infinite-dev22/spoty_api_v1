package io.nomard.spoty_api_v1.services.implementations.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.quotations.QuotationRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.quotations.QuotationMasterService;
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
public class QuotationMasterServiceImpl implements QuotationMasterService {
    @Autowired
    private QuotationRepository quotationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<QuotationMaster>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> quotationRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(quotationRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<QuotationMaster> getById(Long id) {
        return quotationRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<QuotationMaster> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> quotationRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(QuotationMaster quotationMaster) {
        return authService.authUser()
                .flatMap(user -> {
                    var total = 0.00;
                    var tax = 0.00;
                    var netTax = 0.00;
                    var discount = 0.00;
                    var netDiscount = 0.00;
                    if (!quotationMaster.getQuotationDetails().isEmpty()) {
                        for (int i = 0; i < quotationMaster.getQuotationDetails().size(); i++) {
                            var quotationDetail = quotationMaster.getQuotationDetails().get(i);
                            quotationDetail.setQuotation(quotationMaster);
                            total += quotationDetail.getSubTotal();
                            if (Objects.nonNull(quotationDetail.getTax()) && quotationDetail.getTax().getPercentage() > 0.0) {
                                tax += quotationDetail.getTax().getPercentage();
                            }
                            if (Objects.nonNull(quotationDetail.getDiscount()) && quotationDetail.getDiscount().getPercentage() > 0.0) {
                                discount += quotationDetail.getDiscount().getPercentage();
                            }
                        }
                        netTax += tax / quotationMaster.getQuotationDetails().size();
                        netDiscount += discount / quotationMaster.getQuotationDetails().size();
                    }
                    quotationMaster.setNetTax(netTax);
                    quotationMaster.setNetDiscount(netDiscount);
                    quotationMaster.setTotal(total);
                    quotationMaster.setTenant(user.getTenant());
                    if (Objects.isNull(quotationMaster.getBranch())) {
                        quotationMaster.setBranch(user.getBranch());
                    }
                    quotationMaster.setCreatedBy(user);
                    quotationMaster.setCreatedAt(new Date());
                    return quotationRepo.save(quotationMaster)
                            .then(Mono.just(spotyResponseImpl.created()))
                            .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(QuotationMaster data) {
        return quotationRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Quotation in  not found")))
                .flatMap(quotationMaster -> {
                    var total = 0.00;
                    var tax = 0.00;
                    var netTax = 0.00;
                    var discount = 0.00;
                    var netDiscount = 0.00;

                    if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        quotationMaster.setRef(data.getRef());
                    }

                    if (Objects.nonNull(data.getCustomer()) && !Objects.equals(data.getCustomer(), quotationMaster.getCustomer())) {
                        quotationMaster.setCustomer(data.getCustomer());
                    }

                    if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), quotationMaster.getBranch())) {
                        quotationMaster.setBranch(data.getBranch());
                    }

                    if (Objects.nonNull(data.getQuotationDetails()) && !data.getQuotationDetails().isEmpty()) {
                        quotationMaster.setQuotationDetails(data.getQuotationDetails());

                        for (int i = 0; i < data.getQuotationDetails().size(); i++) {
                            var quotationDetail = data.getQuotationDetails().get(i);
                            if (Objects.isNull(quotationDetail.getQuotation())) {
                                quotationDetail.setQuotation(quotationMaster);
                            }
                            total += quotationDetail.getSubTotal();
                            if (Objects.nonNull(quotationDetail.getTax()) && quotationDetail.getTax().getPercentage() > 0.0) {
                                tax += quotationDetail.getTax().getPercentage();
                            }
                            if (Objects.nonNull(quotationDetail.getDiscount()) && quotationDetail.getDiscount().getPercentage() > 0.0) {
                                discount += quotationDetail.getDiscount().getPercentage();
                            }
                        }
                        netTax += tax / data.getQuotationDetails().size();
                        netDiscount += discount / data.getQuotationDetails().size();
                    }

                    if (!Objects.equals(data.getNetTax(), quotationMaster.getNetTax())) {
                        quotationMaster.setNetTax(netTax);
                    }

                    if (!Objects.equals(data.getDiscount(), quotationMaster.getDiscount())) {
                        quotationMaster.setNetDiscount(netDiscount);
                    }

                    if (!Objects.equals(data.getTotal(), quotationMaster.getTotal())) {
                        quotationMaster.setTotal(total);
                    }

                    if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
                        quotationMaster.setStatus(data.getStatus());
                    }

                    if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
                        quotationMaster.setNotes(data.getNotes());
                    }
                    return authService.authUser()
                            .flatMap(user -> {
                                quotationMaster.setUpdatedBy(user);
                                quotationMaster.setUpdatedAt(new Date());
                                return quotationRepo.save(quotationMaster)
                                        .then(Mono.just(spotyResponseImpl.ok()));
                            })
                            .then(Mono.just(spotyResponseImpl.ok()));
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return quotationRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList) {
        return quotationRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
