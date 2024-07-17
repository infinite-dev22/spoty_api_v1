package io.nomard.spoty_api_v1.entities.hrm.pay_roll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Accessors(chain = true)
@Table(name = "pay_slip_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PaySlip {
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
