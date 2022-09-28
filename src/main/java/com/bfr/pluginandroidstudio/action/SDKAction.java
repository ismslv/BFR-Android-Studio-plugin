package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.DeviceManager;
import com.bfr.pluginandroidstudio.ProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;

public class SDKAction extends AnAction {

    Project mProject;
    String[] ids;

    boolean mIsRelease = false;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        mProject = e.getProject();
        String name = ids[1];
        if (!name.equals("sdk")) name = "sdk-" + name;

        switch (ids[2]) {
            case "publish":
                mIsRelease = ids[3].equals("release");
                Actions.runConf((mIsRelease ? "RELEASE" : "SNAPSHOT") + " Publish " + name, e, null);
                break;
            case "build":
                Actions.runConf("Build " + name, e, null);
                break;
            case "showfile":
                File _file = null;
                FileFilter filter = new WildcardFileFilter("*.aar");
                switch (name) {
                    case "sdk":
                        _file = Actions.getLastFileInDir(ProjectManager.getProject().getBasePath() + "/BuddySDK/build/outputs/aar/", filter);
                        break;
                    case "sdk-utils":
                        _file = Actions.getLastFileInDir(ProjectManager.getProject().getBasePath() + "/BuddyCore/utils/build/outputs/aar/", filter);
                        break;
                    case "sdk-ui":
                    case "sdk-network":
                        _file = Actions.getLastFileInDir(ProjectManager.getProject().getBasePath() + "/BuddyCore/shared" + ids[1] + "/build/outputs/aar/", filter);
                        break;
                    default:
                        _file = Actions.getLastFileInDir(ProjectManager.getProject().getBasePath() + "/Service" + ids[1] + "/shared" + ids[1] + "/build/outputs/aar/", filter);
                        break;
                }
                if (_file != null)
                    Actions.showFileInExplorer(_file.getAbsolutePath(), false);
                else
                    Actions.showError("No file found. You have to build it first.");
                break;
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (ids == null)
            ids = Actions.getId(this);
        iEvent.getPresentation().setEnabled(ProjectManager.isBuddyCore);
    }
}
