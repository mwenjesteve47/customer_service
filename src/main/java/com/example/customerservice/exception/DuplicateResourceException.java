package com.example.customerservice.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicateResourceException extends RuntimeException {
    @JsonProperty("statusCode")
    Integer statusCode;
    public DuplicateResourceException(String message) {
        super(message);
        log.error("DuplicateResourceException exception thrown: " + message);
    }

    public DuplicateResourceException(String message, Integer statusCode) {
        super(message);
        this.statusCode= statusCode;
        log.error("DuplicateResourceException exception thrown: " + message);
    }
}
