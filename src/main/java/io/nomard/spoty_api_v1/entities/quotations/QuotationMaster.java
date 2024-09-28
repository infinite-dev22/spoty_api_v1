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

package io.nomard.spoty_api_v1.entities.quotations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.*;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationMaster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ref;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "customer_id")
    private Customer customer;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnore
    private Branch branch;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;
    @OneToMany(mappedBy = "quotation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuotationDetail> quotationDetails = new LinkedList<>();
    @Column(nullable = false)
    @Builder.Default
    private double subTotal = 0;
    @Builder.Default
    private double total = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;
    @Builder.Default
    private double taxAmount = 0;
    @Builder.Default
    private double discountAmount = 0;
    @Column(nullable = false)
    @Builder.Default
    private double shippingFee = 0;
    private String status;
    private String notes;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Builder.Default
    private List<Approver> approvers = new ArrayList<>();
    @Builder.Default
    private Boolean approved = false;
    @Builder.Default
    private Integer nextApprovedLevel = 0;
    private String approvalStatus;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee createdBy;
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee updatedBy;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        QuotationMaster that = (QuotationMaster) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
