package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, Long>, JpaRepository<UserProfile, Long> {
    List<UserProfile> searchAllByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrOtherNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(String email, String firstName, String lastName, String otherName, String phone);

    UserProfile findUserProfileByEmail(String email);
}
