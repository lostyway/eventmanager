package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.exception.LocationCapacityReductionException;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.exception.LocationNotFoundException;
import com.lostway.eventmanager.exception.dto.ErrorMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationNotFoundException(LocationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не найдена",
                        e.getMessage(),
                        getCurrentTimeWithSeconds()));
    }

    @ExceptionHandler(LocationIsPlannedException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationIsPlannedException(LocationIsPlannedException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Локация уже занята мероприятием",
                        e.getMessage(),
                        getCurrentTimeWithSeconds()));
    }

    @ExceptionHandler(LocationCapacityReductionException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationCapacityReductionException(LocationCapacityReductionException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Ошибка изменения вместимости локации",
                        e.getMessage(),
                        getCurrentTimeWithSeconds()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handelMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Данные локации введены некорректно",
                        getDetailedMessage(e),
                        getCurrentTimeWithSeconds()));
    }

    private static String getCurrentTimeWithSeconds() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }

    private String getDetailedMessage(BindException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Поле '%s : %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
    }
}
