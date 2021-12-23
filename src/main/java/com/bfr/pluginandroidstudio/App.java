package com.bfr.pluginandroidstudio;

import java.io.File;

public class App {
    public String ID;
    public String FullID;
    public String FileType;
    public String BuildConfig;
    public String BuildRunConfig;
    public String LaunchPackage;

    public App(String iID, String iFullID, String iFileType, String iBuildConfig, String iBuildRunConfig, String iLaunchPackage) {
        ID = iID;
        FullID = iFullID;
        FileType = iFileType;
        BuildConfig = iBuildConfig;
        BuildRunConfig = iBuildRunConfig;
        LaunchPackage = iLaunchPackage;
    }

    public String getOutputDir() {
        return ProjectManager.getProject().getBasePath() + "/" + FullID + "/build/outputs/" + FileType + "/";
    }

    public String getLocalFilePath() {
        String oPath = ProjectManager.getProject().getBasePath() + "/" + FullID + "/build/outputs/" + FileType + "/";

        if (FileType.equals("apk")) {
            File _f = new File(oPath + "release/" + FullID + "-release.apk");
            if (_f.exists())
                return _f.getAbsolutePath();

            _f = new File(oPath + "debug/" + FullID + "-debug.apk");
            if (_f.exists())
                return _f.getAbsolutePath();

            return "";
        } else {
            File _f = new File(oPath + FullID + "-release.aar");
            if (_f.exists())
                return _f.getAbsolutePath();

            _f = new File(oPath + FullID + "-debug.aar");
            if (_f.exists())
                return _f.getAbsolutePath();

            return "";
        }
    }
}