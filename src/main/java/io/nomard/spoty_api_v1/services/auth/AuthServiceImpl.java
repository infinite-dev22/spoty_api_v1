package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.*;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.principals.SpotyUserPrincipal;
import io.nomard.spoty_api_v1.repositories.*;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.implementations.TenantServiceImpl;
import lombok.extern.java.Log;
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
import java.util.logging.Level;

@Service
@Log
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
    private TenantSettingsRepository tenantSettingRepo;
    @Autowired
    private BranchRepository branchRepo;
    @Autowired
    private EmployeeRepository employeeRepository;
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
            var employee = employeeRepository.findByEmail(loginDetails.getEmail());
            var response = objectMapper.createObjectNode();
            response.put("token", "Bearer " + spotyTokenService.generateToken(userDetails));
            response.putPOJO("user", employee);
            response.putPOJO("role", employee.getRole());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
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
        tenant.setEmail(signUpDetails.getEmail());
        tenant.setSubscriptionEndDate(LocalDateTime.now().minusMonths(12));

        var tenantSetting = new TenantSettings();
        tenantSetting.setTenant(tenant);
        tenantSetting.setEmail(signUpDetails.getEmail());
        tenantSetting.setName(String.join(" ", signUpDetails.getFirstName(), signUpDetails.getLastName(), signUpDetails.getOtherName()));
        tenantSetting.setPhoneNumber(signUpDetails.getPhone());

        var branch = new Branch();
        branch.setName("Default Branch");
        branch.setCity("Default City");
        branch.setPhone("+123456789012");
        branch.setTown("Default Town");
        branch.setTenant(tenant);

        var account = new Account();
        account.setAccountName("Default Account");
        account.setAccountNumber("ACC000000000001");
        account.setDescription("Default account for sales, purchases, payroll, etc.");
        account.setTenant(tenant);

        var user = new User();
        user.setEmail(signUpDetails.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDetails.getPassword()));

        var employee = new Employee();
        employee.setFirstName(signUpDetails.getFirstName());
        employee.setLastName(signUpDetails.getLastName());
        employee.setOtherName(signUpDetails.getOtherName());
        employee.setPhone(signUpDetails.getPhone());
        employee.setEmail(signUpDetails.getEmail());
        employee.setTenant(tenant);
        employee.setBranch(branch);
        employee.setUser(user);
        employee.setRole(roleRepo.searchAllByNameContainingIgnoreCase("admin").getFirst());

        try {
            tenantRepo.save(tenant);
            tenantSettingRepo.save(tenantSetting);
            accountRepo.save(account);
            branchRepo.save(branch);
            userRepo.save(user);
            employeeRepository.save(employee);
            return spotyResponseImpl.created();
        } catch (Exception e) {
             log.severe(e.getMessage());
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Override
    public Employee authUser() {
        var principal = (SpotyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findByEmail(principal.getUsername());
    }
}
