package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Approver;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ApproverRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ApproverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class ApproverServiceImpl implements ApproverService {
    @Autowired
    private ApproverRepository approverRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public ArrayList<Approver> getAll(int pageNo, int pageSize) {
        return approverRepo.findAllByTenantId(authService.authUser().getTenant().getId());
    }

    @Override
    public Approver getById(Long id) throws NotFoundException {
        Optional<Approver> approver = approverRepo.findById(id);
        if (approver.isEmpty()) {
            throw new NotFoundException();
        }
        return approver.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Approver approver) {
        try {
            approver.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(approver.getBranch())) {
                approver.setBranch(authService.authUser().getBranch());
            }
            approver.setCreatedBy(authService.authUser());
            approver.setCreatedAt(LocalDateTime.now());
            approverRepo.save(approver);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            approverRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
