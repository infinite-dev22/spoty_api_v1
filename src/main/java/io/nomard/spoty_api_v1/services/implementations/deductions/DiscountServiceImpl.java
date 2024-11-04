package io.nomard.spoty_api_v1.services.implementations.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.DiscountDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.DiscountMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.deductions.DiscountRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.deductions.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private DiscountRepository discountRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private DiscountMapper discountMapper;

    @Override
    public Page<DiscountDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return discountRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(discount -> discountMapper.toDTO(discount));
    }

    @Override
    public DiscountDTO getById(Long id) throws NotFoundException {
        Optional<Discount> discount = discountRepo.findById(id);
        if (discount.isEmpty()) {
            throw new NotFoundException();
        }
        return discountMapper.toDTO(discount.get());
    }

    @Override
    public Discount getByIdInternal(Long id) throws NotFoundException {
        Optional<Discount> discount = discountRepo.findById(id);
        if (discount.isEmpty()) {
            throw new NotFoundException();
        }
        return discount.get();
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Discount discount) {
        try {
            discount.setTenant(authService.authUser().getTenant());
            discount.setBranch(authService.authUser().getBranch());
            discount.setCreatedBy(authService.authUser());
            discount.setCreatedAt(LocalDateTime.now());
            discountRepo.save(discount);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Discount data) throws NotFoundException {
        var opt = discountRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var discount = opt.get();
        if (!Objects.equals(discount.getName(), data.getName()) && Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            discount.setName(data.getName());
        }
        if (!Objects.equals(discount.getPercentage(), data.getPercentage())) {
            discount.setPercentage(data.getPercentage());
        }
        discount.setUpdatedBy(authService.authUser());
        discount.setUpdatedAt(LocalDateTime.now());
        try {
            discountRepo.save(discount);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            discountRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException {
        try {
            discountRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
