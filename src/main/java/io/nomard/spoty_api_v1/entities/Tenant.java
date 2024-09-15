package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private LocalDateTime subscriptionEndDate;
    @Builder.Default
    private boolean trial = false;
    @Builder.Default
    private boolean canTry = true;
    private LocalDateTime trialEndDate;
    @Builder.Default
    private boolean newTenancy = true;
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
        Tenant tenant = (Tenant) o;
        return getId() != null && Objects.equals(getId(), tenant.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
