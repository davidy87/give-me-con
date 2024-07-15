package com.givemecon.common.exception.concrete;

import com.givemecon.common.exception.GivemeconException;
import lombok.Getter;

import static com.givemecon.common.error.GlobalErrorCode.ENTITY_NOT_FOUND;

@Getter
public class EntityNotFoundException extends GivemeconException {

    public EntityNotFoundException(Class<?> entityType) {
        super(ENTITY_NOT_FOUND, ENTITY_NOT_FOUND.getMessage() + " Entity name: " + entityType.getSimpleName());
    }
}
