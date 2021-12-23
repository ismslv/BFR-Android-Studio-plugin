package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.File;
import java.io.IOException;

public class BuildAction extends AnAction {
    String[] ids;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (ids[2].equals("build") || ids[2].equals("buildrun")) {
            String confName = Actions.getBuildConfig(ids[1], ids[2]);
            Actions.runConf(confName, e, null);
        } else if (ids[2].equals("buildinstall")) {
            App app = Common.APPS.get(ids[1]);
            Actions.removeDir(app.getOutputDir() + "release");
            Actions.removeDir(app.getOutputDir() + "debug");
            String confName = Actions.getBuildConfig(ids[1], ids[2]);
            Actions.runConf(confName, e, new Runnable() {
                @Override
                public void run() {
                    String localFilePath = app.getLocalFilePath();
                    if (!localFilePath.isEmpty()) {
                        try {
                            DeviceManager.CURRENT_DEVICE.push(
                                    new File(localFilePath),
                                    new RemoteFile("sdcard/tmp/" + app.FullID + ".apk")
                            );
                            DeviceManager.CURRENT_DEVICE.executeShell("pm install -t sdcard/tmp/" + app.FullID + ".apk");
                        } catch (IOException | JadbException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (ids == null)
            ids = Actions.getId(this);
        if (ids[2].equals("build"))
            iEvent.getPresentation().setEnabled(ProjectManager.isBuddyCore);
        else
            iEvent.getPresentation().setEnabled(ProjectManager.isBuddyCore && DeviceManager.isDevice());
    }
}
