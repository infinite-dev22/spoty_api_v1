package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.SupplierRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Supplier>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> supplierRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(supplierRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Supplier> getById(Long id) {
        return supplierRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Supplier> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> supplierRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Supplier supplier) {
        return authService.authUser()
                .flatMap(user -> {
                    supplier.setTenant(user.getTenant());
                    if (Objects.isNull(supplier.getBranch())) {
                        supplier.setBranch(user.getBranch());
                    }
                    supplier.setCreatedBy(user);
                    supplier.setCreatedAt(new Date());
                    return supplierRepo.save(supplier)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Supplier data) {
        return supplierRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Supplier not found")))
                .flatMap(supplier -> {
                    boolean updated = false;
                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        supplier.setName(data.getName());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
                        supplier.setEmail(data.getEmail());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
                        supplier.setCity(data.getCity());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
                        supplier.setPhone(data.getPhone());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getAddress()) && !"".equalsIgnoreCase(data.getAddress())) {
                        supplier.setAddress(data.getAddress());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getCountry()) && !"".equalsIgnoreCase(data.getCountry())) {
                        supplier.setCountry(data.getCountry());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getCode()) && !"".equalsIgnoreCase(data.getCode())) {
                        supplier.setCode(data.getCode());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getTaxNumber()) && !"".equalsIgnoreCase(data.getTaxNumber())) {
                        supplier.setTaxNumber(data.getTaxNumber());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    supplier.setUpdatedBy(user);
                                    supplier.setUpdatedAt(new Date());
                                    return supplierRepo.save(supplier)
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
        return supplierRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return supplierRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
