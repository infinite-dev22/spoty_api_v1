package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public User getByEmail(String email) throws NotFoundException {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public List<User> getByContains(String search) {
        return userRepo.searchAll(search.toLowerCase());
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);
        user.setUpdatedBy(authService.authUser());
        user.setUpdatedAt(new Date());
        return userRepo.saveAndFlush(user);
    }

    @Override
    public String delete(Long id) {
        try {
            userRepo.deleteById(id);
            return "User successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to delete User, Contact your system administrator for assistance";
        }
    }

    @Override
    public ResponseEntity<String> save(SignUpModel signUpDetails) throws NotFoundException {
        if (!Objects.equals(signUpDetails.getPassword(), signUpDetails.getPassword2())) {
            return new ResponseEntity<>(
                    "{" +
                            "\t\"status\": \"AN ERROR OCCURRED\",\n" +
                            "\t\"message\": \"Passwords do not match\"" +
                            "}", HttpStatus.CONFLICT
            );
        }

        User existingUser = userRepo.findUserByEmail(signUpDetails.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            return new ResponseEntity<>(
                    "{" +
                            "\t\"status\": \"EMAIL TAKEN\",\n" +
                            "\t\"message\": \"Hey... looks like someone already used this email to create an account with us\"" +
                            "}", HttpStatus.CONFLICT
            );
        }

        User user = new User();
        user.setEmail(signUpDetails.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDetails.getPassword()));
        user.setFirstName(signUpDetails.getFirstName());
        user.setLastName(signUpDetails.getLastName());
        user.setOtherName(signUpDetails.getOtherName());
        user.setCreatedBy(authService.authUser());
        user.setCreatedAt(new Date());

        try {
            userRepo.save(user);

            return new ResponseEntity<>(
                    "{" +
                            "\t\"status\": \"ACCOUNT CREATED SUCCESSFULLY\",\n" +
                            "\t\"message\": \"Hooray, your account has been successfully created\"" +
                            "}", HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "{" +
                            "\t\"status\": \"INTERNAL SERVER ERROR\",\n" +
                            "\t\"message\": \"Oops... An error occurred on our side, this is not your problem\",\n" +
                            "\t\"error\": \"" + e.getMessage() + "\"" +
                            "}", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
