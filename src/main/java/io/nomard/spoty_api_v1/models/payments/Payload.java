package io.nomard.spoty_api_v1.models.payments;

import io.nomard.spoty_api_v1.entities.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Payload {
    private String planName;
    private boolean recurring;
    private String payMethod;
    private String paySource;
    private CardModel card;
    private User user;
}
