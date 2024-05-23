package com.givemecon.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    public static boolean isFileValid(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    public static String convertFilenameToKey(String originalFilename) {
        return UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFilename);
    }
}
