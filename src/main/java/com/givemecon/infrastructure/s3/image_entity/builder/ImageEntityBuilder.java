package com.givemecon.infrastructure.s3.image_entity.builder;

import com.givemecon.domain.entity.ImageEntity;

public abstract class ImageEntityBuilder {

    /**
     * 구현체가 생성하는 image entity의 이름을 반환하는 메서드 (E.g. XxxBuilder -> Xxx).
     * @return Image entity의 이름
     */
    public final String getEntityName() {
        return getEntityType().getSimpleName();
    }

    public abstract Class<? extends ImageEntity> getEntityType();

    public abstract ImageEntity build(String imageKey, String imageUrl, String originalName);
}
