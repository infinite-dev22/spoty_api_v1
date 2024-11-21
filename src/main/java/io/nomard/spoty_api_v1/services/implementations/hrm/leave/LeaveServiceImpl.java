package io.nomard.spoty_api_v1.services.implementations.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.repositories.hrm.leave.LeaveRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.leave.LeaveService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRepository leaveRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Page<Leave> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return leaveRepo.findAllByTenantId(authService.authUser().getTenant().getId(), authService.authUser().getId(), pageRequest);
    }

    @Override
    public Leave getById(Long id) throws NotFoundException {
        Optional<Leave> leave = leaveRepo.findById(id);
        if (leave.isEmpty()) {
            throw new NotFoundException();
        }
        return leave.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Leave leave) {
        try {
            leave.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(leave.getBranch())) {
                leave.setBranch(authService.authUser().getBranch());
            }
            leave.setLeaveStatus("Pending");
            leave.setDuration(Duration.between(leave.getStartDate(), leave.getEndDate()));
            leave.setApproved(false);
            leave.setCreatedBy(authService.authUser());
            leave.setCreatedAt(LocalDateTime.now());
            leaveRepo.save(leave);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Leave data) throws NotFoundException {
        var opt = leaveRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var leave = opt.get();

        if (Objects.nonNull(data.getEmployee())) {
            leave.setEmployee(data.getEmployee());
        }

        if (Objects.nonNull(data.getDesignation())) {
            leave.setDesignation(data.getDesignation());
        }

        if (Objects.nonNull(data.getStartDate()) && Objects.nonNull(data.getEndDate())) {
            leave.setStartDate(data.getStartDate());
            leave.setEndDate(data.getEndDate());
            leave.setDuration(Duration.between(leave.getStartDate(), leave.getEndDate()));
        }

        if (Objects.nonNull(data.getLeaveType())) {
            leave.setLeaveType(data.getLeaveType());
        }

        if (Objects.nonNull(data.getAttachment()) && !"".equalsIgnoreCase(data.getAttachment())) {
            leave.setAttachment(data.getAttachment());
        }

        leave.setUpdatedBy(authService.authUser());
        leave.setUpdatedAt(LocalDateTime.now());

        try {
            leaveRepo.save(leave);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @CacheEvict(value = "leave", key = "#approvalModel.id")
    @Transactional
    public ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException {
        var opt = leaveRepo.findById(approvalModel.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var leave = opt.get();

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "returned")) {
            leave.setApproved(false);
            leave.setLeaveStatus("Returned");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "approved")) {
            leave.setApproved(true);
            leave.setLeaveStatus("Approved");
        }

        if (Objects.equals(approvalModel.getStatus().toLowerCase(), "rejected")) {
            leave.setApproved(false);
            leave.setLeaveStatus("Rejected");
        }

        leave.setUpdatedBy(authService.authUser());
        leave.setUpdatedAt(LocalDateTime.now());
        try {
            leaveRepo.save(leave);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            leaveRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            leaveRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
