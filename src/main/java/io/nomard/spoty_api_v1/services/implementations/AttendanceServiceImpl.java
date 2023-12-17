package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Attendance;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.AttendanceRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Attendance> getAll() {
        return attendanceRepo.findAll();
    }

    @Override
    public Attendance getById(Long id) throws NotFoundException {
        Optional<Attendance> attendance = attendanceRepo.findById(id);
        if (attendance.isEmpty()) {
            throw new NotFoundException();
        }
        return attendance.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(Attendance attendance) {
        try {
            attendance.setCreatedBy(authService.authUser());
            attendance.setCreatedAt(new Date());
            attendanceRepo.saveAndFlush(attendance);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Attendance data) throws NotFoundException {
        var opt = attendanceRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var attendance = opt.get();

//        if (Objects.nonNull(data.getProduct())) {
//            attendance.setProduct(data.getProduct());
//        }

//        if (!Objects.equals(data.getNetTax(), attendance.getNetTax())) {
//            attendance.setNetTax(data.getNetTax());
//        }
//
//        if (Objects.nonNull(data.getTaxType()) && !"".equalsIgnoreCase(data.getTaxType())) {
//            attendance.setTaxType(data.getTaxType());
//        }
//
//        if (!Objects.equals(data.getDiscount(), attendance.getDiscount())) {
//            attendance.setDiscount(data.getDiscount());
//        }
//
//        if (Objects.nonNull(data.getDiscountType()) && !"".equalsIgnoreCase(data.getDiscountType())) {
//            attendance.setDiscountType(data.getDiscountType());
//        }
//
//        if (Objects.nonNull(data.getSerialNumber()) && !"".equalsIgnoreCase(data.getSerialNumber())) {
//            attendance.setSerialNumber(data.getSerialNumber());
//        }
//
//        if (!Objects.equals(data.getTotal(), attendance.getTotal())) {
//            attendance.setTotal(data.getTotal());
//        }

//        if (!Objects.equals(data.getQuantity(), attendance.getQuantity())) {
//            attendance.setQuantity(data.getQuantity());
//        }

        attendance.setUpdatedBy(authService.authUser());
        attendance.setUpdatedAt(new Date());

        try {
            attendanceRepo.saveAndFlush(attendance);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            attendanceRepo.deleteById(id);
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
