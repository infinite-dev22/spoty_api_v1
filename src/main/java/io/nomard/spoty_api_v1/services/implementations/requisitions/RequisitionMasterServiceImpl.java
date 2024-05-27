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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public List<RequisitionMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<RequisitionMaster> page = requisitionMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
    public List<RequisitionMaster> getByContains(String search) {
        return requisitionMasterRepo.searchAllByRefContainingIgnoreCaseOrStatusContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(RequisitionMaster requisitionMaster) {
        try {
            if (!requisitionMaster.getRequisitionDetails().isEmpty()) {
                for (int i = 0; i < requisitionMaster.getRequisitionDetails().size(); i++) {
                    requisitionMaster.getRequisitionDetails().get(i).setRequisition(requisitionMaster);
                }
            }
            requisitionMaster.setTenant(authService.authUser().getTenant());
            requisitionMaster.setCreatedBy(authService.authUser());
            requisitionMaster.setCreatedAt(new Date());
            requisitionMasterRepo.saveAndFlush(requisitionMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
        requisitionMaster.setUpdatedAt(new Date());

        try {
            requisitionMasterRepo.saveAndFlush(requisitionMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
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
