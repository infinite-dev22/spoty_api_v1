package io.nomard.spoty_api_v1.services.implementations.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.transfers.TransferMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.transfers.TransferMasterService;
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
public class TransferMasterServiceImpl implements TransferMasterService {
    @Autowired
    private TransferMasterRepository transferMasterRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<TransferMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<TransferMaster> page = transferMasterRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public TransferMaster getById(Long id) throws NotFoundException {
        Optional<TransferMaster> transferMaster = transferMasterRepo.findById(id);
        if (transferMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return transferMaster.get();
    }

    @Override
    public List<TransferMaster> getByContains(String search) {
        return transferMasterRepo.searchAllByRefContainingIgnoreCase(
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(TransferMaster transferMaster) {
        try {
            transferMaster.setCreatedBy(authService.authUser());
            transferMaster.setCreatedAt(new Date());
            transferMasterRepo.saveAndFlush(transferMaster);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(TransferMaster data) throws NotFoundException {
        var opt = transferMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var transferMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            transferMaster.setRef(data.getRef());
        }

        if (Objects.nonNull(data.getDate())) {
            transferMaster.setDate(data.getDate());
        }

//        if (Objects.nonNull(data.getCustomer())) {
//            transferMaster.setCustomer(data.getCustomer());
//        }

        if (Objects.nonNull(data.getFromBranch())) {
            transferMaster.setFromBranch(data.getFromBranch());
        }

        if (Objects.nonNull(data.getToBranch())) {
            transferMaster.setToBranch(data.getToBranch());
        }

        if (Objects.nonNull(data.getTransferDetails()) && !data.getTransferDetails().isEmpty()) {
            transferMaster.setTransferDetails(data.getTransferDetails());
        }

//        if (!Objects.equals(data.getTaxRate(), transferMaster.getTaxRate())) {
//            transferMaster.setTaxRate(data.getTaxRate());
//        }
//
//        if (!Objects.equals(data.getNetTax(), transferMaster.getNetTax())) {
//            transferMaster.setNetTax(data.getNetTax());
//        }
//
//        if (!Objects.equals(data.getDiscount(), transferMaster.getDiscount())) {
//            transferMaster.setDiscount(data.getDiscount());
//        }

//        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
//            transferMaster.setShipping(data.getShipping());
//        }

//        if (!Objects.equals(data.getPaid(), transferMaster.getPaid())) {
//            transferMaster.setPaid(data.getPaid());
//        }
//
//        if (!Objects.equals(data.getTotal(), transferMaster.getTotal())) {
//            transferMaster.setTotal(data.getTotal());
//        }
//
//        if (!Objects.equals(data.getDue(), transferMaster.getDue())) {
//            transferMaster.setDue(data.getDue());
//        }
//
//        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
//            transferMaster.setStatus(data.getStatus());
//        }
//
//        if (Objects.nonNull(data.getPaymentStatus()) && !"".equalsIgnoreCase(data.getPaymentStatus())) {
//            transferMaster.setPaymentStatus(data.getPaymentStatus());
//        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            transferMaster.setNotes(data.getNotes());
        }

        transferMaster.setUpdatedBy(authService.authUser());
        transferMaster.setUpdatedAt(new Date());

        try {
            transferMasterRepo.saveAndFlush(transferMaster);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            transferMasterRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            transferMasterRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
