package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Organisation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.OrganisationRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrganisationServiceImpl implements OrganisationService {
    @Autowired
    private OrganisationRepository organisationRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Organisation> getAll() {
        return organisationRepo.findAll();
    }

    @Override
    public Organisation getById(Long id) throws NotFoundException {
        Optional<Organisation> organisation = organisationRepo.findById(id);
        if (organisation.isEmpty()) {
            throw new NotFoundException();
        }
        return organisation.get();
    }

    @Override
    public ResponseEntity<ObjectNode> save(Organisation organisation) {
        try {
            organisation.setCreatedBy(authService.authUser());
            organisation.setCreatedAt(new Date());
            organisationRepo.saveAndFlush(organisation);
            return spotyResponseImpl.created();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, Organisation organisation) {
        try {
            organisation.setUpdatedBy(authService.authUser());
            organisation.setUpdatedAt(new Date());
            organisation.setId(id);
            organisationRepo.saveAndFlush(organisation);
            return spotyResponseImpl.ok();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            organisationRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
