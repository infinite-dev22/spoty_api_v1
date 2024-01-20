package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<User> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<User> page = userRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
    }

    @Override
    public User getById(Long id) throws NotFoundException {
        Optional<User> user = userRepo.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        return user.get();
    }

    @Override
    public User getByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public List<User> getByContains(String search) {
        return userRepo.searchAllByEmailContainingIgnoreCase(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> update(User data) throws NotFoundException {
        var opt = userRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var user = opt.get();

        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            user.setEmail(data.getEmail());
        }

        if (Objects.nonNull(data.getPassword()) && !"".equalsIgnoreCase(data.getPassword())) {
            user.setPassword(data.getPassword());
        }

        if (Objects.nonNull(data.getRoles()) && !data.getRoles().isEmpty()) {
            user.setRoles(data.getRoles());
        }

        if (Objects.nonNull(data.isActive())) {
            user.setActive(data.isActive());
        }

        if (Objects.nonNull(data.isLocked())) {
            user.setLocked(data.isLocked());
        }

        if (Objects.nonNull(data.isAccessAllBranches())) {
            user.setAccessAllBranches(data.isAccessAllBranches());
        }

        user.setUpdatedBy(authService.authUser());
        user.setUpdatedAt(new Date());

        try {
            userRepo.saveAndFlush(user);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            userRepo.deleteById(id);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> add(User data) throws NotFoundException {
        User existingUser = userRepo.findUserByEmail(data.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            return spotyResponseImpl.taken();
        }

        User user = new User();
        user.setEmail(data.getEmail());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        user.setRoles(data.getRoles());
        user.setActive(data.isActive());
        user.setLocked(data.isLocked());
        user.setAccessAllBranches(data.isAccessAllBranches());
        user.setCreatedBy(authService.authUser());
        user.setCreatedAt(new Date());

        try {
            userRepo.save(user);

            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
