package com.lostway.eventmanager.exception.controller;

import com.lostway.eventmanager.exception.*;
import com.lostway.eventmanager.exception.dto.ErrorMessageResponse;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationNotFoundException(LocationNotFoundException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не найдена",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(LocationIsPlannedException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationIsPlannedException(LocationIsPlannedException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Локация уже занята мероприятием",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(LocationCapacityReductionException.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationCapacityReductionException(LocationCapacityReductionException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Ошибка изменения вместимости локации",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelEntityNotFoundException(EntityNotFoundException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Сущность не была найдена",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handelMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Данные сущности введены некорректно",
                        getDetailedMessage(e),
                        now()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(HandlerMethodValidationException ex) {
        List<String> details = getDetails(ex);

        ErrorMessageResponse response = ErrorMessageResponse.builder()
                .message("Ошибка валидации параметров")
                .detailedMessage(String.join("; ", details))
                .dateTime(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handelDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.badRequest()
                .body("Ошибка данных: нарушены ограничения БД");
    }

    @ExceptionHandler(LocationAlreadyExists.class)
    public ResponseEntity<ErrorMessageResponse> handelLocationAlreadyExists(LocationAlreadyExists e) {
        log.error("Exception handled:", e);
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Локация уже существует.",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handelEventNotFoundException(EventNotFoundException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Мероприятие не было найдено.",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(EventAlreadyStartedException.class)
    public ResponseEntity<ErrorMessageResponse> handelEventAlreadyStartedException(EventAlreadyStartedException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageResponse(
                        "Регистрация на мероприятие завершено.",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(AlreadyRegisteredException.class)
    public ResponseEntity<ErrorMessageResponse> handelAlreadyRegisteredException(AlreadyRegisteredException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(
                        "Пользователь уже зарегистрирован на участие.",
                        e.getMessage(),
                        now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoResourceException(NoResourceFoundException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Данный адрес не был найден",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorMessageResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(
                        "Некорректный запрос",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorMessageResponse> handleIncorrectPasswordException(IncorrectPasswordException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(
                        "Некорректный запрос",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(
                        "Некорректный запрос",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorMessageResponse> handleJwtException(JwtException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(401)
                .body(new ErrorMessageResponse(
                        "Сущность не найдена",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(403)
                .body(new ErrorMessageResponse(
                        "Недостаточно прав для выполнения операции",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(CapacityNotEnoughException.class)
    public ResponseEntity<ErrorMessageResponse> handleCapacityNotEnoughException(CapacityNotEnoughException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(
                        "Некорректный запрос",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(UserNotMemberException.class)
    public ResponseEntity<ErrorMessageResponse> handleUserNotMemberException(UserNotMemberException e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(
                        "Некорректный запрос",
                        e.getMessage(),
                        now()
                ));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorMessageResponse> handleAllException(Throwable e) {
        log.error("Exception handled:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageResponse(
                        "Внутренняя ошибка сервера",
                        e.getMessage(),
                        now()
                ));
    }

    private String getDetailedMessage(BindException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Поле '%s' : %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
    }

    private static List<String> getDetails(HandlerMethodValidationException ex) {
        List<String> details = ex.getParameterValidationResults().stream()
                .flatMap(r -> r.getResolvableErrors().stream())
                .map(error -> String.format("Field: %s, Message: %s",
                        Arrays.toString(error.getArguments()), error.getDefaultMessage()))
                .toList();
        return details;
    }
}
