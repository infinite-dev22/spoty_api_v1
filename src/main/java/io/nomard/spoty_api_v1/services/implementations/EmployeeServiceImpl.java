package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.PasswordChangeModel;
import io.nomard.spoty_api_v1.models.UserModel;
import io.nomard.spoty_api_v1.repositories.EmployeeRepository;
import io.nomard.spoty_api_v1.repositories.RoleRepository;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.DepartmentRepository;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.DesignationRepository;
import io.nomard.spoty_api_v1.repositories.hrm.hrm.EmploymentStatusRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.EmployeeService;
import io.nomard.spoty_api_v1.templates.emails.EmploymentEmail;
import jakarta.mail.MessagingException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@Service
@Log
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private DesignationRepository designationRepo;

    @Autowired
    private EmploymentStatusRepository employmentStatusRepo;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private TenantSettingsServiceImpl settingsService;

    @Override
    public Page<Employee> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return employeeRepo.findByEmail(
                authService.authUser().getTenant().getId(),
                pageRequest
        );
    }

    @Override
    public Employee getById(Long id) throws NotFoundException {
        Optional<Employee> employee = employeeRepo.findById(id);
        if (employee.isEmpty()) {
            throw new NotFoundException();
        }
        return employee.get();
    }

    @Override
    public Employee getByEmail(String email) {
        return employeeRepo.findByEmail(email);
    }

    @Override
    public ArrayList<Employee> getByContains(String search) {
        return employeeRepo.searchEmployee(
                authService.authUser().getTenant().getId(),
                search.toLowerCase()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(UserModel data)
            throws NotFoundException {
        var opt = employeeRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var employee = opt.get();
        var user = employee.getUser();

        if (
                !Objects.equals(employee.getTenant(), data.getTenant()) &&
                        Objects.nonNull(data.getTenant())
        ) {
            employee.setTenant(data.getTenant());
        }

        if (
                !Objects.equals(employee.getBranch(), data.getBranch()) &&
                        Objects.nonNull(data.getBranch())
        ) {
            employee.setBranch(data.getBranch());
        }

        if (
                !Objects.equals(employee.getDepartment(), data.getDepartment()) &&
                        Objects.nonNull(data.getDepartment())
        ) {
            employee.setDepartment(data.getDepartment());
        }

        if (
                !Objects.equals(employee.getDesignation(), data.getDesignation()) &&
                        Objects.nonNull(data.getDesignation())
        ) {
            employee.setDesignation(data.getDesignation());
        }

        if (
                !Objects.equals(employee.getFirstName(), data.getFirstName()) &&
                        Objects.nonNull(data.getFirstName()) &&
                        !"".equalsIgnoreCase(data.getFirstName())
        ) {
            employee.setFirstName(data.getFirstName());
        }

        if (
                !Objects.equals(employee.getLastName(), data.getLastName()) &&
                        Objects.nonNull(data.getLastName()) &&
                        !"".equalsIgnoreCase(data.getLastName())
        ) {
            employee.setLastName(data.getLastName());
        }

        if (
                !Objects.equals(employee.getOtherName(), data.getOtherName()) &&
                        Objects.nonNull(data.getOtherName()) &&
                        !"".equalsIgnoreCase(data.getOtherName())
        ) {
            employee.setOtherName(data.getOtherName());
        }

        if (
                !Objects.equals(employee.getEmail(), data.getEmail()) &&
                        Objects.nonNull(data.getEmail()) &&
                        !"".equalsIgnoreCase(data.getEmail())
        ) {
            employee.setEmail(data.getEmail());
            user.setEmail(data.getEmail());
        }

        if (
                !Objects.equals(employee.getSalary(), data.getSalary()) &&
                        Objects.nonNull(data.getSalary()) &&
                        !"".equalsIgnoreCase(data.getSalary())
        ) {
            employee.setSalary(data.getSalary());
        }

        if (
                !Objects.equals(employee.getPhone(), data.getPhone()) &&
                        Objects.nonNull(data.getPhone()) &&
                        !"".equalsIgnoreCase(data.getPhone())
        ) {
            employee.setPhone(data.getPhone());
        }

        if (
                !Objects.equals(employee.getRole(), data.getRole()) &&
                        Objects.nonNull(data.getRole())
        ) {
            employee.setRole(data.getRole());
        }

        if (
                !Objects.equals(
                        employee.getEmploymentStatus(),
                        data.getEmploymentStatus()
                ) &&
                        Objects.nonNull(data.getEmploymentStatus())
        ) {
            employee.setEmploymentStatus(data.getEmploymentStatus());
        }

        if (!Objects.equals(employee.isActive(), data.isActive())) {
            employee.setActive(data.isActive());
        }

        if (!Objects.equals(employee.isLocked(), data.isLocked())) {
            employee.setLocked(data.isLocked());
        }

        if (
                Objects.nonNull(data.getAvatar()) &&
                        !"".equalsIgnoreCase(data.getAvatar())
        ) {
            employee.setAvatar(data.getAvatar());
        }

        employee.setUpdatedBy(authService.authUser());
        employee.setUpdatedAt(LocalDateTime.now());

        try {
            employeeRepo.save(employee);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    public ResponseEntity<ObjectNode> changePassword(PasswordChangeModel data)
            throws NotFoundException {
        if (!Objects.equals(data.getPassword(), data.getConfirmPassword())) {
            return spotyResponseImpl.custom(
                    HttpStatus.CONFLICT,
                    "Passwords do not match"
            );
        }

        var opt = Optional.ofNullable(employeeRepo.findByEmail(data.getEmail()));

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var employee = opt.get();
        var user = employee.getUser();

        if (Objects.nonNull(data.getPassword())) {
            user.setPassword(data.getPassword());
        }

        employee.setUpdatedBy(authService.authUser());
        employee.setUpdatedAt(LocalDateTime.now());

        try {
            employeeRepo.save(employee);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            employeeRepo.deleteById(id);

            return spotyResponseImpl.ok();
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> add(UserModel data)
            throws NotFoundException, MessagingException {
        Employee existingUser = employeeRepo.findByEmail(data.getEmail());

        if (
                existingUser != null &&
                        existingUser.getEmail() != null &&
                        !existingUser.getEmail().isEmpty()
        ) {
            return spotyResponseImpl.taken();
        }

        var roleOpt = roleRepo.findById(data.getRole().getId());
        if (roleOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var departmentOpt = departmentRepo.findById(data.getRole().getId());
        if (departmentOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var designationOpt = designationRepo.findById(data.getRole().getId());
        if (designationOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var employmentStatusOpt = employmentStatusRepo.findById(
                data.getRole().getId()
        );
        if (employmentStatusOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var user = new User();
        user.setEmail(data.getEmail());
        var password = UUID.randomUUID().toString().substring(0, 12);
        user.setPassword(new BCryptPasswordEncoder(8).encode(password));
        user.setUserType("Employee");
        user.setCreatedBy(authService.authUser());
        user.setCreatedAt(LocalDateTime.now());

        var employee = new Employee();
        employee.setUser(user);
        employee.setFirstName(data.getFirstName());
        employee.setLastName(data.getLastName());
        employee.setOtherName(data.getOtherName());
        employee.setPhone(data.getPhone());
        employee.setAvatar(data.getAvatar());
        employee.setEmail(data.getEmail());
        employee.setSalary(data.getSalary());
        employee.setRole(roleOpt.get());
        employee.setDepartment(departmentOpt.get());
        employee.setDesignation(designationOpt.get());
        employee.setEmploymentStatus(employmentStatusOpt.get());
        employee.setActive(true);
        employee.setLocked(false);
        employee.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(employee.getBranch())) {
            employee.setBranch(authService.authUser().getBranch());
        }
        employee.setCreatedBy(authService.authUser());
        employee.setCreatedAt(LocalDateTime.now());

        try {
            employeeRepo.save(employee);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return spotyResponseImpl.custom(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }

        var content = new EmploymentEmail(employee, password, settingsService.getSettings().getHrEmail()).getTemplate();

        // Asynchronous email sending

        emailServiceImpl.sendSimpleMessage(
                settingsService.getSettings().getHrEmail(),
                employee.getEmail(),
                "Employment Letter & Work Details",
                content
        );
        emailServiceImpl.sendMessageWithAttachment(
                settingsService.getSettings().getHrEmail(),
                employee.getEmail(),
                "Employment Letter & Work Details",
                content,
                "/home/infinite/Documents/Job_Search/Resume_Jonathan_Mark_Mwigo.pdf"
        );

        return spotyResponseImpl.created();
    }
}
