package io.nomard.spoty_api_v1.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardModel {
    private String card;
    private String cvv;
    private String expiryMonth;
    private String expiryYear;
    private String amount;
    private String firstName;
    private String lastName;
    private String fullName;
    private String reference;
    private String clientIp;
    private String email;
    private String pin;
    private String city;
    private String address;
    private String state;
    private String country;
    private String zipcode;
    private String planName;
    private boolean recurring;
}
