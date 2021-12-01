package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.App;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.tools.DeviceManager;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.omg.CORBA.OMGVMCID;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InstallAction extends AnAction {

    Project mProject;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String _id = ActionManager.getInstance().getId(this);
        String[] _ids = _id.split("_");
        mProject = e.getProject();

        if (_ids[2].equals("install")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/Updates/Firmware");
                DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/Updates/Software");
                DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/Updates/Local");
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        }

        if (!_ids[1].equals("all")) {
            performTask(_ids[2], Common.APPS.get(_ids[1]));
        } else {
            Common.APPS.forEach((s, app) -> {
                if (app.FileType.equals("apk"))
                    performTask(_ids[2], app);
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (iEvent.getProject() == null) {
            iEvent.getPresentation().setEnabled(false);
            return;
        }
        iEvent.getPresentation().setEnabled(iEvent.getProject().getName().equals("BuddyCore") && DeviceManager.isDevice());
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
                oCmd += "pm install -t ";
                break;
            case "launch":
                oCmd += "am start -n com.bfr.buddy.core/com.bfr.buddy.core.LauncherActivity";
                break;
        }

        if (iType.equals("stop") || iType.equals("uninstall"))
            oCmd += "com.bfr.buddy." + iApp.ID;
        else if (iType.equals("install"))
            oCmd += "sdcard/Updates/Local/" + iApp.FullID + ".apk";

        return oCmd;
    }

    void pushFile(App iApp) throws IOException, JadbException {
        DeviceManager.CURRENT_DEVICE.push(
            new File(iApp.getLocalFilePath(mProject.getBasePath())),
            new RemoteFile("sdcard/Updates/Local/" + iApp.FullID + ".apk")
        );
    }

    void performTask(String iType, App iApp) {
        try {
            if (iType.equals("install")) {
                if (new File(iApp.getLocalFilePath(mProject.getBasePath())).exists())
                    pushFile(iApp);
                else {
                    Actions.showNotification(mProject, "No apk. Build " + iApp.FullID + " before installing.", NotificationType.INFORMATION);
                    return;
                }
            }
            String _cmd = getCommand(iType, iApp);
            DeviceManager.CURRENT_DEVICE.executeShell(_cmd);
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
