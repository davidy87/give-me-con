package com.givemecon.util;

import org.springframework.web.multipart.MultipartFile;

public abstract class FileUtils {

    public static boolean isValidFile(MultipartFile imageFile) {
        return imageFile != null && !imageFile.isEmpty();
    }
}
