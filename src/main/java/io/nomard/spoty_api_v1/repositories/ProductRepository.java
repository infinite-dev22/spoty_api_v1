package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.StockAlertModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveSortingRepository<Product, Long>, ReactiveCrudRepository<Product, Long> {
    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE p.tenant.id = :id " +
            "AND concat(" +
            "trim(lower(p.unit.name))," +
            "trim(lower(p.category.name))," +
            "trim(lower(p.barcodeType))," +
            "trim(lower(p.name))," +
            "trim(lower(p.brand.name))," +
            "trim(lower(p.tax.name))," +
            "trim(lower(p.discount.name))," +
            "trim(lower(p.serialNumber))) " +
            "LIKE %:search%")
    Flux<Product> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE p.tenant.id = :id " +
            "AND p.quantity <= p.stockAlert")
    Flux<Product> stockAlert(@Param("id") Long tenantId);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Products', COUNT(s)) " +
            "FROM Product s " +
            "WHERE s.tenant.id = :id")
    Mono<DashboardKPIModel> countProducts(@Param("id") Long tenantId);

    @Query("SELECT p FROM Product p WHERE p.tenant.id = :id")
    Flux<Product> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.StockAlertModel(p.name, p.quantity, p.costPrice) " +
            "FROM Product p " +
            "WHERE p.tenant.id = :id " +
            "AND p.quantity <= p.stockAlert")
    Flux<StockAlertModel> productsStockAlert(@Param("id") Long tenantId);
}
