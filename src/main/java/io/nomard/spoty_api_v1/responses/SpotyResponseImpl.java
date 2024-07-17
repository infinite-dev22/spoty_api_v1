package io.nomard.spoty_api_v1.responses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SpotyResponseImpl implements SpotyResponse {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<ObjectNode> ok() {
        var response = objectMapper.createObjectNode();
        response.put("status", 200);
        response.put("message", "Process successfully completed");
        return new ResponseEntity<>(
                response, HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<ObjectNode> created() {
        var response = objectMapper.createObjectNode();
        response.put("status", 201);
        response.put("message", "Process successfully completed");
        return new ResponseEntity<>(
                response, HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<ObjectNode> error(Throwable throwable) {
        var response = objectMapper.createObjectNode();
        response.put("status", 500);
        response.put("message", "An error occurred");
        response.put("body", throwable.getMessage());
        return new ResponseEntity<>(
                response, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    public ResponseEntity<ObjectNode> conflict() {
        var response = objectMapper.createObjectNode();
        response.put("status", 409);
        response.put("message", "Email already taken");
        return new ResponseEntity<>(
                response, HttpStatus.CONFLICT
        );
    }

    @Override
    public ResponseEntity<ObjectNode> taken() {
        var response = objectMapper.createObjectNode();
        response.put("status", 409);
        response.put("message", "Email already taken");
        return new ResponseEntity<>(
                response, HttpStatus.CONFLICT
        );
    }

    @Override
    public ResponseEntity<ObjectNode> custom(HttpStatus httpStatus, String message) {
        var response = objectMapper.createObjectNode();
        response.put("status", httpStatus.value());
        response.put("message", message);
        return new ResponseEntity<>(
                response, httpStatus
        );
    }
}
