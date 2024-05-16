package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BranchRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BranchServiceImpl implements BranchService {
    @Autowired
    private BranchRepository branchRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Branch> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Branch> page = branchRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public Branch getById(Long id) throws NotFoundException {
        Optional<Branch> branch = branchRepo.findById(id);
        if (branch.isEmpty()) {
            throw new NotFoundException();
        }
        return branch.get();
    }

    @Override
    public List<Branch> getByContains(String search) {
        return branchRepo.searchAllByEmailContainingIgnoreCaseOrNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrTownContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                search.toLowerCase(), search.toLowerCase(), search.toLowerCase(), search.toLowerCase(), search.toLowerCase()
        );
    }

    @Override
    public ResponseEntity<ObjectNode> save(Branch branch) {
        try {
            branch.setCreatedBy(authService.authUser());
            branch.setCreatedAt(new Date());
            branchRepo.saveAndFlush(branch);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Branch data) throws NotFoundException {
        var opt = branchRepo.findById(data.getId());
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var branch = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            branch.setName(data.getName());
        }

        if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
            branch.setCity(data.getCity());
        }

        if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
            branch.setPhone(data.getPhone());
        }

        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            branch.setEmail(data.getEmail());
        }

        if (Objects.nonNull(data.getTown()) && !"".equalsIgnoreCase(data.getTown())) {
            branch.setTown(data.getTown());
        }

        if (Objects.nonNull(data.getZipCode()) && !"".equalsIgnoreCase(data.getZipCode())) {
            branch.setZipCode(data.getZipCode());
        }
        branch.setUpdatedBy(authService.authUser());
        branch.setUpdatedAt(new Date());

        try {
            branchRepo.save(branch);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            branchRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            branchRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
