package io.nomard.spoty_api_v1.models.payments;

import lombok.*;

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
