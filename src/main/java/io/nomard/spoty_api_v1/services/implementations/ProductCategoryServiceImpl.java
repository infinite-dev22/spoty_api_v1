package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ProductCategoryDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.ProductCategoryMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ProductCategoryRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ProductCategoryService;
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
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductCategoryRepository productCategoryRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Override
    public Page<ProductCategoryDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return productCategoryRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(productCategory -> productCategoryMapper.toDTO(productCategory));
    }

    @Override
    public ProductCategoryDTO getById(Long id) throws NotFoundException {
        Optional<ProductCategory> productCategory = productCategoryRepo.findById(id);
        if (productCategory.isEmpty()) {
            throw new NotFoundException();
        }
        return productCategoryMapper.toDTO(productCategory.get());
    }

    @Override
    public List<ProductCategoryDTO> getByContains(String search) {
        return productCategoryRepo.searchAll(authService.authUser().getTenant().getId(), search.toLowerCase())
                .stream()
                .map(productCategory -> productCategoryMapper.toDTO(productCategory))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(ProductCategory productCategory) {
        try {
            productCategory.setTenant(authService.authUser().getTenant());
            if (Objects.isNull(productCategory.getBranch())) {
                productCategory.setBranch(authService.authUser().getBranch());
            }
            productCategory.setCreatedBy(authService.authUser());
            productCategory.setCreatedAt(LocalDateTime.now());
            productCategoryRepo.save(productCategory);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(ProductCategory data) throws NotFoundException {
        var opt = productCategoryRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var productCategory = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            productCategory.setName(data.getName());
        }

        if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
            productCategory.setDescription(data.getDescription());
        }

        productCategory.setUpdatedBy(authService.authUser());
        productCategory.setUpdatedAt(LocalDateTime.now());

        try {
            productCategoryRepo.save(productCategory);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            productCategoryRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            productCategoryRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
