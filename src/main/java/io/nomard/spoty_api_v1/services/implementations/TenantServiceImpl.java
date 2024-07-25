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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public List<Tenant> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Tenant> page = tenantRepo.findAll(pageRequest);
        return page.getContent();
    }

    @Override
    public Tenant getById(Long id) throws NotFoundException {
        return tenantRepo.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public LocalDateTime getSubscriptionEndDate(Long id) throws NotFoundException {
        Tenant tenant = tenantRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        Hibernate.initialize(tenant.getSubscriptionEndDate());
        return tenant.getSubscriptionEndDate();
    }

    @Override
    public LocalDateTime getTrialEndDate(Long id) throws NotFoundException {
        return userRepo.findById(id).orElseThrow(NotFoundException::new).getTenant().getTrialEndDate();
    }

    @Override
    public boolean isTrial(Long id) throws NotFoundException {
        return userRepo.findById(id).orElseThrow(NotFoundException::new).getTenant().isTrial();
    }

    @Override
    public boolean canTry(Long id) throws NotFoundException {
        return userRepo.findById(id).orElseThrow(NotFoundException::new).getTenant().isCanTry();
    }

    @Override
    public boolean isNewTenancy(Long id) throws NotFoundException {
        return userRepo.findById(id).orElseThrow(NotFoundException::new).getTenant().isNewTenancy();
    }

    @Override
    public boolean isInGracePeriod(Long id) throws NotFoundException {
        LocalDateTime subscriptionEndDateTime = getSubscriptionEndDate(id);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodEnd = subscriptionEndDateTime.plusDays(getGracePeriodDays());
        return now.isBefore(gracePeriodEnd);
    }

    private int getGracePeriodDays() {
        return 7; // Adjust this value as needed
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Tenant tenant) {
        try {
            tenant.setCreatedAt(LocalDateTime.now());
            tenantRepo.saveAndFlush(tenant);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> startTrial(Long tenantId) throws NotFoundException {
        Tenant tenant = tenantRepo.findById(tenantId).orElseThrow(NotFoundException::new);
        tenant.setTrial(true);
        tenant.setTrialEndDate(DateUtils.addDays(7));
        tenant.setSubscriptionEndDate(DateUtils.addDays(7));
        tenant.setCanTry(false);
        tenant.setUpdatedAt(LocalDateTime.now());
        try {
            tenantRepo.saveAndFlush(tenant);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Tenant data) throws NotFoundException {
        Tenant tenant = tenantRepo.findById(data.getId()).orElseThrow(NotFoundException::new);

        if (!Objects.equals(tenant.getName(), data.getName()) && Objects.nonNull(data.getName()) && !data.getName().isEmpty()) {
            tenant.setName(data.getName());
        }

        if (!Objects.equals(tenant.isTrial(), data.isTrial())) {
            tenant.setTrial(data.isTrial());
        }

        if (!Objects.equals(tenant.getTrialEndDate(), data.getTrialEndDate()) && Objects.nonNull(data.getTrialEndDate())) {
            tenant.setTrialEndDate(data.getTrialEndDate());
        }

        if (!Objects.equals(tenant.isNewTenancy(), data.isNewTenancy())) {
            tenant.setNewTenancy(data.isNewTenancy());
        }

        tenant.setUpdatedAt(LocalDateTime.now());

        try {
            tenantRepo.saveAndFlush(tenant);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            tenantRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
