package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.DeviceManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.File;
import java.io.IOException;

public class ConfigAction extends AnAction {
    private String mPath;
    private String mType;
    private static final String CONFIG_SYSTEM = "System";
    private static final String CONFIG_USER_DEF = "Default user";
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        String[] ids = ActionManager.getInstance().getId(this).split("_");
        String idConfig = ids[1];
        String idAction = ids[2];

        try {
            DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p " + Common.CONFIG_SYSTEM_REMOTE);
            DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p " + Common.CONFIG_USER_REMOTE.replace("[USERNAME]", "Default"));
        } catch (IOException | JadbException ex) {
            ex.printStackTrace();
        }

        String checkExtension = ids[1].equals("apps") ? ".json" : ".xml";

        if (idAction.equals("push") || idAction.equals("pull") || idAction.equals("remove")) {
            // Configs
            String _remoteFilePath = getConfigRemotePath(idConfig);

            switch (idAction) {
                case "pull":
                    VirtualFile virtualFilePull = FileChooser.chooseFile(
                            new FileChooserDescriptor(false, true, false, false, false, false),
                            e.getProject(), null);
                    if (virtualFilePull != null && virtualFilePull.exists()) {
                        try {
                            DeviceManager.CURRENT_DEVICE.pull(
                                    new RemoteFile(_remoteFilePath),
                                    new File(virtualFilePull.getPath() + "/" + getConfigFileName(idConfig))
                            );
                        } catch (IOException | JadbException ex) {
                            ex.printStackTrace();
                        }
                    } else
                        return;
                    break;
                case "push":
                    VirtualFile virtualFilePush = FileChooser.chooseFile(
                            new FileChooserDescriptor(true, false, false, false, false, false),
                            e.getProject(), null);
                    if (virtualFilePush != null && virtualFilePush.exists() && virtualFilePush.getPath().endsWith(checkExtension))
                        if (isFileGood(idConfig, virtualFilePush)) {
                            try {
                                DeviceManager.CURRENT_DEVICE.push(
                                        new File(virtualFilePush.getPath()),
                                        new RemoteFile(_remoteFilePath)
                                );
                            } catch (IOException | JadbException ex) {
                                ex.printStackTrace();
                            }
                        } else
                            return;
                    break;
                case "remove":
                    try {
                        DeviceManager.CURRENT_DEVICE.executeShell("rm " + _remoteFilePath);
                    } catch (IOException | JadbException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
        } else if (idAction.equals("devmodeon")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("dpm remove-active-admin com.bfr.buddy.core/.AdminReceiver");
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        } else if (idAction.equals("devmodeoff")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("dpm set-device-owner com.bfr.buddy.core/.AdminReceiver");
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        Actions.setMenuEnabledIfDevice(iEvent);
    }

    String getConfigRemotePath(String iType) {
        if (iType.equals("system")) {
            return Common.CONFIG_SYSTEM_REMOTE + getConfigFileName(iType);
        } else if (iType.equals("user") || iType.equals("apps")) {
            return Common.CONFIG_USER_REMOTE.replace("[USERNAME]", "Default") + getConfigFileName(iType);
        }

        return "";
    }

    String getConfigFileName(String iType) {
        switch (iType) {
            case "system":
                return "cfg_system.xml";
            case "user":
                return "cfg_user.xml";
            case "apps":
                return "applications.json";
        }

        return "";
    }

    boolean isFileGood(String iType, VirtualFile iFile) {
        String _ext = iFile.getFileType().getDefaultExtension();
        return (iType.equals("apps") && _ext.equals("json")) || _ext.equals("xml");
    }
}
