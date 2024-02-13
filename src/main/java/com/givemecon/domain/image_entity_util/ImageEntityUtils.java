package com.givemecon.domain.image_entity_util;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.domain.ImageEntity;
import com.givemecon.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class ImageEntityUtils {

    private final AwsS3Service awsS3Service;

    private final ImageEntityBuilderFactory imageEntityBuilderFactory;

    public ImageEntity createImageEntity(String baseEntityName, MultipartFile imageFile) {
        String originalName = imageFile.getOriginalFilename();
        String imageKey = FileUtils.convertFilenameToKey(originalName);
        String imageUrl = awsS3Service.upload(imageKey, imageFile);

        return (ImageEntity) imageEntityBuilderFactory.findBy(baseEntityName)
                .build(imageKey, imageUrl, originalName);
    }

    public void updateImageEntity(ImageEntity imageEntity, MultipartFile newImageFile) {
        String imageKey = imageEntity.getImageKey();
        String newImageUrl = awsS3Service.upload(imageKey, newImageFile);
        String newOriginalName = newImageFile.getOriginalFilename();
        imageEntity.update(newImageUrl, newOriginalName);
    }
}
