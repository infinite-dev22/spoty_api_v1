package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.TenantRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.interfaces.TenantService;
import io.nomard.spoty_api_v1.utils.CoreUtils;
import lombok.extern.java.Log;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;

@Service
@Log
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantRepository tenantRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<Tenant> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return tenantRepo.findAll(pageRequest);
    }

    @Override
    public Tenant getById(Long id) throws NotFoundException {
        return tenantRepo.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found"));
    }

    @Override
    public LocalDateTime getSubscriptionEndDate(Long id) throws NotFoundException {
        Tenant tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        Hibernate.initialize(tenant.getSubscriptionEndDate());
        return tenant.getSubscriptionEndDate();
    }

    @Override
    public boolean isTrial(Long id) throws NotFoundException {
        return tenantRepo.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found")).isTrial();
    }

    @Override
    public boolean canTry(Long id) throws NotFoundException {
        return tenantRepo.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found")).isCanTry();
    }

    @Override
    public boolean isNewTenancy(Long id) throws NotFoundException {
        return tenantRepo.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found")).isNewTenancy();
    }

    @Override
    public boolean isInGracePeriod(Long id) throws NotFoundException {
        LocalDateTime subscriptionEndDateTime = getSubscriptionEndDate(id);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodEnd = subscriptionEndDateTime.plusDays(getGracePeriodDays());
        return now.isBefore(gracePeriodEnd);
    }

    public int getGracePeriodDays() {
        return 7;
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Tenant tenant) {
        try {
            tenant.setCreatedAt(LocalDateTime.now());
            tenantRepo.save(tenant);
            return spotyResponseImpl.created();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> startTrial(Long tenantId) throws NotFoundException {
        Tenant tenant = tenantRepo.findById(tenantId).orElseThrow(() -> new NotFoundException("Tenant not found"));
        tenant.setTrial(true);
        tenant.setSubscriptionEndDate(CoreUtils.DateCalculations.addMonths(1));
        tenant.setCanTry(false);
        tenant.setUpdatedAt(LocalDateTime.now());
        try {
            tenantRepo.save(tenant);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Tenant data) throws NotFoundException {
        Tenant tenant = tenantRepo.findById(data.getId()).orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (!Objects.equals(tenant.getName(), data.getName()) && Objects.nonNull(data.getName()) && !data.getName().isEmpty()) {
            tenant.setName(data.getName());
        }

        if (!Objects.equals(tenant.isTrial(), data.isTrial())) {
            tenant.setTrial(data.isTrial());
        }

        if (!Objects.equals(tenant.isNewTenancy(), data.isNewTenancy())) {
            tenant.setNewTenancy(data.isNewTenancy());
        }

        tenant.setUpdatedAt(LocalDateTime.now());

        try {
            tenantRepo.save(tenant);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            tenantRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
