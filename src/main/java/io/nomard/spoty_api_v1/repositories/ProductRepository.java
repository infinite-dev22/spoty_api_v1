package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, JpaRepository<Product, Long> {
    @Query("select p from Product p where concat(" +
            "trim(lower(p.unit.name))," +
            "trim(lower(p.category.name))," +
            "trim(lower(p.barcodeType))," +
            "trim(lower(p.name))," +
            "trim(lower(p.brand.name))," +
            "trim(lower(p.tax.name))," +
            "trim(lower(p.discount.name))," +
            "trim(lower(p.serialNumber))) like %:search%")
    List<Product> searchAll(@Param("search") String search);

    @Query("select p from Product p where p.quantity <= p.stockAlert")
    List<Product> findAllByTenantIdByQuantityIsLessThanEqualStockAlert();

    @Query("select p from Product p where p.tenant.id = :id")
    Page<Product> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}
