package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.entities.UserProfile;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.principals.SpotyUserPrincipal;
import io.nomard.spoty_api_v1.repositories.*;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Date;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    public SpotyUserDetailsService spotyUserDetailsService;
    @Autowired
    public SpotyTokenService spotyTokenService;
    @Autowired
    public TenantServiceImpl tenantService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantRepository tenantRepo;
    @Autowired
    private BranchRepository branchRepo;
    @Autowired
    private UserProfileRepository userProfileRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<ObjectNode> login(LoginModel loginDetails) throws NotFoundException {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDetails.getEmail(),
                            loginDetails.getPassword()
                    ));
            final var userDetails = spotyUserDetailsService.loadUserByUsername(loginDetails.getEmail());
            // Set authenticated user into context.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Get subscription end date.
            var subscriptionEndDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(tenantService.getSubscriptionEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).getTime()), ZoneId.systemDefault());
            var gracePeriodEnd = subscriptionEndDate.plusDays(getGracePeriodDays());
            var subscriptionWarningDate = subscriptionEndDate.minusDays(getGracePeriodDays());
            var response = objectMapper.createObjectNode();
            response.put("trial", tenantService.isTrial(userRepo.findUserByEmail(loginDetails.getEmail()).getId()));
            response.put("canTry", tenantService.canTry(userRepo.findUserByEmail(loginDetails.getEmail()).getId()));
            response.put("newTenancy", tenantService.isNewTenancy(userRepo.findUserByEmail(loginDetails.getEmail()).getId()));
            response.put("activeTenancy", Date.from(subscriptionEndDate.toInstant(ZoneOffset.UTC)).before(Date.from(subscriptionEndDate.plusDays(getGracePeriodDays()).toInstant(ZoneOffset.UTC))));
            // Check if trial is expired.
            if (tenantService.isTrial(userRepo.findUserByEmail(loginDetails.getEmail()).getId()) && tenantService.getTrialEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).before(new Date())) {
                response.put("status", 200);
                response.put("message", "Subscription required");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            // Check if subscription is expired.
            if (tenantService.getSubscriptionEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).before(new Date(subscriptionWarningDate.toEpochSecond(ZoneOffset.UTC)))) {
                response.put("status", 200);
                response.put("message", "Subscription required");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            response.put("status", 200);
            response.put("token", "Bearer " + spotyTokenService.generateToken(userDetails));
            response.putPOJO("user", userRepo.findUserByEmail(loginDetails.getEmail()));
            // Check if subscription is about to expire(7 days before).
            if (tenantService.getSubscriptionEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).before(new Date(subscriptionWarningDate.toEpochSecond(ZoneOffset.UTC)))
                    && !tenantService.getSubscriptionEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).before(new Date())) {
                // send email warning about days left for subscription to expire.
                response.put("message", "Subscription is about to expire, please renew");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            // Check if subscription has expired but in grace period(7 days after).
            if (tenantService.getSubscriptionEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).before(new Date())
                    && !tenantService.getSubscriptionEndDate(userRepo.findUserByEmail(loginDetails.getEmail()).getId()).before(new Date(gracePeriodEnd.toEpochSecond(ZoneOffset.UTC)))) {
                // send email warning about days left for account to be locked.
                response.put("message", "Subscription expired, please renew");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.put("message", "Process successfully completed");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
//            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> register(SignUpModel signUpDetails) {
        if (!Objects.equals(signUpDetails.getPassword(), signUpDetails.getConfirmPassword())) {
            return spotyResponseImpl.custom(HttpStatus.CONFLICT, "Passwords do not match");
        }
        var existingUser = userRepo.findUserByEmail(signUpDetails.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            return spotyResponseImpl.taken();
        }
        var tenant = new Tenant();
        tenant.setName(signUpDetails.getFirstName() + " " + signUpDetails.getLastName() + " " + signUpDetails.getOtherName());
//        tenant.setSubscriptionEndDate(new Date(LocalDate.now().minusMonths(1).toEpochDay()));
        tenant.setSubscriptionEndDate(new Date(LocalDate.now().minusMonths(1).toEpochDay()));
        tenant.setTrialEndDate(new Date(LocalDate.now().minusMonths(1).toEpochDay()));

        var branch = new Branch();
        branch.setName("Default Branch");
        branch.setCity("Default City");
        branch.setPhone("+123456789012");
        branch.setTown("Default Town");
        branch.setTenant(tenant);

        var user = new User();
        var userProfile = new UserProfile();
        userProfile.setFirstName(signUpDetails.getFirstName());
        userProfile.setLastName(signUpDetails.getLastName());
        userProfile.setOtherName(signUpDetails.getOtherName());
        userProfile.setPhone(signUpDetails.getPhone());
        userProfile.setTenant(tenant);

        user.setUserProfile(userProfile);
        user.setTenant(tenant);
        user.setBranch(branch);
        user.setEmail(signUpDetails.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDetails.getPassword()));
        user.setRole(roleRepo.searchAllByNameContainingIgnoreCase("admin").get(0));
        user.setTenant(tenant);

        try {
            tenantRepo.saveAndFlush(tenant);
            branchRepo.saveAndFlush(branch);
            userProfileRepo.saveAndFlush(userProfile);
            userRepo.save(user);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public User authUser() {
        var principal = (SpotyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepo.findUserByEmail(principal.getUsername());
    }

    private int getGracePeriodDays() {
        // Retrieve grace period duration from configuration
        return 7;
    }
}
