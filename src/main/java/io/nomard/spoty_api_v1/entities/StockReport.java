package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Accessors(chain = true)
@Table(name = "stock_reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class StockReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Branch.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Branch branch;
    @JoinColumn(nullable = false, name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @Column(name = "product_name")
    private String productName;
    @Column(name = "sale_price")
    private Double salePrice;
    @Column(name = "purchase_price")
    private Double purchasePrice;
    @Column(name = "purchased_quantity")
    private Integer purchasedQuantity;
    @Column(name = "sold_quantity")
    private Integer soldQuantity;
    @Column(name = "available_stock")
    private Double availableStock;
    @Column(name = "sale_stock_value")
    private String saleStockValue;  // Value of current stock at sale price.
    @Column(name = "purchase_stock_value")
    private String purchaseStockValue;  // Value of current stock at purchase price.

    @Column(name = "created_at")
    @JsonIgnore
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(name = "updated_at")
    @JsonIgnore
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;
}
