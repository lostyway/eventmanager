package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.exception.LocationCapacityReductionException;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.exception.LocationNotFoundException;
import com.lostway.eventmanager.exception.ErrorMessageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationNotFoundException(LocationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не найдена",
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(LocationIsPlannedException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationIsPlannedException(LocationIsPlannedException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Локация уже занята мероприятием",
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(LocationCapacityReductionException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationCapacityReductionException(LocationCapacityReductionException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Ошибка изменения вместимости локации",
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не была найдена",
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handelMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Данные сущности введены некорректно",
                        getDetailedMessage(e),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handelDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка данных: нарушены ограничения БД");
    }

    private String getDetailedMessage(BindException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Поле '%s : %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
    }
}
