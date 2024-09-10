package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.SaleTermAndConditionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.SaleTermAndConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleTermAndConditionServiceImpl implements SaleTermAndConditionService {
    @Autowired
    private SaleTermAndConditionRepository saleTermAndConditionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<SaleTermAndCondition> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return saleTermAndConditionRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public SaleTermAndCondition getById(Long id) throws NotFoundException {
        Optional<SaleTermAndCondition> saleTermAndCondition = saleTermAndConditionRepo.findById(id);
        if (saleTermAndCondition.isEmpty()) {
            throw new NotFoundException();
        }
        return saleTermAndCondition.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(SaleTermAndCondition saleTermAndCondition) {
        try {
            saleTermAndCondition.setTenant(authService.authUser().getTenant());
            saleTermAndCondition.setCreatedBy(authService.authUser());
            saleTermAndCondition.setCreatedAt(LocalDateTime.now());
            saleTermAndConditionRepo.save(saleTermAndCondition);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(SaleTermAndCondition data) throws NotFoundException {
        var opt = saleTermAndConditionRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleTermAndCondition = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            saleTermAndCondition.setName(data.getName());
        }

        if (!Objects.equals(data.isActive(), saleTermAndCondition.isActive())) {
            saleTermAndCondition.setActive(data.isActive());
        }

        saleTermAndCondition.setUpdatedBy(authService.authUser());
        saleTermAndCondition.setUpdatedAt(LocalDateTime.now());

        try {
            saleTermAndConditionRepo.save(saleTermAndCondition);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            saleTermAndConditionRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            saleTermAndConditionRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
