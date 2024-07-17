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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<User>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> userRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(userRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<User> getById(Long id) {
        return userRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public Flux<List<User>> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> userRepo.searchAllByEmail(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(UserModel data) {
        return userRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> {
                    boolean updated = false;

                    var userProfile = user.getUserProfile();

                    if (!Objects.equals(user.getTenant(), data.getTenant()) && Objects.nonNull(data.getTenant())) {
                        user.setTenant(data.getTenant());
                        updated = true;
                    }

                    if (!Objects.equals(user.getBranch(), data.getBranch()) && Objects.nonNull(data.getBranch())) {
                        user.setBranch(data.getBranch());
                        updated = true;
                    }

                    if (!Objects.equals(userProfile.getFirstName(), data.getFirstName()) && Objects.nonNull(data.getFirstName()) && !"".equalsIgnoreCase(data.getFirstName())) {
                        userProfile.setFirstName(data.getFirstName());
                        updated = true;
                    }

                    if (!Objects.equals(userProfile.getLastName(), data.getLastName()) && Objects.nonNull(data.getLastName()) && !"".equalsIgnoreCase(data.getLastName())) {
                        userProfile.setLastName(data.getLastName());
                        updated = true;
                    }

                    if (!Objects.equals(userProfile.getOtherName(), data.getOtherName()) && Objects.nonNull(data.getOtherName()) && !"".equalsIgnoreCase(data.getOtherName())) {
                        userProfile.setOtherName(data.getOtherName());
                        updated = true;
                    }

                    if (!Objects.equals(user.getEmail(), data.getEmail()) && Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
                        user.setEmail(data.getEmail());
                        updated = true;
                    }

                    if (!Objects.equals(userProfile.getPhone(), data.getPhone()) && Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
                        userProfile.setPhone(data.getPhone());
                        updated = true;
                    }

                    if (!Objects.equals(user.getRole(), data.getRole()) && Objects.nonNull(data.getRole())) {
                        user.setRole(data.getRole());
                        updated = true;
                    }

                    if (!Objects.equals(user.isActive(), data.isActive())) {
                        user.setActive(data.isActive());
                        updated = true;
                    }

                    if (!Objects.equals(user.isLocked(), data.isLocked())) {
                        user.setLocked(data.isLocked());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getAvatar()) && !"".equalsIgnoreCase(data.getAvatar())) {
                        userProfile.setAvatar(data.getAvatar());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(authUser -> {
                                    user.setUpdatedBy(authUser);
                                    user.setUpdatedAt(new Date());
                                    return userRepo.save(user)
                                            .thenReturn(spotyResponseImpl.ok());
                                });
                    } else {
                        return Mono.just(spotyResponseImpl.ok());
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return userRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> add(UserModel data) {
        return userRepo.findUserByEmail(data.getEmail())
                .flatMap(existingUser -> {
                    if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
                        return Mono.just(spotyResponseImpl.taken());
                    }
                    return authService.authUser()
                            .flatMap(authUser -> {
                                User user = new User();
                                UserProfile userProfile = new UserProfile();
                                userProfile.setFirstName(data.getFirstName());
                                userProfile.setLastName(data.getLastName());
                                userProfile.setOtherName(data.getOtherName());
                                userProfile.setPhone(data.getPhone());
                                userProfile.setAvatar(data.getAvatar());
                                userProfile.setTenant(authUser.getTenant());
                                userProfile.setCreatedBy(authUser);
                                userProfile.setCreatedAt(new Date());

                                user.setUserProfile(userProfile);
                                user.setTenant(data.getTenant());
                                user.setBranch(data.getBranch());
                                user.setEmail(data.getEmail());
                                user.setPassword(new BCryptPasswordEncoder(8).encode("new_user"));
                                user.setRole(data.getRole());
                                user.setActive(data.isActive());
                                user.setLocked(data.isLocked());
                                if (Objects.isNull(user.getBranch())) {
                                    user.setBranch(authUser.getBranch());
                                }
                                user.setTenant(authUser.getTenant());
                                user.setCreatedBy(authUser);
                                user.setCreatedAt(new Date());
                                return userRepo.save(user)
                                        .thenReturn(spotyResponseImpl.created());
                            });

                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }
}
