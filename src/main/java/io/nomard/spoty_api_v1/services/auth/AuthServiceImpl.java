package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.entities.UserProfile;
import io.nomard.spoty_api_v1.entities.accounting.Account;
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
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    private ReactiveAuthenticationManager reactiveAuthenticationManager;
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
    public Mono<ResponseEntity<ObjectNode>> login(LoginModel loginDetails) {
        return spotyUserDetailsService.findByUsername(loginDetails.getEmail())
                .flatMap(userDetails -> userRepo.findUserByEmail(loginDetails.getEmail())
                        .flatMap(user -> reactiveAuthenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                loginDetails.getEmail(),
                                                loginDetails.getPassword()
                                        ))
                                .flatMap(authentication -> {
                                    SecurityContextHolder.getContext().setAuthentication(authentication);

                                    Mono<Date> subscriptionEndDateMono = tenantService.getSubscriptionEndDate(user.getTenant().getId());
                                    Mono<Date> trialEndDateMono = tenantService.getTrialEndDate(user.getTenant().getId());
                                    Mono<Boolean> isTrialMono = tenantService.isTrial(user.getTenant().getId());
                                    Mono<Boolean> canTryMono = tenantService.canTry(user.getTenant().getId());
                                    Mono<Boolean> isNewTenancyMono = tenantService.isNewTenancy(user.getTenant().getId());

                                    return Mono.zip(subscriptionEndDateMono, trialEndDateMono, isTrialMono, canTryMono, isNewTenancyMono)
                                            .flatMap(tuple -> {
                                                Date subscriptionEndDate = tuple.getT1();
                                                Date trialEndDate = tuple.getT2();
                                                boolean isTrial = tuple.getT3();
                                                boolean canTry = tuple.getT4();
                                                boolean isNewTenancy = tuple.getT5();

                                                LocalDateTime subscriptionEndDateTime = LocalDateTime.ofInstant(subscriptionEndDate.toInstant(), ZoneId.systemDefault());
                                                LocalDateTime now = LocalDateTime.now();
                                                LocalDateTime gracePeriodEnd = subscriptionEndDateTime.plusDays(getGracePeriodDays());
                                                LocalDateTime subscriptionWarningDate = subscriptionEndDateTime.minusDays(getGracePeriodDays());

                                                ObjectNode response = objectMapper.createObjectNode();
                                                response.put("status", 200);
                                                response.put("trial", isTrial);
                                                response.put("canTry", canTry);
                                                response.put("newTenancy", isNewTenancy);
                                                response.put("activeTenancy", subscriptionEndDateTime.isAfter(now));
                                                response.put("activeTenancyWarning", subscriptionEndDateTime.isAfter(now) && subscriptionEndDateTime.isBefore(subscriptionWarningDate.plusDays(getGracePeriodDays())));
                                                response.put("inActiveTenancyWarning", subscriptionEndDateTime.isBefore(now) && now.isBefore(gracePeriodEnd));
                                                response.put("token", "Bearer " + spotyTokenService.generateToken(userDetails));
                                                response.putPOJO("user", user);

                                                if (isTrial && trialEndDate.before(new Date())) {
                                                    response.put("message", "Subscription required");
                                                } else if (subscriptionEndDateTime.isBefore(now)) {
                                                    if (now.isBefore(subscriptionWarningDate)) {
                                                        response.put("message", "Subscription required");
                                                    } else if (now.isBefore(gracePeriodEnd)) {
                                                        response.put("message", "Subscription expired, please renew");
                                                    }
                                                } else if (subscriptionEndDateTime.isBefore(subscriptionWarningDate)) {
                                                    response.put("message", "Subscription is about to expire, please renew");
                                                } else {
                                                    response.put("message", "Process successfully completed");
                                                }

                                                return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
                                            });
                                })
                        )
                );
    }


    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> register(SignUpModel signUpDetails) {
        return userRepo.findUserByEmail(signUpDetails.getEmail())
                .flatMap(existingUser -> {
                    if (!Objects.equals(signUpDetails.getPassword(), signUpDetails.getConfirmPassword())) {
                        return Mono.just(spotyResponseImpl.custom(HttpStatus.CONFLICT, "Passwords do not match"));
                    }

                    if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
                        return Mono.just(spotyResponseImpl.taken());
                    }

                    var tenant = new Tenant();
                    tenant.setName(String.join(" ", signUpDetails.getFirstName(), signUpDetails.getLastName(), signUpDetails.getOtherName()));
                    tenant.setSubscriptionEndDate(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    tenant.setTrialEndDate(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));


                    return tenantRepo.save(tenant)
                            .flatMap(savedTenant -> {
                                var branch = new Branch();
                                branch.setName("Default Branch");
                                branch.setCity("Default City");
                                branch.setPhone("+123456789012");
                                branch.setTown("Default Town");
                                branch.setTenant(tenant);
                                return branchRepo.save(branch)
                                        .flatMap(savedBranch -> {
                                            var account = new Account();
                                            account.setAccountName("Default Account");
                                            account.setAccountNumber("ACC000000000001");
                                            account.setBalance(0d);
                                            account.setDescription("Default account for sales, purchases, payroll, etc.");
                                            account.setTenant(tenant);
                                            return accountRepo.save(account)
                                                    .flatMap(savedAccount -> {
                                                        var userProfile = new UserProfile();
                                                        userProfile.setFirstName(signUpDetails.getFirstName());
                                                        userProfile.setLastName(signUpDetails.getLastName());
                                                        userProfile.setOtherName(signUpDetails.getOtherName());
                                                        userProfile.setPhone(signUpDetails.getPhone());
                                                        userProfile.setTenant(tenant);
                                                        return userProfileRepo.save(userProfile)
                                                                .flatMap(savedUserProfile -> roleRepo.search("admin")
                                                                        .flatMap(role -> {
                                                                            var user = new User();
                                                                            user.setUserProfile(userProfile);
                                                                            user.setTenant(tenant);
                                                                            user.setBranch(branch);
                                                                            user.setEmail(signUpDetails.getEmail());
                                                                            user.setPassword(passwordEncoder.encode(signUpDetails.getPassword()));
                                                                            user.setRole(role);
                                                                            return userRepo.save(user)
                                                                                    .thenReturn(spotyResponseImpl.created());
                                                                        }).then()
                                                                        .thenReturn(spotyResponseImpl.created()));
                                                    })
                                                    .thenReturn(spotyResponseImpl.created());
                                        })
                                        .thenReturn(spotyResponseImpl.created());
                            })
                            .thenReturn(spotyResponseImpl.created());
                })
                .thenReturn(spotyResponseImpl.created());
    }

    @Override
    public Mono<User> authUser() {
        var principal = (SpotyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findUserByEmail(principal.getUsername());
    }

    private int getGracePeriodDays() {
        return 7; // Retrieve grace period duration FROM configuration
    }
}
