package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.BeneficiaryBadgeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.BeneficiaryBadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BeneficiaryBadgeServiceImpl implements BeneficiaryBadgeService {
    @Autowired
    private BeneficiaryBadgeRepository beneficiaryBadgeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<BeneficiaryBadge> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        return beneficiaryBadgeRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public BeneficiaryBadge getById(Long id) throws NotFoundException {
        Optional<BeneficiaryBadge> beneficiaryBadge = beneficiaryBadgeRepo.findById(id);
        if (beneficiaryBadge.isEmpty()) {
            throw new NotFoundException();
        }
        return beneficiaryBadge.get();
    }

    @Override
    public List<BeneficiaryBadge> getByContains(String search) {
        return beneficiaryBadgeRepo.searchAllByNameContainingIgnoreCaseOrColorContainingIgnoreCase(search, search);
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(BeneficiaryBadge beneficiaryBadge) {
        try {
            beneficiaryBadge.setTenant(authService.authUser().getTenant());
            beneficiaryBadge.setCreatedBy(authService.authUser());
            beneficiaryBadge.setCreatedAt(LocalDateTime.now());
            beneficiaryBadgeRepo.saveAndFlush(beneficiaryBadge);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(BeneficiaryBadge data) throws NotFoundException {
        var opt = beneficiaryBadgeRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var beneficiaryBadge = opt.get();

        if (!Objects.equals(data.getBranches(), beneficiaryBadge.getBranches())) {
            beneficiaryBadge.setBranches(data.getBranches());
        }

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            beneficiaryBadge.setName(data.getName());
        }

        if (Objects.nonNull(data.getBeneficiaryType())) {
            beneficiaryBadge.setBeneficiaryType(data.getBeneficiaryType());
        }

        if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
            beneficiaryBadge.setColor(data.getColor());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            beneficiaryBadge.setDescription(data.getDescription());
        }

        beneficiaryBadge.setUpdatedBy(authService.authUser());
        beneficiaryBadge.setUpdatedAt(LocalDateTime.now());

        try {
            beneficiaryBadgeRepo.saveAndFlush(beneficiaryBadge);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            beneficiaryBadgeRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            beneficiaryBadgeRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
