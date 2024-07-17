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

package io.nomard.spoty_api_v1.entities.requisitions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

@Entity
@Accessors(chain = true)
@Table(name = "requisition_masters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequisitionMaster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ref;

    @Column(nullable = false)
    private Date date;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    @JoinColumn(nullable = false, name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @OneToMany(orphanRemoval = true, mappedBy = "requisition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<RequisitionDetail> requisitionDetails = new LinkedList<>();

    private String shipVia;
    private String shipMethod;
    private String shippingTerms;
    private Date deliveryDate;
    private String notes;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private double totalCost;

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

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;
}
