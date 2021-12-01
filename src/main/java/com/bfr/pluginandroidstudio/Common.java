package com.bfr.pluginandroidstudio;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Common {
    public static LinkedHashMap<String, App> APPS = new LinkedHashMap<String, App>() {{
        put("usb", new App("usb", "ServiceUSB", "apk", "BuildServiceUSB"));
        put("speech", new App("speech", "ServiceSpeech", "apk", "BuildServiceSpeech"));
        put("vision", new App("vision", "ServiceVision", "apk", "BuildServiceVision"));
        put("updater", new App("updater", "Updater", "apk", "BuildUpdater"));
        put("core", new App("core", "BuddyCore", "apk", "BuildCore"));
        put("sdk", new App("sdk", "BuddySDK", "aar", "BuildSDK"));
    }};
}