package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    public List<User> getAll() {
        return userRepo.findAll();
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
        return userRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, User user) {
        try {
            user.setId(id);
            user.setUpdatedBy(authService.authUser());
            user.setUpdatedAt(new Date());
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
    public ResponseEntity<ObjectNode> add(User usr) throws NotFoundException {
        User existingUser = userRepo.findUserByEmail(usr.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            return spotyResponseImpl.taken();
        }

        User user = new User();
        user.setEmail(usr.getEmail());
        user.setPassword(passwordEncoder.encode(usr.getPassword()));
        user.setFirstName(usr.getFirstName());
        user.setLastName(usr.getLastName());
        user.setOtherName(usr.getOtherName());
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
