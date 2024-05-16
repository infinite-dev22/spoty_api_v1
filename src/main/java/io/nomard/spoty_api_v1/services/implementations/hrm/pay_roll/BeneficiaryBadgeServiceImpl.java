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

import java.util.*;

@Service
public class BeneficiaryBadgeServiceImpl implements BeneficiaryBadgeService {
    @Autowired
    private BeneficiaryBadgeRepository beneficiaryBadgeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<BeneficiaryBadge> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<BeneficiaryBadge> page = beneficiaryBadgeRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
    public ResponseEntity<ObjectNode> save(BeneficiaryBadge beneficiaryBadge) {
        try {
            beneficiaryBadge.setCreatedBy(authService.authUser());
            beneficiaryBadge.setCreatedAt(new Date());
            beneficiaryBadgeRepo.saveAndFlush(beneficiaryBadge);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
        beneficiaryBadge.setUpdatedAt(new Date());

        try {
            beneficiaryBadgeRepo.saveAndFlush(beneficiaryBadge);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
