package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.SupplierDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.SupplierMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.SupplierRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public Page<SupplierDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return supplierRepo.findByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(supplier -> supplierMapper.toDTO(supplier));
    }

    @Override
    public SupplierDTO getById(Long id) throws NotFoundException {
        Optional<Supplier> supplier = supplierRepo.findById(id);
        if (supplier.isEmpty()) {
            throw new NotFoundException();
        }
        return supplierMapper.toDTO(supplier.get());
    }

    @Override
    public List<SupplierDTO> getByContains(String search) {
        return supplierRepo.search(authService.authUser().getTenant().getId(), search.toLowerCase())
                .stream()
                .map(supplier -> supplierMapper.toDTO(supplier))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Supplier supplier) {
        supplier.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(supplier.getBranch())) {
            supplier.setBranch(authService.authUser().getBranch());
        }
        supplier.setCreatedBy(authService.authUser());
        supplier.setCreatedAt(LocalDateTime.now());
        try {
            supplierRepo.save(supplier);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Supplier data) throws NotFoundException {
        var opt = supplierRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var supplier = opt.get();

        if (Objects.nonNull(data.getFirstName()) && !"".equalsIgnoreCase(data.getFirstName())) {
            supplier.setFirstName(data.getFirstName());
        }

        if (Objects.nonNull(data.getOtherName()) && !"".equalsIgnoreCase(data.getOtherName())) {
            supplier.setOtherName(data.getOtherName());
        }

        if (Objects.nonNull(data.getLastName()) && !"".equalsIgnoreCase(data.getLastName())) {
            supplier.setLastName(data.getLastName());
        }

        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            supplier.setEmail(data.getEmail());
        }

        if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
            supplier.setPhone(data.getPhone());
        }

        if (Objects.nonNull(data.getCountry()) && !"".equalsIgnoreCase(data.getCountry())) {
            supplier.setCountry(data.getCountry());
        }

        if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
            supplier.setCity(data.getCity());
        }

        if (Objects.nonNull(data.getAddress()) && !"".equalsIgnoreCase(data.getAddress())) {
            supplier.setAddress(data.getAddress());
        }

        if (Objects.nonNull(data.getTaxNumber()) && !"".equalsIgnoreCase(data.getTaxNumber())) {
            supplier.setTaxNumber(data.getTaxNumber());
        }

        supplier.setUpdatedBy(authService.authUser());
        supplier.setUpdatedAt(LocalDateTime.now());

        try {
            supplierRepo.save(supplier);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            supplierRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            supplierRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
