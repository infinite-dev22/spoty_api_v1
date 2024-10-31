package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DepartmentDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.DepartmentMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.DepartmentRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.hrm.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public Page<DepartmentDTO.DepartmentAsWholeDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return departmentRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(department -> departmentMapper.toDTO(department));
    }

    @Override
    public DepartmentDTO.DepartmentAsWholeDTO getById(Long id) throws NotFoundException {
        Optional<Department> department = departmentRepo.findById(id);
        if (department.isEmpty()) {
            throw new NotFoundException();
        }
        return departmentMapper.toDTO(department.get());
    }

    @Override
    public List<DepartmentDTO.DepartmentAsWholeDTO> getByContains(String search) {
        return departmentRepo.searchAll(authService.authUser().getTenant().getId(), search)
                .stream()
                .map(department -> departmentMapper.toDTO(department))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Department department) {
        try {
            department.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(department.getBranch())) {
                department.setBranch(authService.authUser().getBranch());
            }
            department.setCreatedBy(authService.authUser());
            department.setCreatedAt(LocalDateTime.now());
            departmentRepo.save(department);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Department data) throws NotFoundException {
        var opt = departmentRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var department = opt.get();

        if (Objects.nonNull(data.getBranch())) {
            department.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            department.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            department.setDescription(data.getDescription());
        }

        department.setUpdatedBy(authService.authUser());
        department.setUpdatedAt(LocalDateTime.now());

        try {
            departmentRepo.save(department);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            departmentRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(List<Long> idList) {
        try {
            departmentRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
