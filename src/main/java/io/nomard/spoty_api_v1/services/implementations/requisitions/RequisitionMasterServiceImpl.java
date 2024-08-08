package io.nomard.spoty_api_v1.services.implementations.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.requisitions.RequisitionMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.requisitions.RequisitionMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RequisitionMasterServiceImpl implements RequisitionMasterService {
    @Autowired
    private RequisitionMasterRepository requisitionMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<RequisitionMaster> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return requisitionMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    public RequisitionMaster getById(Long id) throws NotFoundException {
        Optional<RequisitionMaster> requisitionMaster = requisitionMasterRepo.findById(id);
        if (requisitionMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return requisitionMaster.get();
    }

    @Override
    public ArrayList<RequisitionMaster> getByContains(String search) {
        return requisitionMasterRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(RequisitionMaster requisitionMaster) {
        try {
            if (!requisitionMaster.getRequisitionDetails().isEmpty()) {
                for (int i = 0; i < requisitionMaster.getRequisitionDetails().size(); i++) {
                    requisitionMaster.getRequisitionDetails().get(i).setRequisition(requisitionMaster);
                }
            }
            requisitionMaster.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(requisitionMaster.getBranch())) {
                requisitionMaster.setBranch(authService.authUser().getBranch());
            }
            requisitionMaster.setCreatedBy(authService.authUser());
            requisitionMaster.setCreatedAt(LocalDateTime.now());
            requisitionMasterRepo.saveAndFlush(requisitionMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(RequisitionMaster data) throws NotFoundException {
        var opt = requisitionMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var requisitionMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            requisitionMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate()) && !Objects.equals(data.getDate(), requisitionMaster.getDate())) {
            requisitionMaster.setDate(data.getDate());
        }

        if (Objects.nonNull(data.getSupplier()) && !Objects.equals(data.getSupplier(), requisitionMaster.getSupplier())) {
            requisitionMaster.setSupplier(data.getSupplier());
        }

        if (Objects.nonNull(data.getBranch()) && !Objects.equals(data.getBranch(), requisitionMaster.getBranch())) {
            requisitionMaster.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getRequisitionDetails()) && !data.getRequisitionDetails().isEmpty()) {
            requisitionMaster.setRequisitionDetails(data.getRequisitionDetails());

            for (int i = 0; i < requisitionMaster.getRequisitionDetails().size(); i++) {
                requisitionMaster.getRequisitionDetails().get(i).setRequisition(requisitionMaster);
            }
        }

        if (Objects.nonNull(data.getShipVia()) && !"".equalsIgnoreCase(data.getShipVia())) {
            requisitionMaster.setShipVia(data.getShipVia());
        }

        if (Objects.nonNull(data.getShipMethod()) && !"".equalsIgnoreCase(data.getShipMethod())) {
            requisitionMaster.setShipMethod(data.getShipMethod());
        }

        if (Objects.nonNull(data.getShippingTerms()) && !"".equalsIgnoreCase(data.getShippingTerms())) {
            requisitionMaster.setShippingTerms(data.getShippingTerms());
        }

        if (Objects.nonNull(data.getDeliveryDate()) && !Objects.equals(data.getDeliveryDate(), requisitionMaster.getDeliveryDate())) {
            requisitionMaster.setDeliveryDate(data.getDeliveryDate());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            requisitionMaster.setNotes(data.getNotes());
        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            requisitionMaster.setStatus(data.getStatus());
        }

        if (!Objects.equals(data.getTotalCost(), requisitionMaster.getTotalCost())) {
            requisitionMaster.setTotalCost(data.getTotalCost());
        }

        requisitionMaster.setUpdatedBy(authService.authUser());
        requisitionMaster.setUpdatedAt(LocalDateTime.now());

        try {
            requisitionMasterRepo.saveAndFlush(requisitionMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            requisitionMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            requisitionMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
