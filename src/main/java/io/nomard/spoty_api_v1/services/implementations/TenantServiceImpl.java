package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.TenantRepository;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.interfaces.TenantService;
import io.nomard.spoty_api_v1.utils.DateUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Service
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantRepository tenantRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<Tenant> getAll(int pageNo, int pageSize) {
        return tenantRepo.findAll();
    }

    @Override
    public Mono<Tenant> getById(Long id) {
        return tenantRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Mono<Date> getSubscriptionEndDate(Long id) {
        return tenantRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Tenant not found"))).map(tenant -> {
                    Hibernate.initialize(tenant.getSubscriptionEndDate());
                    return tenant.getSubscriptionEndDate();
                });
    }


    @Override
    public Mono<Date> getTrialEndDate(Long id) {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> user.getTenant().getTrialEndDate());
    }

    @Override
    public Mono<Boolean> isTrial(Long id) {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> user.getTenant().isTrial());
    }

    @Override
    public Mono<Boolean> canTry(Long id) {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> user.getTenant().isCanTry());
    }

    @Override
    public Mono<Boolean> isNewTenancy(Long id) {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> user.getTenant().isNewTenancy());
    }

    @Override
    public Mono<Boolean> isInGracePeriod(Long id) {
        return getSubscriptionEndDate(id)
                .map(subscriptionEndDate -> {
                    LocalDateTime subscriptionEndDateTime = LocalDateTime.ofInstant(subscriptionEndDate.toInstant(), ZoneId.systemDefault());
                    LocalDateTime gracePeriodEnd = subscriptionEndDateTime.plusDays(getGracePeriodDays());
                    return LocalDateTime.now().isBefore(gracePeriodEnd);
                });
    }

    private int getGracePeriodDays() {
        return 7;
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Tenant tenant) {
        tenant.setCreatedAt(new Date());
        return tenantRepo.save(tenant)
                .map(savedTenant -> spotyResponseImpl.created())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> startTrial(Long tenantId) {
        return tenantRepo.findById(tenantId)
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(tenant -> {
                    tenant.setTrial(true);
                    tenant.setTrialEndDate(DateUtils.addDays(7));
                    tenant.setSubscriptionEndDate(DateUtils.addDays(7));
                    tenant.setCanTry(false);
                    tenant.setUpdatedAt(new Date());
                    return tenantRepo.save(tenant)
                            .thenReturn(spotyResponseImpl.ok());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Tenant data) {
        return tenantRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(existingTenant -> {
                    if (!Objects.equals(existingTenant.getName(), data.getName()) && Objects.nonNull(data.getName()) && !data.getName().isEmpty()) {
                        existingTenant.setName(data.getName());
                    }

                    if (!Objects.equals(existingTenant.isTrial(), data.isTrial())) {
                        existingTenant.setTrial(data.isTrial());
                    }

                    if (!Objects.equals(existingTenant.getTrialEndDate(), data.getTrialEndDate()) && Objects.nonNull(data.getTrialEndDate())) {
                        existingTenant.setTrialEndDate(data.getTrialEndDate());
                    }

                    if (!Objects.equals(existingTenant.isNewTenancy(), data.isNewTenancy())) {
                        existingTenant.setNewTenancy(data.isNewTenancy());
                    }

                    existingTenant.setUpdatedAt(new Date());

                    return tenantRepo.save(existingTenant)
                            .thenReturn(spotyResponseImpl.ok());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return tenantRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
