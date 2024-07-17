package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ProductRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private DocumentServiceImpl documentService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public Flux<PageImpl<Product>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> productRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(productRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public Mono<Product> getById(Long id) {
        return productRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public Flux<Product> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> productRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public Flux<Product> getWarning() {
        return authService.authUser()
                .flatMapMany(user -> productRepo.stockAlert(user.getTenant().getId()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Product product, MultipartFile file) {
        return authService.authUser()
                .flatMap(user -> {
                    var fileURL = "";

                    product.setTenant(user.getTenant());
                    if (Objects.nonNull(file)) {
                        fileURL = String.valueOf(documentService.save(file));
                        product.setImage(fileURL);
                    }

                    if (Objects.isNull(product.getBranch())) {
                        product.setBranch(user.getBranch());
                    }
                    product.setCreatedBy(user);
                    product.setCreatedAt(new Date());
                    return productRepo.save(product)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @CacheEvict(value = "products", key = "#data.id")
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Product data, MultipartFile file) {
        return productRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> {
                    boolean updated = false;
                    var fileURL = "";

                    if (Objects.nonNull(data.getUnit())) {
                        product.setUnit(data.getUnit());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getCategory())) {
                        product.setCategory(data.getCategory());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getBrand())) {
                        product.setBrand(data.getBrand());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getBranch())) {
                        product.setBranch(data.getBranch());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getBarcodeType()) && !"".equalsIgnoreCase(data.getBarcodeType())) {
                        product.setBarcodeType(data.getBarcodeType());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        product.setName(data.getName());
                        updated = true;
                    }

                    if (!Objects.equals(data.getQuantity(), 0)) {
                        product.setQuantity(data.getQuantity());
                        updated = true;
                    }

                    if (!Objects.equals(data.getCostPrice(), 0)) {
                        product.setCostPrice(data.getCostPrice());
                        updated = true;
                    }

                    if (!Objects.equals(data.getSalePrice(), 0)) {
                        product.setSalePrice(data.getSalePrice());
                        updated = true;
                    }

                    if (!Objects.equals(product.getDiscount(), data.getDiscount()) && Objects.nonNull(data.getDiscount())) {
                        product.setDiscount(data.getDiscount());
                        updated = true;
                    }

                    if (!Objects.equals(product.getTax(), data.getTax()) && Objects.nonNull(data.getTax())) {
                        product.setTax(data.getTax());
                        updated = true;
                    }

                    if (!Objects.equals(data.getStockAlert(), 0)) {
                        product.setStockAlert(data.getStockAlert());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
                        product.setSerialNumber(data.getSerialNumber());
                        updated = true;
                    }
                    if (Objects.nonNull(file)) {
                        try {
                            fileURL = String.valueOf(documentService.save(file));
                        } catch (Exception e) {
                            return Mono.just(spotyResponseImpl.error(e));
                        }
                        product.setImage(fileURL);
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    product.setUpdatedBy(user);
                                    product.setUpdatedAt(new Date());
                                    return productRepo.save(product)
                                            .thenReturn(spotyResponseImpl.ok());
                                });
                    } else {
                        return Mono.just(spotyResponseImpl.ok());
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return productRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return productRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
