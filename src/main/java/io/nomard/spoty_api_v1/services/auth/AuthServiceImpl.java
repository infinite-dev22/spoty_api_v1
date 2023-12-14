package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.AuthenticationResponse;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.principals.SpotyUserPrincipal;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    public UserDetailsService userDetailsService;
    @Autowired
    public SpotyTokenService spotyTokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public AuthenticationResponse login(LoginModel loginDetails) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDetails.getEmail(),
                            loginDetails.getPassword()
                    ));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDetails.getEmail());
            final AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setAccessToken(spotyTokenService.generateToken(userDetails));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return authenticationResponse;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "{" +
                    "\t\"status\": \"INTERNAL SERVER ERROR\",\n" +
                    "\t\"message\": \"Oops... An error occurred on our side, this is not your problem\",\n" +
                    "\t\"error\": \"" + e.getMessage() + "\"" +
                    "}"
            );
        }
    }

    @Override
    public ResponseEntity<ObjectNode> register(SignUpModel signUpDetails) throws NotFoundException {
        if (!Objects.equals(signUpDetails.getPassword(), signUpDetails.getPassword2())) {
            return spotyResponseImpl.custom(HttpStatus.CONFLICT, "Passwords do not match");
        }

        User existingUser = userRepo.findUserByEmail(signUpDetails.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            return spotyResponseImpl.taken();
        }

        User user = new User();
        user.setEmail(signUpDetails.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDetails.getPassword()));
        user.setFirstName(signUpDetails.getFirstName());
        user.setLastName(signUpDetails.getLastName());
        user.setOtherName(signUpDetails.getOtherName());
        user.setCreatedAt(new Date());

        try {
            userRepo.save(user);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public User authUser() {
        SpotyUserPrincipal principal = (SpotyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepo.findUserByEmail(principal.getUsername());
    }
}
