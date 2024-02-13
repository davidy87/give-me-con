package com.givemecon.domain.image_entity_util;

import com.givemecon.domain.ImageEntity;

public abstract class ImageEntityBuilder {

    /**
     * Image entity의 주인인 (연관 관계 상에서) entity의 클래스 이름을 반환한다.
     * E.g. CategoryIconBuilder -> Category
     * @return Image entity의 주인인 (연관 관계 상에서) entity의 클래스 이름
     */
    public abstract String getOwnerEntityName();

    public abstract ImageEntity build(String imageKey, String imageUrl, String originalName);
}
