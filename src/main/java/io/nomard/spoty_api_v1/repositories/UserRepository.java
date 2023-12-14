package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where concat(" +
            "trim(lower(u.email))," +
            "trim(lower(u.firstName))," +
            "trim(lower(u.lastName))," +
            "trim(lower(u.otherName))," +
            "trim(lower(u.phone))) like %:search%")
    List<User> searchAll(String search);

    User findUserByEmail(String email);
}
