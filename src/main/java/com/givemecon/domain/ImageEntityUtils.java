package com.givemecon.domain;

import com.givemecon.domain.brand.BrandIcon;
import com.givemecon.domain.category.CategoryIcon;
import com.givemecon.domain.voucher.VoucherImage;
import com.givemecon.domain.voucherforsale.VoucherForSaleImage;
import com.givemecon.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class ImageEntityUtils {

    private final AwsS3Service awsS3Service;

    public ImageEntity createImageEntity(String baseEntityName, MultipartFile imageFile) {
        String originalName = imageFile.getOriginalFilename();
        String imageKey = FileUtils.convertFilenameToKey(originalName);
        String imageUrl = awsS3Service.upload(imageKey, imageFile);

        return switch (baseEntityName) {
            case "Category" -> CategoryIcon.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build();
            case "Brand" -> BrandIcon.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build();
            case "Voucher" -> VoucherImage.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build();
            case "VoucherForSale" -> VoucherForSaleImage.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build();
            default -> null;
        };
    }

    public void updateImageEntity(ImageEntity imageEntity, MultipartFile newImageFile) {
        String imageKey = imageEntity.getImageKey();
        String newImageUrl = awsS3Service.upload(imageKey, newImageFile);
        String newOriginalName = newImageFile.getOriginalFilename();
        imageEntity.update(newImageUrl, newOriginalName);
    }
}
