package io.nomard.spoty_api_v1.services.implementations.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.transfers.TransferMasterRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.transfers.TransferMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class TransferMasterServiceImpl implements TransferMasterService {
    @Autowired
    private TransferMasterRepository transferMasterRepo;
    @Autowired
    private TransferTransactionServiceImpl transferTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public List<TransferMaster> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<TransferMaster> page = transferMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public TransferMaster getById(Long id) throws NotFoundException {
        Optional<TransferMaster> transferMaster = transferMasterRepo.findById(id);
        if (transferMaster.isEmpty()) {
            throw new NotFoundException();
        }
        return transferMaster.get();
    }

    @Override
    @Cacheable("transfer_masters")
    @Transactional(readOnly = true)
    public List<TransferMaster> getByContains(String search) {
        return transferMasterRepo.searchAllByRefContainingIgnoreCase(
                search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(TransferMaster transferMaster) {
        try {
            for (int i = 0; i < transferMaster.getTransferDetails().size(); i++) {
                transferMaster.getTransferDetails().get(i).setTransfer(transferMaster);
            }
            transferMaster.setTenant(authService.authUser().getTenant());
            transferMaster.setCreatedBy(authService.authUser());
            transferMaster.setCreatedAt(new Date());
            transferMasterRepo.saveAndFlush(transferMaster);

            for (int i = 0; i < transferMaster.getTransferDetails().size(); i++) {
                transferTransactionService.save(transferMaster.getTransferDetails().get(i));
            }

            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "transfer_masters", key = "#data.id")
    public ResponseEntity<ObjectNode> update(TransferMaster data) throws NotFoundException {
        var opt = transferMasterRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var transferMaster = opt.get();

        if (Objects.nonNull(data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
            transferMaster.setRef(data.getRef());
        }

        if (!Objects.equals(transferMaster.getDate(), data.getDate()) && Objects.nonNull(data.getDate())) {
            transferMaster.setDate(data.getDate());
        }

        if (!Objects.equals(transferMaster.getFromBranch(), data.getFromBranch()) && Objects.nonNull(data.getFromBranch())) {
            transferMaster.setFromBranch(data.getFromBranch());
        }

        if (!Objects.equals(transferMaster.getToBranch(), data.getToBranch()) && Objects.nonNull(data.getToBranch())) {
            transferMaster.setToBranch(data.getToBranch());
        }

        if (Objects.nonNull(data.getTransferDetails()) && !data.getTransferDetails().isEmpty()) {
            transferMaster.setTransferDetails(data.getTransferDetails());

            for (int i = 0; i < transferMaster.getTransferDetails().size(); i++) {
                transferMaster.getTransferDetails().get(i).setTransfer(transferMaster);
                try {
                    transferTransactionService.update(transferMaster.getTransferDetails().get(i));
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Objects.nonNull(data.getShipping()) && !"".equalsIgnoreCase(data.getShipping())) {
            transferMaster.setShipping(data.getShipping());
        }

        if (!Objects.equals(data.getTotal(), transferMaster.getTotal())) {
            transferMaster.setTotal(data.getTotal());
        }

        if (Objects.nonNull(data.getStatus()) && !"".equalsIgnoreCase(data.getStatus())) {
            transferMaster.setStatus(data.getStatus());
        }

        if (Objects.nonNull(data.getNotes()) && !"".equalsIgnoreCase(data.getNotes())) {
            transferMaster.setNotes(data.getNotes());
        }

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
