package com.givemecon.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class DeletionCheckEntity {

    private boolean deleted;
}
