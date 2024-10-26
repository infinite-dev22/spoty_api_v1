package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
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
    public ArrayList<Reviewer> getAll(int pageNo, int pageSize) {
        return approverRepo.findAllByTenantId(authService.authUser().getTenant().getId());
    }

    @Override
    public Reviewer getById(Long id) throws NotFoundException {
        Optional<Reviewer> approver = approverRepo.findById(id);
        if (approver.isEmpty()) {
            throw new NotFoundException();
        }
        return approver.get();
    }

    @Override
    public Reviewer getByUserId(Long id) throws NotFoundException {
        Optional<Reviewer> approver = approverRepo.findByUserId(id);
        if (approver.isEmpty()) {
            throw new NotFoundException("User is not an approver");
        }
        return approver.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Reviewer reviewer) {
        try {
            reviewer.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(reviewer.getBranch())) {
                reviewer.setBranch(authService.authUser().getBranch());
            }
            reviewer.setCreatedBy(authService.authUser());
            reviewer.setCreatedAt(LocalDateTime.now());
            approverRepo.save(reviewer);
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
