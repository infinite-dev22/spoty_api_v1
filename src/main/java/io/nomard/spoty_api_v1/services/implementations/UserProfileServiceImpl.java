package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UserProfile;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
import io.nomard.spoty_api_v1.repositories.UserProfileRepository;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserProfileRepository userProfileRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<UserProfile> getAll() {
        return userProfileRepo.findAll();
    }

    @Override
    public UserProfile getById(Long id) throws NotFoundException {
        Optional<UserProfile> user = userProfileRepo.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        return user.get();
    }

    @Override
    public UserProfile getByEmail(String email) {
        return userProfileRepo.findUserProfileByEmail(email);
    }

    @Override
    public List<UserProfile> getByContains(String search) {
        return userProfileRepo.searchAllByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrOtherNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> update(UserModel data) throws NotFoundException {
        var opt = userProfileRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }

        var userProfile = opt.get();
        var user = userRepo.findUserByEmail(data.getEmail());

        if (Objects.nonNull(data.getFirstName()) && !"".equalsIgnoreCase(data.getFirstName())) {
            userProfile.setFirstName(data.getFirstName());
        }

        if (Objects.nonNull(data.getLastName()) && !"".equalsIgnoreCase(data.getLastName())) {
            userProfile.setLastName(data.getLastName());
        }

        if (Objects.nonNull(data.getOtherName()) && !"".equalsIgnoreCase(data.getOtherName())) {
            userProfile.setOtherName(data.getOtherName());
        }

        if (Objects.nonNull(data.getAvatar()) && !"".equalsIgnoreCase(data.getAvatar())) {
            userProfile.setAvatar(data.getAvatar());
        }

        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            userProfile.setEmail(data.getEmail());
            user.setEmail(data.getEmail());
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

        userProfile.setUpdatedBy(authService.authUser());
        userProfile.setUpdatedAt(new Date());

        try {
            userProfileRepo.saveAndFlush(userProfile);
            userRepo.saveAndFlush(user);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            userProfileRepo.deleteById(id);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> add(UserProfile userProfile) {

        userProfile.setCreatedBy(authService.authUser());
        userProfile.setCreatedAt(new Date());

        try {
            userProfileRepo.save(userProfile);

            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
