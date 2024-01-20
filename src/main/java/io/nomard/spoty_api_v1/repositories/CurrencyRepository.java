package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends PagingAndSortingRepository<Currency, Long>, JpaRepository<Currency, Long> {
    List<Currency> searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrSymbolContainingIgnoreCase(String name, String code, String symbol);
}
