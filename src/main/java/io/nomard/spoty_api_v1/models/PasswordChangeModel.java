package io.nomard.spoty_api_v1.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PasswordChangeModel {
    private String email;
    private String password;
    private String confirmPassword;
}
