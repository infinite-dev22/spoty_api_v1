package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.BrandDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.BrandMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BrandRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.BrandService;
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
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public Page<BrandDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return brandRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(brand -> brandMapper.toDTO(brand));
    }

    @Override
    public BrandDTO getById(Long id) throws NotFoundException {
        Optional<Brand> brand = brandRepo.findById(id);
        if (brand.isEmpty()) {
            throw new NotFoundException();
        }
        return brandMapper.toDTO(brand.get());
    }

    @Override
    public List<BrandDTO> getByContains(String search) {
        return brandRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase()).stream().map(brand -> brandMapper.toDTO(brand)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Brand brand) {
        try {
            brand.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(brand.getBranch())) {
                brand.setBranch(authService.authUser().getBranch());
            }
            brand.setCreatedBy(authService.authUser());
            brand.setCreatedAt(LocalDateTime.now());
            brandRepo.save(brand);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Brand data) throws NotFoundException {
        var opt = brandRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var brand = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            brand.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            brand.setDescription(data.getDescription());
        }

        if (Objects.nonNull(data.getImage()) && !"".equalsIgnoreCase(data.getImage())) {
            brand.setImage(data.getImage());
        }

        brand.setUpdatedBy(authService.authUser());
        brand.setUpdatedAt(LocalDateTime.now());

        try {
            brandRepo.save(brand);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            brandRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            brandRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
