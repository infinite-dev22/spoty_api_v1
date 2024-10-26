package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.EmploymentStatusDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.EmploymentStatusMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.EmploymentStatusRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.hrm.EmploymentStatusService;
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
import java.util.stream.Collectors;

@Service
public class EmploymentStatusServiceImpl implements EmploymentStatusService {
    @Autowired
    private EmploymentStatusRepository employmentStatusRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private EmploymentStatusMapper employmentStatusMapper;

    @Override
    public Page<EmploymentStatusDTO.EmploymentStatusAsWholeDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return employmentStatusRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(employmentStatus -> employmentStatusMapper.toWholeDTO(employmentStatus));
    }

    @Override
    public EmploymentStatusDTO.EmploymentStatusAsWholeDTO getById(Long id) throws NotFoundException {
        Optional<EmploymentStatus> employmentStatus = employmentStatusRepo.findById(id);
        if (employmentStatus.isEmpty()) {
            throw new NotFoundException();
        }
        return employmentStatusMapper.toWholeDTO(employmentStatus.get());
    }

    @Override
    public List<EmploymentStatusDTO.EmploymentStatusAsWholeDTO> getByContains(String search) {
        return employmentStatusRepo.searchAll(authService.authUser().getTenant().getId(), search)
                .stream()
                .map(employmentStatus -> employmentStatusMapper.toWholeDTO(employmentStatus))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(EmploymentStatus employmentStatus) {
        try {
            employmentStatus.setTenant(authService.authUser().getTenant());
            employmentStatus.setCreatedBy(authService.authUser());
            employmentStatus.setCreatedAt(LocalDateTime.now());
            employmentStatusRepo.save(employmentStatus);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(EmploymentStatus data) throws NotFoundException {
        var opt = employmentStatusRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var employmentStatus = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            employmentStatus.setName(data.getName());
        }

        if (Objects.nonNull(data.getColor()) && !"".equalsIgnoreCase(data.getColor())) {
            employmentStatus.setColor(data.getColor());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            employmentStatus.setDescription(data.getDescription());
        }

        employmentStatus.setUpdatedBy(authService.authUser());
        employmentStatus.setUpdatedAt(LocalDateTime.now());

        try {
            employmentStatusRepo.save(employmentStatus);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            employmentStatusRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            employmentStatusRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
