package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends GivemeconException {

    private final String entityName;

    public EntityNotFoundException(ErrorCode errorCode, Class<?> entityType) {
        super(errorCode);
        this.entityName = entityType.getSimpleName();
    }
}
