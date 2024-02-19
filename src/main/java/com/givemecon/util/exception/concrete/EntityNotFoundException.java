package com.givemecon.util.exception.concrete;

import com.givemecon.util.exception.GivemeconException;
import lombok.Getter;

import static com.givemecon.util.error.ErrorCode.*;

@Getter
public class EntityNotFoundException extends GivemeconException {

    private final String entityName;

    public EntityNotFoundException(Class<?> entityType) {
        super(ENTITY_NOT_FOUND);
        this.entityName = entityType.getSimpleName();
    }
}
