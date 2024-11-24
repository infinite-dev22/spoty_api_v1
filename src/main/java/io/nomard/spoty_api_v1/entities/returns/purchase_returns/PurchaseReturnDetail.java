package io.nomard.spoty_api_v1.entities.returns.purchase_returns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseReturnDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double unitCost;
    private double totalCost;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_return_id")
    @JsonIgnore
    private PurchaseReturnMaster purchaseReturnMaster;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Product product;
    private int quantity;
    @JsonIgnore
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Employee createdBy;
    @JsonIgnore
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Employee updatedBy;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PurchaseReturnDetail that = (PurchaseReturnDetail) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
