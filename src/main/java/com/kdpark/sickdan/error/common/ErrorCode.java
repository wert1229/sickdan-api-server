package com.kdpark.sickdan.error.common;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    INVALID_INPUT_VALUE(400, "C001", "Some input values are invaild"),
    ENTITY_NOT_FOUND(400, "C002", "Can't find the entity"),
    INTERNAL_IO_FAILED(400, "C003", "Internal IO failed for some reason");

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
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

    public int getStatus() {
        return status;
    }
}
