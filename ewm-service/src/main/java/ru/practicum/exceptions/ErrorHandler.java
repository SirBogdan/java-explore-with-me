package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.info("400 {}", e.getMessage(), e);
        return new ErrorResponse(e.getLocalizedMessage(), "Ошибка валидации", HttpStatus.BAD_REQUEST.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(exc -> "Поле " + exc.getField() + " " + exc.getDefaultMessage())
                .collect(Collectors.toList());
        log.info("400 {}", e.getMessage(), e);
        ErrorResponse result = new ErrorResponse(e.getLocalizedMessage(),
                "Ошибка валидации: попытка ввода некорректных значений", HttpStatus.BAD_REQUEST.name());
        result.setErrors(errors);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final ObjectNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new ErrorResponse(e.getLocalizedMessage(), "Объект не найден", HttpStatus.NOT_FOUND.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final ConflictException e) {
        log.info("409 {}", e.getMessage());
        return new ErrorResponse(e.getLocalizedMessage(), "Конфликт при попытке ввода значений",
                HttpStatus.CONFLICT.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolation(final ConstraintViolationException e) {
        log.info("409 {}", e.getMessage());
        return new ErrorResponse(e.getLocalizedMessage(),
                "Ошибка базы данных: попытка ввода невалидных значений", HttpStatus.CONFLICT.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final ForbiddenException e) {
        log.info("409 {}", e.getMessage());
        return new ErrorResponse(e.getLocalizedMessage(), "Ошибка доступа", HttpStatus.FORBIDDEN.name());
    }
}
