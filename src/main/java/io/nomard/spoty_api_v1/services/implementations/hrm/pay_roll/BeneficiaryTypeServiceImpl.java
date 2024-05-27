package io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.pay_roll.BeneficiaryTypeRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll.BeneficiaryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BeneficiaryTypeServiceImpl implements BeneficiaryTypeService {
    @Autowired
    private BeneficiaryTypeRepository beneficiaryTypeRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<BeneficiaryType> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<BeneficiaryType> page = beneficiaryTypeRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public BeneficiaryType getById(Long id) throws NotFoundException {
        Optional<BeneficiaryType> beneficiaryType = beneficiaryTypeRepo.findById(id);
        if (beneficiaryType.isEmpty()) {
            throw new NotFoundException();
        }
        return beneficiaryType.get();
    }

    @Override
    public ArrayList<BeneficiaryType> getByContains(String search) {
        return beneficiaryTypeRepo.searchAllByNameContainingIgnoreCaseOrColorContainingIgnoreCase(
                search,
                search
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(BeneficiaryType beneficiaryType) {
        try {
            beneficiaryType.setTenant(authService.authUser().getTenant());
            beneficiaryType.setCreatedBy(authService.authUser());
            beneficiaryType.setCreatedAt(new Date());
            beneficiaryTypeRepo.saveAndFlush(beneficiaryType);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(BeneficiaryType data) throws NotFoundException {
        var opt = beneficiaryTypeRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var beneficiaryType = opt.get();

        if (!Objects.equals(data.getBranches(), beneficiaryType.getBranches())) {
            beneficiaryType.setBranches(data.getBranches());
        }

        if (!Objects.equals(data.getName(), beneficiaryType.getName())) {
            beneficiaryType.setName(data.getName());
        }

        if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
            beneficiaryType.setColor(data.getColor());
        }

        if (!Objects.equals(data.getDescription(), beneficiaryType.getDescription())) {
            beneficiaryType.setDescription(data.getDescription());
        }

        beneficiaryType.setUpdatedBy(authService.authUser());
        beneficiaryType.setUpdatedAt(new Date());

        try {
            beneficiaryTypeRepo.saveAndFlush(beneficiaryType);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            beneficiaryTypeRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            beneficiaryTypeRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
