package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
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
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ConfigAction extends AnAction {
    private String mPath;
    private String mType;
    private static final String CONFIG_SYSTEM = "System";
    private static final String CONFIG_USER_DEF = "Default user";
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);
        String _id = ActionManager.getInstance().getId(this);

        try {
            DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/Configs/System");
            DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/Configs/Users/Default");
        } catch (IOException | JadbException ex) {
            ex.printStackTrace();
        }

        if (_id.equals("config_pull")) {
            mType = Messages.showEditableChooseDialog("What config to get?", "Choose config", null,
                    new String[]{CONFIG_SYSTEM, CONFIG_USER_DEF}, CONFIG_SYSTEM, null);
            if (mType != null && !mType.isEmpty()) {
                VirtualFile _virtualFile = FileChooser.chooseFile(
                        new FileChooserDescriptor(false, true, false, false, false, false),
                        e.getProject(), null);
                if (_virtualFile != null && _virtualFile.exists()) {
                    mPath = _virtualFile.getPath();
                    try {
                        DeviceManager.CURRENT_DEVICE.pull(
                            new RemoteFile(getConfigRemotePath()),
                            new File(mPath + "/" + getConfigFileName())
                        );
                    } catch (IOException | JadbException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                    return;
            } else
                return;
        } else if (_id.equals("config_push")) {
            mType = Messages.showEditableChooseDialog("What config to send?", "Choose config", null,
                    new String[]{"System", "Default user"}, "System", null);
            if (mType != null && !mType.isEmpty()) {
                VirtualFile _virtualFile = FileChooser.chooseFile(
                        new FileChooserDescriptor(true, false, false, false, false, false),
                        e.getProject(), null);
                if (_virtualFile != null && _virtualFile.exists())
                    if (_virtualFile.getFileType().getDefaultExtension().equals("xml")) {
                        mPath = _virtualFile.getPath();
                        try {
                            DeviceManager.CURRENT_DEVICE.push(
                                    new File(mPath),
                                    new RemoteFile(getConfigRemotePath())
                            );
                        } catch (IOException | JadbException ex) {
                            ex.printStackTrace();
                        }
                    }
                else
                    return;
            } else
                return;
        } else if (_id.equals("config_remove")) {
            mType = Messages.showEditableChooseDialog("What config to delete?", "Choose config", null,
                    new String[]{"System", "Default user"}, "System", null);
            if (mType != null && mType.isEmpty()) {
                try {
                    DeviceManager.CURRENT_DEVICE.executeShell("rm " + getConfigRemotePath());
                } catch (IOException | JadbException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (_id.equals("devmode_on")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("dpm remove-active-admin com.bfr.buddy.core/.AdminReceiver");
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        } else if (_id.equals("devmode_off")) {
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

    String getConfigRemotePath() {
        if (mType.equals(CONFIG_SYSTEM)) {
            return "sdcard/Configs/System/" + getConfigFileName();
        } else if (mType.equals(CONFIG_USER_DEF)) {
            return "sdcard/Configs/Users/Default/" + getConfigFileName();
        }

        return "";
    }

    String getConfigFileName() {
        if (mType.equals(CONFIG_SYSTEM)) {
            return "cfg_system.xml";
        } else if (mType.equals(CONFIG_USER_DEF)) {
            return "cfg_user.xml";
        }

        return "";
    }
}
