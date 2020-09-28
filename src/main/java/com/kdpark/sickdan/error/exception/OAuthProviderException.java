package com.kdpark.sickdan.error.exception;

import com.kdpark.sickdan.error.common.BusinessException;
import com.kdpark.sickdan.error.common.ErrorCode;

public class OAuthProviderException extends BusinessException {
    public OAuthProviderException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
