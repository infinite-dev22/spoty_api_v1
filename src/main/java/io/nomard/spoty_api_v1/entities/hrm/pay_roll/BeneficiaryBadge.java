package io.nomard.spoty_api_v1.entities.hrm.pay_roll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

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
public class BeneficiaryBadge {
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Branch.class)
    @JoinTable(
            name = "beneficiary_badges_branches",
            joinColumns = {@JoinColumn(name = "employment_status_id")},
            inverseJoinColumns = {@JoinColumn(name = "branch_id")})
    @JsonIgnore
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "beneficiary_type_id")
    private BeneficiaryType beneficiaryType;
    private String color;
    private String description;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private User updatedBy;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BeneficiaryBadge that = (BeneficiaryBadge) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
