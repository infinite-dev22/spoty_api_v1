package io.nomard.spoty_api_v1.entities.hrm.pay_roll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "salaries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    @JoinColumn(nullable = false, name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;
    @ManyToOne
    private User employee;
    @ManyToOne
    private Designation designation;
    private String period;
    @ManyToOne
    @JoinColumn(name = "pay_slip_type_id")
    private PaySlipType paySlipType;
    private char status;  // P - Pending, R - Rejected, A - Approved, E - Returned, V - Viewed, G - Generated, S - Sent
    private String salary;
    @Column(name = "net_salary")
    private String netSalary;

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