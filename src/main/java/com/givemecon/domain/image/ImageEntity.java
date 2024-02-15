package com.givemecon.domain.image;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class ImageEntity extends BaseTimeEntity {

    @Column(nullable = false, unique = true)
    private String imageKey;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Column(nullable = false)
    private String originalName;

    protected ImageEntity(String imageKey, String imageUrl, String originalName) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.originalName = originalName;
    }

    public void update(String imageUrl, String originalName) {
        this.imageUrl = imageUrl;
        this.originalName = originalName;
    }
}
