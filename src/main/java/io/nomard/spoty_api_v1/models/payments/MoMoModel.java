package io.nomard.spoty_api_v1.models.payments;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class MoMoModel {
    private String fullName;
    private String clientIp;
    private String planName;
    private Payload payload;
}
