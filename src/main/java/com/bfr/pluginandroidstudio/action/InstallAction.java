package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.*;

public class InstallAction extends AnAction {

    Project mProject;
    String[] ids;
    String length;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        mProject = e.getProject();

        if (ids[2].equals("install")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/tmp");
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        }

        if (!ids[1].equals("all")) {
            performTask(ids[2], Common.APPS.get(ids[1]));
        } else {
            Common.APPS.forEach((s, app) -> {
                if (app.FileType.equals("apk"))
                    performTask(ids[2], app);
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (ids == null)
            ids = Actions.getId(this);
        iEvent.getPresentation().setEnabled(DeviceManager.isDevice());
    }

    String getCommand(String iType, App iApp) {
        String oCmd = "";

        switch (iType) {
            case "stop":
                oCmd += "am force-stop ";
                break;
            case "uninstall":
                oCmd += "pm uninstall ";
                break;
            case "install":
                oCmd += "cat sdcard/tmp/" + iApp.FullID + ".apk | pm install -t -S " + length + " & input keyevent 3";
                break;
            case "launch":
                oCmd += "am start -n " + iApp.LaunchPackage;
                break;
        }

        if (iType.equals("stop") || iType.equals("uninstall"))
            oCmd += "com.bfr.buddy." + iApp.ID;

        return oCmd;
    }

    void pushFile(App iApp) throws IOException, JadbException {
        DeviceManager.CURRENT_DEVICE.push(
            new File(iApp.getLocalFilePath()),
            new RemoteFile("sdcard/tmp/" + iApp.FullID + ".apk")
        );
    }

    void pushFile(String iFilePath, App iApp) throws IOException, JadbException {
        DeviceManager.CURRENT_DEVICE.push(
                new File(iFilePath),
                new RemoteFile("sdcard/Updates/Local/" + iApp.FullID + ".apk")
        );
    }

    void performTask(String iType, App iApp) {
        try {
            if (iType.equals("relaunch")) {
                DeviceManager.CURRENT_DEVICE.executeShell(getCommand("stop", iApp));
                iType = "launch";
            }

            if (iType.equals("install"))
                if (new File(iApp.getLocalFilePath()).exists())
                    length = "" + new File(iApp.getLocalFilePath()).length();

            String _cmd = getCommand(iType, iApp);
            if (iType.equals("install")) {
                if (new File(iApp.getLocalFilePath()).exists()) {
                    pushFile(iApp);
                    DeviceManager.CURRENT_DEVICE.executeShell(_cmd);
                } else {
                    VirtualFile virtualFile = FileChooser.chooseFile(new FileChooserDescriptor(
                            true, false, false, false, false, false
                    ), null, null);
                    if (virtualFile != null && virtualFile.exists() && virtualFile.getPath().endsWith(".apk")) {
                        pushFile(virtualFile.getPath(), iApp);
                        DeviceManager.CURRENT_DEVICE.executeShell(_cmd);
                    } else
                        return;
                }
            } else {
                DeviceManager.CURRENT_DEVICE.executeShell(_cmd);
            }
        } catch (IOException | JadbException e) {
            e.printStackTrace();
        }

        if (iType.equals("install") && iApp.ID.equals("core")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("appops set com.bfr.buddy.core SYSTEM_ALERT_WINDOW allow");
                DeviceManager.CURRENT_DEVICE.executeShell("appops set com.bfr.buddy.core WRITE_SETTINGS allow");
                DeviceManager.CURRENT_DEVICE.executeShell("cmd package set-home-activity com.bfr.buddy.core/.LauncherActivity");
                DeviceManager.CURRENT_DEVICE.executeShell("input keyevent 3");
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        }

        Actions.showNotification(mProject, "Completed: " + iType + " " + iApp.FullID, NotificationType.INFORMATION);
    }
}
