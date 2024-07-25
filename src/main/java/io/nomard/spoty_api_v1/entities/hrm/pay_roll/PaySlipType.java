package io.nomard.spoty_api_v1.entities.hrm.pay_roll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "pay_slip_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PaySlipType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Branch.class)
    @JoinTable(
            name = "pay_slip_types_branches",
            joinColumns = {@JoinColumn(name = "employment_status_id")},
            inverseJoinColumns = {@JoinColumn(name = "branch_id")})
    @Builder.Default
    private List<Branch> branches = new LinkedList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;
    private String name;
    private String color;
    private String description;

    @Column(name = "created_at")
    @JsonIgnore
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;
}
