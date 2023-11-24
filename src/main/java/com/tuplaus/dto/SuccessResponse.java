package com.tuplaus.dto;

import lombok.Data;

@Data
public class SuccessResponse<T> {
    private final T data;
    private final String status;

    public SuccessResponse(T data) {
        this.data = data;
        this.status = "success";
    }
}
