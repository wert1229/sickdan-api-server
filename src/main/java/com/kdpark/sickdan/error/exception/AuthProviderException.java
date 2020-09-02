package com.kdpark.sickdan.error.exception;

import com.kdpark.sickdan.error.common.BusinessException;
import com.kdpark.sickdan.error.common.ErrorCode;

public class AuthProviderException extends BusinessException {
    public AuthProviderException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
