package com.tuplaus.dto;

import lombok.Data;

@Data
public class ErrorResponse  {
    private final String status;
    private final String message;

    public ErrorResponse(String message) {
        this.status = "error";
        this.message = message;
    }
}
