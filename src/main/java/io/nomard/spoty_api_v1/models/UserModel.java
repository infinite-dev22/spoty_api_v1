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

package io.nomard.spoty_api_v1.models;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class UserModel implements Serializable {
    private Long id;
    private Tenant tenant;
    private Branch branch;
    private String firstName;
    private String lastName;
    private String otherName;
    private String email;
    private String salary;
    private String phone;
    private Role role;
    private EmploymentStatus employmentStatus;
    private Department department;
    private Designation designation;
    private LocalDate startDate;
    @Builder.Default
    private boolean active = true;
    @Builder.Default
    private boolean locked = false;
    private String avatar;
}
