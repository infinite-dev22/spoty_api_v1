package io.nomard.spoty_api_v1.entities.hrm.pay_roll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pay_slip_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaySlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Branch.class)
    @JoinTable(
            name = "pay_slip_types_branches",
            joinColumns = {@JoinColumn(name = "employment_status_id")},
            inverseJoinColumns = {@JoinColumn(name = "branch_id")})
    @Builder.Default
    private List<Branch> branches = Collections.synchronizedList(new ArrayList<>());
    @ManyToOne
    @JoinColumn(name = "pay_slip_type_id")
    private PaySlipType paySlipType;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "salaries_quantity")
    private int salariesQuantity;
    private char status;  // P - Pending, R - Rejected, A - Approved, E - Returned, V - Viewed, G - Generated, g - Generating, S - Sent
    private String message;

    @Column(name = "created_at")
    @JsonIgnore
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(name = "updated_at")
    @JsonIgnore
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;
}
