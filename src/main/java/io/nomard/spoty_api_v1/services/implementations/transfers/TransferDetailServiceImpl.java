package io.nomard.spoty_api_v1.services.implementations.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.transfers.TransferDetailRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.transfers.TransferDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransferDetailServiceImpl implements TransferDetailService {
    @Autowired
    private TransferDetailRepository transferDetailRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<TransferDetail> getAll() {
        return transferDetailRepo.findAll();
    }

    @Override
    public TransferDetail getById(Long id) throws NotFoundException {
        Optional<TransferDetail> transferDetail = transferDetailRepo.findById(id);
        if (transferDetail.isEmpty()) {
            throw new NotFoundException();
        }
        return transferDetail.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(TransferDetail transferDetail) {
        try {
            transferDetail.setCreatedBy(authService.authUser());
            transferDetail.setCreatedAt(new Date());
            transferDetailRepo.saveAndFlush(transferDetail);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> saveMultiple(List<TransferDetail> transferDetailList) {
        return null;
    }

    @Override
    public ResponseEntity<ObjectNode> update(TransferDetail data) throws NotFoundException {
        var opt = transferDetailRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var transferDetail = opt.get();

        if (Objects.nonNull(data.getProduct())) {
            transferDetail.setProduct(data.getProduct());
        }

//        if (!Objects.equals(data.getNetTax(), transferDetail.getNetTax())) {
//            transferDetail.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            transferDetail.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), transferDetail.getDiscount())) {
//            transferDetail.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            transferDetail.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            transferDetail.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), transferDetail.getTotal())) {
//            transferDetail.setTotal(data.getTotal());
//        }

        if (!Objects.equals(data.getQuantity(), transferDetail.getQuantity())) {
            transferDetail.setQuantity(data.getQuantity());
        }

        transferDetail.setUpdatedBy(authService.authUser());
        transferDetail.setUpdatedAt(new Date());

        try {
            transferDetailRepo.saveAndFlush(transferDetail);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            transferDetailRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException {
        return null;
    }
}
