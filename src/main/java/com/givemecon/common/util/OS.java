package com.givemecon.common.util;

public enum OS {

    WINDOWS,
    UNIX,
    MAC_OS_X;

    public static OS detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return UNIX;
        } else if (osName.contains("mac")) {
            return MAC_OS_X;
        } else {
            throw new RuntimeException("Unrecognized OS: " + osName);
        }
    }
}
