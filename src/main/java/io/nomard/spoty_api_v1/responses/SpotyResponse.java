package io.nomard.spoty_api_v1.responses;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface SpotyResponse {
    ResponseEntity<ObjectNode> ok();

    ResponseEntity<ObjectNode> created();

    ResponseEntity<ObjectNode> error(Exception exception);

    ResponseEntity<ObjectNode> conflict();

    ResponseEntity<ObjectNode> taken();

    ResponseEntity<ObjectNode> custom(HttpStatus httpStatus, String message);
}
