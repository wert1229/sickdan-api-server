package com.kdpark.sickdan.error.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    INVALID_INPUT_VALUE(BAD_REQUEST, "C001", "Some input values are invaild"),
    ENTITY_NOT_FOUND(BAD_REQUEST, "C002", "Can't find the entity"),
    INTERNAL_IO_FAILED(BAD_REQUEST, "C003", "Internal IO failed for some reason"),
    EXTERNAL_IO_FAILED(BAD_REQUEST, "C004", "External IO failed for some reason"),
    MEMBER_DUPLICATED(BAD_REQUEST, "C005", "Member duplicated"),
    BANISHED_REFRESH_TOKEN(UNAUTHORIZED, "C006", "Banished refresh Token");

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
