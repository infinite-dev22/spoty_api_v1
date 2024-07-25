package io.nomard.spoty_api_v1.services.implementations.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.leave.LeaveRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.leave.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRepository leaveStatusRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Leave> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Leave> page = leaveStatusRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
        return page.getContent();
    }

    @Override
    public Leave getById(Long id) throws NotFoundException {
        Optional<Leave> leaveStatus = leaveStatusRepo.findById(id);
        if (leaveStatus.isEmpty()) {
            throw new NotFoundException();
        }
        return leaveStatus.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Leave leave) {
        try {
            leave.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(leave.getBranch())) {
                leave.setBranch(authService.authUser().getBranch());
            }
            leave.setCreatedBy(authService.authUser());
            leave.setCreatedAt(LocalDateTime.now());
            leaveStatusRepo.saveAndFlush(leave);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Leave data) throws NotFoundException {
        var opt = leaveStatusRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var leaveStatus = opt.get();

        if (Objects.nonNull(data.getEmployee())) {
            leaveStatus.setEmployee(data.getEmployee());
        }

        if (Objects.nonNull(data.getDesignation())) {
            leaveStatus.setDesignation(data.getDesignation());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            leaveStatus.setDescription(data.getDescription());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            leaveStatus.setDescription(data.getDescription());
        }

        if (Objects.nonNull(data.getStartDate())) {
            leaveStatus.setStartDate(data.getStartDate());
        }

        if (Objects.nonNull(data.getEndDate())) {
            leaveStatus.setEndDate(data.getEndDate());
        }

        if (Objects.nonNull(data.getDuration())) {
            leaveStatus.setDuration(data.getDuration());
        }

        if (Objects.nonNull(data.getLeaveType())) {
            leaveStatus.setLeaveType(data.getLeaveType());
        }

        if (Objects.nonNull(data.getAttachment()) && !"".equalsIgnoreCase(data.getAttachment())) {
            leaveStatus.setAttachment(data.getAttachment());
        }

        if (data.getStatus() != '\0') {
            leaveStatus.setStatus(data.getStatus());
        }

        leaveStatus.setUpdatedBy(authService.authUser());
        leaveStatus.setUpdatedAt(LocalDateTime.now());

        try {
            leaveStatusRepo.saveAndFlush(leaveStatus);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            leaveStatusRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            leaveStatusRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
