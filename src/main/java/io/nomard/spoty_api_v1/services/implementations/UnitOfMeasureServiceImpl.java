package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.UnitOfMeasureRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.UnitOfMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {
    @Autowired
    private UnitOfMeasureRepository uomRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<UnitOfMeasure> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return uomRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public UnitOfMeasure getById(Long id) throws NotFoundException {
        Optional<UnitOfMeasure> unitOfMeasure = uomRepo.findById(id);
        if (unitOfMeasure.isEmpty()) {
            throw new NotFoundException();
        }
        return unitOfMeasure.get();
    }

    @Override
    public ArrayList<UnitOfMeasure> getByContains(String search) {
        return uomRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(UnitOfMeasure uom) {
        uom.setTenant(authService.authUser().getTenant());
        uom.setCreatedBy(authService.authUser());
        uom.setCreatedAt(LocalDateTime.now());
        uomRepo.save(uom);
        try {
            uomRepo.save(uom);

            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(UnitOfMeasure data) throws NotFoundException {
        var opt = uomRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var uom = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            uom.setName(data.getName());
        }

        if (Objects.nonNull(data.getShortName()) && !"".equalsIgnoreCase(data.getShortName())) {
            uom.setShortName(data.getShortName());
        }

        if (Objects.nonNull(data.getOperator()) && !"".equalsIgnoreCase(data.getOperator())) {
            uom.setOperator(data.getOperator());
        }

        if (!Objects.equals(data.getOperatorValue(), 0)) {
            uom.setOperatorValue(data.getOperatorValue());
        }

        if (Objects.nonNull(data.getBaseUnit())) {
            uom.setBaseUnit(data.getBaseUnit());
        }

        uom.setUpdatedBy(authService.authUser());
        uom.setUpdatedAt(LocalDateTime.now());

        try {
            uomRepo.save(uom);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            uomRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            uomRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
