package com.kdpark.sickdan.error.exception;

import com.kdpark.sickdan.error.common.BusinessException;
import com.kdpark.sickdan.error.common.ErrorCode;

public class EntityDuplicatedException extends BusinessException {
    public EntityDuplicatedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
