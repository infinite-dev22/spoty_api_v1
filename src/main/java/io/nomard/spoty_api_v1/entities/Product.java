package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "unit can't be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UnitOfMeasure unit;

    @NotNull(message = "category can't be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProductCategory category;

    @NotNull(message = "brand can't be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Brand brand;

    @ManyToOne(targetEntity = Branch.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Branch branch;
    @JoinColumn(nullable = false, name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;

    @Column(name = "barcode_type")
    private String barcodeType;
    @NotBlank(message = "name can't be blank")
    @Column(nullable = false)
    private String name;
    @Column
    private long quantity;
    @Column
    private double costPrice;
    @NotNull(message = "Sale price can't be blank")
    @Column(nullable = false)
    private double salePrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;
    @Column(name = "stock_alert")
    private long stockAlert;
    @Column(name = "serial_number")
    private String serialNumber;
    @Column
    private String image;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "created_by")
    private User createdBy;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
