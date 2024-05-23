package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, JpaRepository<Role, Long> {
    List<Role> searchAllByNameContainingIgnoreCase(String name);
}
