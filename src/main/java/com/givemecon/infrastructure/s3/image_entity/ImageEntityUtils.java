package com.givemecon.infrastructure.s3.image_entity;

import com.givemecon.common.util.FileUtils;
import com.givemecon.domain.entity.ImageEntity;
import com.givemecon.infrastructure.s3.AwsS3Utils;
import com.givemecon.infrastructure.s3.image_entity.builder.ImageEntityBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class ImageEntityUtils {

    private final AwsS3Utils awsS3Utils;

    private final ImageEntityBuilderFactory imageEntityBuilderFactory;

    public <IE extends ImageEntity> IE createImageEntity(Class<IE> imageEntityType, MultipartFile imageFile) {
        String originalName = imageFile.getOriginalFilename();
        String imageKey = FileUtils.convertFilenameToKey(originalName);
        String imageUrl = awsS3Utils.upload(imageKey, imageFile);
        ImageEntity imageEntity = imageEntityBuilderFactory.findBy(imageEntityType.getSimpleName())
                .build(imageKey, imageUrl, originalName);

        return imageEntityType.cast(imageEntity);
    }

    public void updateImageEntity(ImageEntity imageEntity, MultipartFile newImageFile) {
        String imageKey = imageEntity.getImageKey();
        String newImageUrl = awsS3Utils.upload(imageKey, newImageFile);
        String newOriginalName = newImageFile.getOriginalFilename();
        imageEntity.update(newImageUrl, newOriginalName);
    }
}
