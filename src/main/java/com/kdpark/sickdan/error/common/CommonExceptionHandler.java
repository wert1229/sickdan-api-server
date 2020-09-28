package com.kdpark.sickdan.error.common;

import com.kdpark.sickdan.error.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class CommonExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(EntityDuplicatedException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(EntityDuplicatedException e) {
        log.error("EntityDuplicatedException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(FileReadException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(FileReadException e) {
        log.error("FileReadException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(OAuthProviderException.class)
    protected ResponseEntity<ErrorResponse> handleAuthException(OAuthProviderException e) {
        log.error("AuthProviderException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(PasswordNotCorrectException.class)
    protected ResponseEntity<ErrorResponse> handleAuthException(PasswordNotCorrectException e) {
        log.error("PasswordNotCorrectException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(BanishedTokenException.class)
    protected ResponseEntity<ErrorResponse> handleAuthException(BanishedTokenException e) {
        log.error("BanishedTokenException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

//    protected ResponseEntity<ErrorResponse> handleBindException(MethodArgumentNotValidException e) {
//        log.error("MethodArgumentNotValidException", e);
//        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
}
