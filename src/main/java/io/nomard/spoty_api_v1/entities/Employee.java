package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.proxy.HibernateProxy;

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
    @JsonView(Views.Detailed.class)
    private Tenant tenant;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Moderate.class)
    private Department department;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Moderate.class)
    private Designation designation;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Moderate.class)
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
    @JsonView(Views.Moderate.class)
    private Role role;

    @JsonView(Views.Tiny.class)
    private String firstName;
    @JsonView(Views.Tiny.class)
    private String lastName;
    @JsonView(Views.Tiny.class)
    private String otherName;
    @JsonView(Views.Moderate.class)
    private String phone;
    @JsonView(Views.Moderate.class)
    private String avatar;
    @JsonView(Views.Moderate.class)
    private String email;
    @JsonView(Views.Moderate.class)
    private String salary;

    @JoinColumn
    @Column(nullable = false)
    @Builder.Default
    @JsonView(Views.Moderate.class)
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    @JsonIgnore
    private boolean locked = false;

    @JsonView(Views.Moderate.class)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Moderate.class)
    private Employee createdBy;

    @JsonView(Views.Moderate.class)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Moderate.class)
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
