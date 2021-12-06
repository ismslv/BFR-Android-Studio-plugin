package com.bfr.pluginandroidstudio;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Common {
    public static LinkedHashMap<String, App> APPS = new LinkedHashMap<String, App>() {{
        put("usb", new App("usb", "ServiceUSB", "apk", "BuildUSB", "ServiceUSB"));
        put("speech", new App("speech", "ServiceSpeech", "apk", "BuildSpeech", "ServiceSpeech"));
        put("vision", new App("vision", "ServiceVision", "apk", "BuildVision", "ServiceVision"));
        put("updater", new App("updater", "Updater", "apk", "BuildUpdater", "Updater"));
        put("core", new App("core", "BuddyCore", "apk", "BuildCore", "BuddyCore"));
        put("sdk", new App("sdk", "BuddySDK", "aar", "BuildSDK", "BuddySDK"));
    }};

    public static String CONFIG_PATH = "/sdcard/Configs/";
    public static String CONFIG_SYSTEM_REMOTE = CONFIG_PATH + "System/";
    public static String CONFIG_USER_REMOTE = CONFIG_PATH + "Users/[USERNAME]/";
}