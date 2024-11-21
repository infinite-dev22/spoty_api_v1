package io.nomard.spoty_api_v1.entities.returns.purchase_returns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.*;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseReturnMaster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ref;
    @Column(nullable = false)
    private LocalDate date;
    @JoinColumn(nullable = false, name = "supplier_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier;
    @JoinColumn(name = "branch_id")
    @ManyToOne(targetEntity = Branch.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Branch branch;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;
    @OneToMany(orphanRemoval = true, mappedBy = "purchaseReturnMaster", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    @Valid
    private List<PurchaseReturnDetail> purchaseReturnDetails = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;
    private double taxAmount;
    private double discountAmount;
    @Builder.Default
    private double shippingFee = 0.0;
    private double amountPaid;
    private double total;
    private double subTotal;
    private double amountDue;
    private String purchaseStatus;
    private String paymentStatus;
    private String notes;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Builder.Default
    private List<Reviewer> reviewers = new ArrayList<>();
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
        PurchaseReturnMaster that = (PurchaseReturnMaster) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
