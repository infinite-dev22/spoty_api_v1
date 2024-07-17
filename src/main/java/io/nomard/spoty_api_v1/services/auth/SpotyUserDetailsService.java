package io.nomard.spoty_api_v1.services.auth;

import io.nomard.spoty_api_v1.principals.SpotyUserPrincipal;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SpotyUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findUserByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)))
                .map(SpotyUserPrincipal::new);
    }
}