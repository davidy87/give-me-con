package com.givemecon.util.exception.concrete;

import com.givemecon.util.exception.GivemeconException;
import lombok.Getter;

import static com.givemecon.util.error.GlobalErrorCode.*;

@Getter
public class EntityNotFoundException extends GivemeconException {

    public EntityNotFoundException(Class<?> entityType) {
        super(ENTITY_NOT_FOUND, ENTITY_NOT_FOUND.getMessage() + " Entity name: " + entityType.getSimpleName());
    }
}
