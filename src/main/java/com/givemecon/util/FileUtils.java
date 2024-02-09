package com.givemecon.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public abstract class FileUtils {

    public static boolean isValidFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    public static String convertFilenameToKey(String originalFilename) {
        return UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFilename);
    }
}
