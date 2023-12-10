package io.nomard.spoty_api_v1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorMessage {
    private HttpStatus status;
    private String message;
}
