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

package io.nomard.spoty_api_v1.entities.sales;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sale_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ref;

    @ManyToOne(optional = false)
    @JoinColumn(name = "saleMaster_id", nullable = false)
    private SaleMaster sale;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "product_id")
    private Product product;

    private String serialNumber;

    @Column(nullable = false)
    private double price;

    private double netTax;
    private String taxType;
    private double discount;
    private String discountType;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "created_by")
    private User createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
