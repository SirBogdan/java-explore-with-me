package ru.practicum.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ErrorResponse {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ErrorResponse(String message, String reason, String status) {
        this.errors = Collections.emptyList();
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
