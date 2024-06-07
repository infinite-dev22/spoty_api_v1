package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.entities.UserProfile;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

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
        Page<User> page = userRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest);
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
    @Transactional
    public ResponseEntity<ObjectNode> update(UserModel data) throws NotFoundException {
        var opt = userRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var user = opt.get();
        var userProfile = user.getUserProfile();

        if (!Objects.equals(user.getTenant(), data.getTenant()) && Objects.nonNull(data.getTenant())) {
            user.setTenant(data.getTenant());
        }

        if (!Objects.equals(user.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
            user.setBranch(data.getBranch());
        }

        if (!Objects.equals(userProfile.getFirstName(), data.getFirstName()) && Objects.nonNull(data.getFirstName()) && !"".equalsIgnoreCase(data.getFirstName())) {
            userProfile.setFirstName(data.getFirstName());
        }

        if (!Objects.equals(userProfile.getLastName(), data.getLastName()) && Objects.nonNull(data.getLastName()) && !"".equalsIgnoreCase(data.getLastName())) {
            userProfile.setLastName(data.getLastName());
        }

        if (!Objects.equals(userProfile.getOtherName(), data.getOtherName()) && Objects.nonNull(data.getOtherName()) && !"".equalsIgnoreCase(data.getOtherName())) {
            userProfile.setOtherName(data.getOtherName());
        }

        if (!Objects.equals(user.getEmail(), data.getEmail()) && Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            user.setEmail(data.getEmail());
        }

        if (!Objects.equals(userProfile.getPhone(), data.getPhone()) && Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
            userProfile.setPhone(data.getPhone());
        }

        if (!Objects.equals(user.getRole(), data.getRole()) && Objects.nonNull(data.getRole())) {
            user.setRole(data.getRole());
        }

        if (!Objects.equals(user.isActive(), data.isActive())) {
            user.setActive(data.isActive());
        }

        if (!Objects.equals(user.isLocked(), data.isLocked())) {
            user.setLocked(data.isLocked());
        }

        if (Objects.nonNull(data.getAvatar()) && !"".equalsIgnoreCase(data.getAvatar())) {
            userProfile.setAvatar(data.getAvatar());
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
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            userRepo.deleteById(id);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> add(UserModel data) throws NotFoundException {
        User existingUser = userRepo.findUserByEmail(data.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            return spotyResponseImpl.taken();
        }

        User user = new User();
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(data.getFirstName());
        userProfile.setLastName(data.getLastName());
        userProfile.setOtherName(data.getOtherName());
        userProfile.setPhone(data.getPhone());
        userProfile.setAvatar(data.getAvatar());
        userProfile.setTenant(authService.authUser().getTenant());
        userProfile.setCreatedBy(authService.authUser());
        userProfile.setCreatedAt(new Date());

        user.setUserProfile(userProfile);
        user.setTenant(data.getTenant());
        user.setBranch(data.getBranch());
        user.setEmail(data.getEmail());
        user.setPassword(passwordEncoder.encode("new_user"));
        user.setRole(data.getRole());
        user.setActive(data.isActive());
        user.setLocked(data.isLocked());
        if (Objects.isNull(user.getBranch())) {
            user.setBranch(authService.authUser().getBranch());
        }
        user.setTenant(authService.authUser().getTenant());
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
