package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ZenService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZenServiceRepository extends JpaRepository<ZenService, Long> {
}
