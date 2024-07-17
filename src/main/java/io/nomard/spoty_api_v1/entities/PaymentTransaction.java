package io.nomard.spoty_api_v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Accessors(chain = true)
@Table(name = "payment_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false, name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @ManyToOne(targetEntity = Branch.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Branch branch;

    @Column(name = "transaction_reference")
    private String transactionReference;
    @Column(name = "plan_name")
    private String planName;
    @Column(name = "paid_on")
    private Date paidOn;
    private String amount;
    @Builder.Default
    private boolean recurring = false;
    @Column(name = "pay_method")
    private String payMethod;
    @Column(name = "pay_source")
    private String paySource; // Phone number or Card number.

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
