/*
 * Copyright (c) 2023, Jonathan Mark Mwigo. All rights reserved.
 *
 * The computer system code contained in this file is the property of Jonathan Mark Mwigo and is protected by copyright law. Any unauthorized use of this code is prohibited.
 *
 * This copyright notice applies to all parts of the computer system code, including the source code, object code, and any other related materials.
 *
 * The computer system code may not be modified, translated, or reverse-engineered without the express written permission of Jonathan Mark Mwigo.
 *
 * Jonathan Mark Mwigo reserves the right to update, modify, or discontinue the computer system code at any time.
 *
 * Jonathan Mark Mwigo makes no warranties, express or implied, with respect to the computer system code. Jonathan Mark Mwigo shall not be liable for any damages, including, but not limited to, direct, indirect, incidental, special, consequential, or punitive damages, arising out of or in connection with the use of the computer system code.
 */

package io.nomard.spoty_api_v1.entities.adjustments;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "adjustment_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdjustmentTransaction implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "adjustment_detail_id")
    private AdjustmentDetail adjustmentDetail;

    @Column
    private Date date;

    @Column(name = "adjust_quantity")
    private long adjustQuantity;

    @Column(name = "adjustment_type")
    private String adjustmentType;

    @Column(name = "created_at")
    @JsonIgnore
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(name = "updated_at")
    @JsonIgnore
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;
}
