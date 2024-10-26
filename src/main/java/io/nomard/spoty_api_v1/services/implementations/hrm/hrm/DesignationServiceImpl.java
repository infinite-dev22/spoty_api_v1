package io.nomard.spoty_api_v1.services.implementations.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DesignationDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.DesignationMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.DesignationRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.hrm.hrm.DesignationService;
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
public class DesignationServiceImpl implements DesignationService {
    @Autowired
    private DesignationRepository designationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private DesignationMapper designationMapper;

    @Override
    public Page<DesignationDTO.DesignationAsWholeDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return designationRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(designation -> designationMapper.toDTO(designation));
    }

    @Override
    public DesignationDTO.DesignationAsWholeDTO getById(Long id) throws NotFoundException {
        Optional<Designation> designation = designationRepo.findById(id);
        if (designation.isEmpty()) {
            throw new NotFoundException();
        }
        return designationMapper.toDTO(designation.get());
    }

    @Override
    public List<DesignationDTO.DesignationAsWholeDTO> getByContains(String search) {
        return designationRepo.searchAll(authService.authUser().getTenant().getId(), search)
                .stream()
                .map(designation -> designationMapper.toDTO(designation))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Designation designation) {
        try {
            designation.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(designation.getBranch())) {
                designation.setBranch(authService.authUser().getBranch());
            }
            designation.setCreatedBy(authService.authUser());
            designation.setCreatedAt(LocalDateTime.now());
            designationRepo.save(designation);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Designation data) throws NotFoundException {
        var opt = designationRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var designation = opt.get();

        if (Objects.nonNull(data.getBranch())) {
            designation.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            designation.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            designation.setDescription(data.getDescription());
        }

        designation.setUpdatedBy(authService.authUser());
        designation.setUpdatedAt(LocalDateTime.now());

        try {
            designationRepo.save(designation);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            designationRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) {
        try {
            designationRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
