package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    @Query("select c from Currency c where concat(" +
            "trim(lower(c.name))," +
            "trim(lower(c.name))," +
            "trim(lower(c.symbol))) like %:search%")
    List<Currency> searchAll(String search);
}
