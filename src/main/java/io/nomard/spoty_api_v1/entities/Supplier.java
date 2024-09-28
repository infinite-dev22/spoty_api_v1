package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.utils.Views;
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
public class Supplier implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Branch.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Branch branch;
    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Tenant tenant;
    @JsonView(Views.Tiny.class)
    private String firstName;
    @JsonView(Views.Tiny.class)
    private String lastName;
    @JsonView(Views.Tiny.class)
    private String otherName;
    @JsonView(Views.Tiny.class)
    private String email;
    @JsonView(Views.Tiny.class)
    private String phone;
    @Column(unique = true)
    @JsonView(Views.Tiny.class)
    private String avatar;
    @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;
    @JsonView(Views.Tiny.class)
    private String country;
    @JsonView(Views.Tiny.class)
    private String city;
    @JsonView(Views.Tiny.class)
    private String address;
    @JsonView(Views.Tiny.class)
    private String taxNumber;
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
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Supplier user = (Supplier) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
