package com.lostway.eventmanager.exception.controller;

import com.lostway.eventmanager.exception.*;
import com.lostway.eventmanager.exception.dto.ErrorMessageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static java.time.LocalDateTime.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationNotFoundException(LocationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не найдена",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(LocationIsPlannedException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationIsPlannedException(LocationIsPlannedException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Локация уже занята мероприятием",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(LocationCapacityReductionException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationCapacityReductionException(LocationCapacityReductionException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Ошибка изменения вместимости локации",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не была найдена",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handelMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Данные сущности введены некорректно",
                        getDetailedMessage(e),
                        now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handelDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest()
                .body("Ошибка данных: нарушены ограничения БД");
    }

    @ExceptionHandler(LocationAlreadyExists.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationAlreadyExists(LocationAlreadyExists e) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Локация уже существует.",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handelAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessageResponse(
                        "Недостаточно прав для выполнения операции",
                        e.getMessage(),
                        now()
                ));
    }

    private String getDetailedMessage(BindException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Поле '%s : %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
    }
}
