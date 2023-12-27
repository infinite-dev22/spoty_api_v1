package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "stock_reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Branch branch;

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

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(name = "updated_at")
    @JsonIgnore
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;
}
