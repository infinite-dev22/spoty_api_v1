package io.nomard.spoty_api_v1.models.payments;

import io.nomard.spoty_api_v1.entities.User;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Payload {
    private String phoneNumber;
    private String network;
    private String amount;
    private String currency;
    private String email;
}
