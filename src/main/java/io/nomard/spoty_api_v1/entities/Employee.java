package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Branch.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Branch branch;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;
    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Designation designation;
    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private EmploymentStatus employmentStatus;
    @JoinColumn(nullable = false)
    @OneToOne(
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            targetEntity = User.class
    )
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JsonIgnore
    private User user;
    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(nullable = false)
    private Role role;
    private String firstName;
    private String lastName;
    private String otherName;
    private String phone;
    private String avatar;
    private String email;
    private String salary;
    private LocalDate startDate;
    private String employmentConfirmationLetter;
    @JoinColumn
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    @Column(nullable = false)
    @Builder.Default
    @JsonIgnore
    private boolean locked = false;
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
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Employee user = (Employee) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode()
                : getClass().hashCode();
    }
}
