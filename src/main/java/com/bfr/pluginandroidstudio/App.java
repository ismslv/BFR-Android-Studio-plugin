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

        File fileRelease = getLastFileFromDir(oPath + "release/", filter);
        if (fileRelease != null)
            return fileRelease.getAbsolutePath();

        File fileDebug = getLastFileFromDir(oPath + "debug/", filter);
        if (fileDebug != null)
            return fileDebug.getAbsolutePath();

        return "";
    }

    private File getLastFileFromDir(String iDirName, FileFilter iFilter) {
        File dir = new File(iDirName);

        if (!dir.exists() || !dir.isDirectory())
            return null;

        File[] files = dir.listFiles(iFilter);
        if (files != null && files.length > 0) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            return files[files.length - 1];
        }

        return null;
    }
}