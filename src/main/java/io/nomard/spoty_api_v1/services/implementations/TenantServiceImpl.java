package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.TenantRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.interfaces.TenantService;
import io.nomard.spoty_api_v1.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantRepository tenantRepo;
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
        Optional<Tenant> tenant = tenantRepo.findById(id);
        if (tenant.isEmpty()) {
            throw new NotFoundException();
        }
        return tenant.get();
    }

    @Override
    public Date getSubscriptionEndDate(Long id) throws NotFoundException {
        Optional<Tenant> tenant = tenantRepo.findById(id);
        if (tenant.isEmpty()) {
            throw new NotFoundException();
        }
        return tenant.get().getSubscriptionEndDate();
    }

    @Override
    public Date getTrialEndDate(Long id) throws NotFoundException {
        Optional<Tenant> tenant = tenantRepo.findById(id);
        if (tenant.isEmpty()) {
            throw new NotFoundException();
        }
        return tenant.get().getTrialEndDate();
    }

    @Override
    public boolean isTrial(Long id) throws NotFoundException {
        Optional<Tenant> tenant = tenantRepo.findById(id);
        if (tenant.isEmpty()) {
            throw new NotFoundException();
        }
        return tenant.get().isTrial();
    }

    @Override
    public boolean canTry(Long id) throws NotFoundException {
        Optional<Tenant> tenant = tenantRepo.findById(id);
        if (tenant.isEmpty()) {
            throw new NotFoundException();
        }
        return tenant.get().isCanTry();
    }

    @Override
    public boolean isNewTenancy(Long id) throws NotFoundException {
        Optional<Tenant> tenant = tenantRepo.findById(id);
        if (tenant.isEmpty()) {
            throw new NotFoundException();
        }
        return tenant.get().isNewTenancy();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Tenant tenant) {
        try {
            tenant.setCreatedAt(new Date());
            tenantRepo.saveAndFlush(tenant);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> startTrial(Long tenantId) throws NotFoundException {
        var opt = tenantRepo.findById(tenantId);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var tenant = opt.get();
        tenant.setTrial(true);
        tenant.setTrialEndDate(DateUtils.addDays(7));
        tenant.setSubscriptionEndDate(DateUtils.addDays(7));
        tenant.setCanTry(false);
        tenant.setUpdatedAt(new Date());
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
        var opt = tenantRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var tenant = opt.get();

        if (!Objects.equals(tenant.getName(), data.getName()) && Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
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

        tenant.setUpdatedAt(new Date());

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
