package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.UnitOfMeasureRepository;
import io.nomard.spoty_api_v1.services.interfaces.UnitOfMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {
    @Autowired
    private UnitOfMeasureRepository uomRepo;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    public List<UnitOfMeasure> getAll() {
        return uomRepo.findAll();
    }

    @Override
    public UnitOfMeasure getById(Long id) throws NotFoundException {
        Optional<UnitOfMeasure> unitOfMeasure = uomRepo.findById(id);
        if (unitOfMeasure.isEmpty()) {
            throw new NotFoundException();
        }
        return unitOfMeasure.get();
    }

    @Override
    public List<UnitOfMeasure> getByContains(String search) {
        return uomRepo.searchAll(search.toLowerCase());
    }

    @Override
    public UnitOfMeasure save(UnitOfMeasure uom) {
        uom.setCreatedBy(authService.authUser());
        uom.setCreatedAt(new Date());
        return uomRepo.saveAndFlush(uom);
    }

    @Override
    public UnitOfMeasure update(Long id, UnitOfMeasure uom) {
        uom.setId(id);
        uom.setUpdatedBy(authService.authUser());
        uom.setUpdatedAt(new Date());
        return uomRepo.saveAndFlush(uom);
    }

    @Override
    public String delete(Long id) {
        try {
            uomRepo.deleteById(id);
            return "UnitOfMeasure successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to delete UnitOfMeasure, Contact your system administrator for assistance";
        }
    }
}
