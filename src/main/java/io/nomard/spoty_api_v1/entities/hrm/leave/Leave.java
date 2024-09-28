package io.nomard.spoty_api_v1.entities.hrm.leave;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnore
    private Branch branch;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "designation_id")
    private Designation designation;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Duration duration;
    @Column(name = "leave_type")
    private String leaveType;
    private String attachment;
    private String leaveStatus;
    private Boolean approved;
    private String reason;
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
        Leave leave = (Leave) o;
        return getId() != null && Objects.equals(getId(), leave.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
