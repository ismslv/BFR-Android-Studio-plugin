package com.bfr.pluginandroidstudio.tools;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;

public class DeviceManager {

    public static Preferences PREFS;

    private static JadbConnection ADB;
    public static String CURRENT_IP;
    public static List<String[]> ROBOTS;

    private static Timer mTimer;
    private static boolean isInit = init();

    private static boolean init() {
        PREFS = Preferences.userNodeForPackage(DeviceManager.class);
        ADB = new JadbConnection();
        CURRENT_IP = "";
        ROBOTS = getDeviceList();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    CURRENT_IP = "";
                    List<JadbDevice> _devices = ADB.getDevices();
                    for (JadbDevice _d : _devices) {
                        if (_d.getState() == JadbDevice.State.Device) {
                            CURRENT_IP = _d.getSerial();
                            break;
                        }
                    }
                } catch (IOException | JadbException e) {
                    //e.printStackTrace();
                    CURRENT_IP = "";
                }
            }
        }, 0, 2000);
        return true;
    }

    public static boolean isDevice() {
        if (!isInit) init();
        return !CURRENT_IP.isEmpty();
    }

    public static List<String[]> getDeviceList() {
        List<String[]> oDevices = new ArrayList<>();
        String _d = PREFS.get("DEVICES", "");
        if (!_d.isEmpty()) {
            String[] _split = _d.split("\\|\\|");
            if (_split.length > 0) {
                for (String _s : _split) {
                    String[] _sd = _s.split("\\|");
                    oDevices.add(new String[] {_sd[0], _sd[1]});
                }
            }
        }
        return oDevices;
    }

    public static void saveDevices() {
        List<String> oD = new ArrayList<>();
        for (String[] _s : ROBOTS)
            oD.add(String.join("|", _s));
        String oJoin = String.join("||", oD);
        PREFS.put("DEVICES", oJoin);
    }

    public static String getRobotIP(String iName) {
        String oIP = "";
        if (ROBOTS.size() > 0) {
            for (String[] _s : ROBOTS) {
                if (_s[0].equals(iName)) {
                    oIP = _s[1];
                    break;
                }
            }
        }
        return oIP;
    }

    public static String getRobotName(String iIP) {
        String oName = "Untitled";
        if (ROBOTS.size() > 0) {
            for (String[] _s : ROBOTS) {
                if (_s[1].equals(iIP)) {
                    oName = _s[0];
                    break;
                }
            }
        }
        return oName;
    }

    public static void addOrReplace(String iIP, String iName) {
        boolean _found = false;
        if (ROBOTS.size() > 0) {
            for (String[] _s : ROBOTS) {
                if (_s[1].equals(iIP)) {
                    _s[0] = iName;
                    _found = true;
                    break;
                }
            }
        }
        if (!_found) {
            ROBOTS.add(new String[] {iName, iIP});
        }
    }

    public static void remove(String iIPOrName) {
        int _id = -1;
        if (ROBOTS.size() > 0)
            for (int i = 0; i < ROBOTS.size(); i++)
                if (ROBOTS.get(i)[0].equals(iIPOrName) || ROBOTS.get(i)[1].equals(iIPOrName)) {
                    _id = i;
                    break;
                }
        if (_id >= 0)
            ROBOTS.remove(_id);
    }

    public static String[] getNames() {
        return DeviceManager.ROBOTS.stream().map(s -> s[0]).toArray(String[]::new);
    }
}
