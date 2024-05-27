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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public List<SaleTermAndCondition> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<SaleTermAndCondition> page = saleTermAndConditionRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
    public ResponseEntity<ObjectNode> save(SaleTermAndCondition saleTermAndCondition) {
        try {
            saleTermAndCondition.setTenant(authService.authUser().getTenant());
            saleTermAndCondition.setCreatedBy(authService.authUser());
            saleTermAndCondition.setCreatedAt(new Date());
            saleTermAndConditionRepo.saveAndFlush(saleTermAndCondition);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(SaleTermAndCondition data) throws NotFoundException {
        var opt = saleTermAndConditionRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var saleTermAndCondition = opt.get();

        if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), saleTermAndCondition.getBranch())) {
            saleTermAndCondition.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            saleTermAndCondition.setName(data.getName());
        }

        if (!Objects.equals(data.isActive(), saleTermAndCondition.isActive())) {
            saleTermAndCondition.setActive(data.isActive());
        }

        saleTermAndCondition.setUpdatedBy(authService.authUser());
        saleTermAndCondition.setUpdatedAt(new Date());

        try {
            saleTermAndConditionRepo.saveAndFlush(saleTermAndCondition);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
