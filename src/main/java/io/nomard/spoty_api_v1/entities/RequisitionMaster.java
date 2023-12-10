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
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
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

    @ManyToOne
    private User user_detail;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "requisition", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
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
