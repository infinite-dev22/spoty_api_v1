/*
 * Copyright (c) 2023, Jonathan Mark Mwigo. All rights reserved.
 *
 * The computer system code contained in this file is the property of Jonathan Mark Mwigo and is protected by copyright law. Any unauthorized use of this code is prohibited.
 *
 * This copyright notice applies to all parts of the computer system code, including the source code, object code, and any other related materials.
 *
 * The computer system code may not be modified, translated, or reverse-engineered without the express written permission of Jonathan Mark Mwigo.
 *
 * Jonathan Mark Mwigo reserves the right to update, modify, or discontinue the computer system code at any time.
 *
 * Jonathan Mark Mwigo makes no warranties, express or implied, with respect to the computer system code. Jonathan Mark Mwigo shall not be liable for any damages, including, but not limited to, direct, indirect, incidental, special, consequential, or punitive damages, arising out of or in connection with the use of the computer system code.
 */

package io.nomard.spoty_api_v1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "unit can't be null")
    @ManyToOne(optional = false)
    private UnitOfMeasure unit;

    @NotNull(message = "category can't be null")
    @ManyToOne(optional = false)
    private ProductCategory category;

    @NotNull(message = "brand can't be null")
    @ManyToOne(optional = false)
    private Brand brand;

    @ManyToOne(targetEntity = Branch.class)
    private Branch branch;

    @Column(name = "barcode_type")
    private String barcodeType;

    @Column(name = "product_type")
    private String productType;

    @NotBlank(message = "name can't be blank")
    @Column(nullable = false)
    private String name;
    @Column
    private long quantity;
    @Column
    private double cost;
    @NotNull(message = "price can't be blank")
    @Column(nullable = false)
    private double price;
    @Column
    private double discount;

    @Column(name = "net_tax")
    private double netTax;

    @Column(name = "tax_type")
    private String taxType;

    @Column(name = "stock_alert")
    private long stockAlert;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column
    private String image;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "created_by")
    private User createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
