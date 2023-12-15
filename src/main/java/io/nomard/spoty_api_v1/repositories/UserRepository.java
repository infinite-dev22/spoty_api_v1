package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> searchAllByEmailContainingIgnoreCase(String email);

    User findUserByEmail(String email);
}
