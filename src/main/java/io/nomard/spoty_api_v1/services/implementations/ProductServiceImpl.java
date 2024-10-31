package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ProductDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.mappers.ProductMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ProductRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private DocumentServiceImpl documentService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return productRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(product -> productMapper.toDTO(product));
    }

    @Override
    @Cacheable("products_no_paged")
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllNonPaged() {
        return productRepo.findAllByTenantIdNonPaged(authService.authUser().getTenant().getId())
                .stream()
                .map(product -> productMapper.toDTO(product))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public ProductDTO getById(Long id) throws NotFoundException {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException();
        }
        return productMapper.toDTO(product.get());
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public Product getByIdInternally(Long id) throws NotFoundException {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException();
        }
        return product.get();
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public List<ProductDTO> getByContains(String search) {
        return productRepo.searchAll(search.toLowerCase())
                .stream()
                .map(product -> productMapper.toDTO(product))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("products")
    @Transactional(readOnly = true)
    public List<ProductDTO> getWarning() {
        return productRepo.findAllByTenantIdByQuantityIsLessThanEqualStockAlert()
                .stream()
                .map(product -> productMapper.toDTO(product))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Product product, MultipartFile file) {
        var fileURL = "";
        try {
            if (Objects.nonNull(file)) {
                fileURL = String.valueOf(documentService.save(file));
                product.setImage(fileURL);
            }
        } catch (Exception e) {
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        return save(product);
    }

    @Override
    public ResponseEntity<ObjectNode> save(Product product) {
        product.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(product.getBranch())) {
            product.setBranch(authService.authUser().getBranch());
        }
        product.setCreatedBy(authService.authUser());
        product.setCreatedAt(LocalDateTime.now());
        try {
            productRepo.save(product);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "products", key = "#data.id")
    public ResponseEntity<ObjectNode> update(Product data, MultipartFile file) throws NotFoundException {
        var opt = productRepo.findById(data.getId());
        var fileURL = "";

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var product = opt.get();

        if (Objects.nonNull(data.getUnit())) {
            product.setUnit(data.getUnit());
        }

        if (Objects.nonNull(data.getCategory())) {
            product.setCategory(data.getCategory());
        }

        if (Objects.nonNull(data.getBrand())) {
            product.setBrand(data.getBrand());
        }

        if (Objects.nonNull(data.getBranch())) {
            product.setBranch(data.getBranch());
        }

        if (Objects.nonNull(data.getBarcodeType()) && !"".equalsIgnoreCase(data.getBarcodeType())) {
            product.setBarcodeType(data.getBarcodeType());
        }

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            product.setName(data.getName());
        }

        if (!Objects.equals(data.getQuantity(), 0d)) {
            product.setQuantity(data.getQuantity());
        }

        if (!Objects.equals(data.getCostPrice(), 0d)) {
            product.setCostPrice(data.getCostPrice());
        }

        if (!Objects.equals(data.getSalePrice(), 0d)) {
            product.setSalePrice(data.getSalePrice());
        }

        if (!Objects.equals(product.getDiscount(), data.getDiscount()) && Objects.nonNull(data.getDiscount())) {
            product.setDiscount(data.getDiscount());
        }

        if (!Objects.equals(product.getTax(), data.getTax()) && Objects.nonNull(data.getTax())) {
            product.setTax(data.getTax());
        }

        if (!Objects.equals(data.getStockAlert(), 0d)) {
            product.setStockAlert(data.getStockAlert());
        }

        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
            product.setSerialNumber(data.getSerialNumber());
        }
        if (Objects.nonNull(file)) {
            try {
                documentService.delete(product.getImage());
                fileURL = String.valueOf(documentService.save(file));
            } catch (Exception e) {
                return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }
            product.setImage(fileURL);
        }

        product.setUpdatedBy(authService.authUser());
        product.setUpdatedAt(LocalDateTime.now());

        try {
            productRepo.save(product);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            productRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            productRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
