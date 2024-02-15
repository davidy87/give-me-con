package com.givemecon.util.image_entity;

import com.givemecon.domain.image.ImageEntity;

public abstract class ImageEntityBuilder {

    /**
     * 구현체가 생성하는 image entity의 이름을 반환하는 메서드 (E.g. XxxBuilder -> Xxx).
     * @return Image entity의 이름
     */
    public String getEntityName() {
        return getEntityType().getSimpleName();
    }

    public abstract Class<? extends ImageEntity> getEntityType();

    public abstract ImageEntity build(String imageKey, String imageUrl, String originalName);
}
