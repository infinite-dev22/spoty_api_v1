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

package io.nomard.spoty_api_v1.entities.returns.sale_returns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "sale_return_masters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class SaleReturnMaster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user_detail;

    @Column(nullable = false)
    private Date date;

    private String ref;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "customer_id")
    private Customer customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    @JoinColumn(nullable = false, name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @OneToMany(orphanRemoval = true, mappedBy = "saleReturn", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SaleReturnDetail> saleReturnDetails;

    private double taxRate;
    private double netTax;
    private double discount;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private double paid;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private String status;

    private String notes;

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
