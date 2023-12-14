package io.nomard.spoty_api_v1.services.auth;

import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.principals.SpotyUserPrincipal;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpotyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepo.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return new SpotyUserPrincipal(user);
    }
}