package com.tuplaus.component;

import com.tuplaus.dto.ErrorResponse;
import com.tuplaus.exception.MoneyException;
import com.tuplaus.exception.TuplausException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MoneyException.class)
    public ResponseEntity<ErrorResponse> handleMoneyException(MoneyException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(TuplausException.class)
    public ResponseEntity<ErrorResponse> handleTuplausException(TuplausException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
