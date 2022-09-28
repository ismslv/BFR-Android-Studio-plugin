package com.bfr.pluginandroidstudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Common {
    public static LinkedHashMap<String, App> APPS = new LinkedHashMap<String, App>() {{
        put("usb", new App("usb", "ServiceUSB", "apk", "Build USB", "ServiceUSB", ""));
        put("speech", new App("speech", "ServiceSpeech", "apk", "Build Speech", "ServiceSpeech", ""));
        put("vision", new App("vision", "ServiceVision", "apk", "Build Vision", "ServiceVision", ""));
        put("companion", new App("companion", "ServiceCompanion", "apk", "Build Companion", "ServiceCompanion", ""));
        put("updater", new App("updater", "Updater", "apk", "Build Updater", "Updater", "com.bfr.buddy.updater/com.bfr.buddy.updater.UpdaterActivity"));
        put("core", new App("core", "BuddyCore", "apk", "Build Core", "BuddyCore", "com.bfr.buddy.core/com.bfr.buddy.core.LauncherActivity"));
        put("sdk", new App("sdk", "BuddySDK", "aar", "Build SDK", "BuddySDK", ""));
    }};

    public static String[] SDKLIBS = new String[] {
            "sdk",
            "utils",
            "ui",
            "network",
            "companion",
            "usb", "sdk-speech", "sdk-vision"
    };

    public static String CONFIG_PATH = "/sdcard/Configs/";
    public static String CONFIG_SYSTEM_REMOTE = CONFIG_PATH + "System/";
    public static String CONFIG_USER_REMOTE = CONFIG_PATH + "Users/[USERNAME]/";

    public static String CONFIG_LOGS = "/sdcard/Logs/";
    public static String CONFIG_LOGS_SYSTEM = CONFIG_LOGS + "System/";
    public static String CONFIG_LOGS_CUSTOM = CONFIG_LOGS + "Custom/";
}