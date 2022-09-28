package com.bfr.pluginandroidstudio;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

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
        String oPath = getOutputDir();
        FileFilter filter = new WildcardFileFilter(FullID + "_*." + FileType);

        File fileRelease = Actions.getLastFileInDir(oPath + "release/", filter);
        if (fileRelease != null)
            return fileRelease.getAbsolutePath();

        File fileDebug = Actions.getLastFileInDir(oPath + "debug/", filter);
        if (fileDebug != null)
            return fileDebug.getAbsolutePath();

        return "";
    }
}