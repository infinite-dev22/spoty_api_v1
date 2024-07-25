package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.entities.UserProfile;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.principals.SpotyUserPrincipal;
import io.nomard.spoty_api_v1.repositories.*;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
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

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SpotyUserDetailsService spotyUserDetailsService;
    @Autowired
    private SpotyTokenService spotyTokenService;
    @Autowired
    private TenantServiceImpl tenantService;
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
    @Autowired
    private AccountRepository accountRepo;

    @Override
    public ResponseEntity<ObjectNode> login(LoginModel loginDetails) throws NotFoundException {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDetails.getEmail(),
                            loginDetails.getPassword()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            var userDetails = spotyUserDetailsService.loadUserByUsername(loginDetails.getEmail());
            var user = userRepo.findUserByEmail(loginDetails.getEmail());
            var tenantId = user.getTenant().getId();
            var now = LocalDateTime.now();
            var gracePeriodEnd = tenantService.getSubscriptionEndDate(tenantId).plusDays(getGracePeriodDays());
            var subscriptionWarningDate = tenantService.getSubscriptionEndDate(tenantId).minusDays(getGracePeriodDays());

            boolean trial = tenantService.isTrial(tenantId);
            boolean canTry = tenantService.canTry(tenantId);
            boolean newTenancy = tenantService.isNewTenancy(tenantId);
            boolean activeTenancy = tenantService.getSubscriptionEndDate(tenantId).isAfter(now);
            boolean activeTenancyWarning = tenantService.getSubscriptionEndDate(tenantId).isAfter(now) && tenantService.getSubscriptionEndDate(tenantId).isBefore(subscriptionWarningDate.plusDays(getGracePeriodDays()));
            boolean inActiveTenancyWarning = tenantService.getSubscriptionEndDate(tenantId).isBefore(now) && now.isBefore(gracePeriodEnd);

            var response = objectMapper.createObjectNode();
            response.put("status", 200);
            response.put("trial", trial);
            response.put("canTry", canTry);
            response.put("newTenancy", newTenancy);
            response.put("activeTenancy", activeTenancy);
            response.put("activeTenancyWarning", activeTenancyWarning);
            response.put("inActiveTenancyWarning", inActiveTenancyWarning);
            response.put("token", "Bearer " + spotyTokenService.generateToken(userDetails));
            response.putPOJO("user", user);

            if (trial && tenantService.getTrialEndDate(tenantId).isBefore(LocalDateTime.now())) {
                response.put("message", "Subscription required");
            } else if (tenantService.getSubscriptionEndDate(tenantId).isBefore(now)) {
                if (now.isBefore(subscriptionWarningDate)) {
                    response.put("message", "Subscription required");
                } else if (now.isBefore(gracePeriodEnd)) {
                    response.put("message", "Subscription expired, please renew");
                }
            } else if (tenantService.getSubscriptionEndDate(tenantId).isBefore(subscriptionWarningDate)) {
                response.put("message", "Subscription is about to expire, please renew");
            } else {
                response.put("message", "Process successfully completed");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        tenant.setName(String.join(" ", signUpDetails.getFirstName(), signUpDetails.getLastName(), signUpDetails.getOtherName()));
        tenant.setSubscriptionEndDate(LocalDateTime.now().minusMonths(12));
        tenant.setTrialEndDate(LocalDateTime.now().minusMonths(12));

        var branch = new Branch();
        branch.setName("Default Branch");
        branch.setCity("Default City");
        branch.setPhone("+123456789012");
        branch.setTown("Default Town");
        branch.setTenant(tenant);

        var account = new Account();
        account.setAccountName("Default Account");
        account.setAccountNumber("ACC000000000001");
        account.setBalance(0d);
        account.setDescription("Default account for sales, purchases, payroll, etc.");
        account.setTenant(tenant);

        var userProfile = new UserProfile();
        userProfile.setFirstName(signUpDetails.getFirstName());
        userProfile.setLastName(signUpDetails.getLastName());
        userProfile.setOtherName(signUpDetails.getOtherName());
        userProfile.setPhone(signUpDetails.getPhone());
        userProfile.setTenant(tenant);

        var user = new User();
        user.setUserProfile(userProfile);
        user.setTenant(tenant);
        user.setBranch(branch);
        user.setEmail(signUpDetails.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDetails.getPassword()));
        user.setRole(roleRepo.searchAllByNameContainingIgnoreCase("admin").get(0));

        try {
            tenantRepo.saveAndFlush(tenant);
            accountRepo.saveAndFlush(account);
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
        return 7; // Retrieve grace period duration from configuration
    }
}
