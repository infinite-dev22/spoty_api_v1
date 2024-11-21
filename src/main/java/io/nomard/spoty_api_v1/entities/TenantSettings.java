package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String websiteLink;
    private String phoneNumber;
    private String email;
    private String supportEmail;
    private String infoEmail;
    private String hrEmail;
    private String salesEmail;
    private String postalAddress;
    private String physicalAddress;
    private String tagLine;
    private Boolean reportLogo;
    private Boolean emailLogo;
    private Boolean receiptLogo;
    private String twitter;
    private String facebook;
    private String linkedIn;
    @Builder.Default
    private Boolean review = false;
    @Builder.Default
    private Boolean approveAdjustments = false;
    @Builder.Default
    private Boolean approveRequisitions = false;
    @Builder.Default
    private Boolean approveTransfers = false;
    @Builder.Default
    private Boolean approveStockIns = false;
    @Builder.Default
    private Boolean approveQuotations = false;
    @Builder.Default
    private Boolean approvePurchases = false;
    @Builder.Default
    private Boolean approveSaleReturns = false;
    @Builder.Default
    private Boolean approveSales = false;
    @Builder.Default
    private Boolean approvePurchaseReturns = false;
    @Builder.Default
    private Integer approvalLevels = 0;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Reviewer> reviewers = new ArrayList<>();
    private Currency defaultCurrency;
    private String logo;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;
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
        ProductCategory that = (ProductCategory) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
