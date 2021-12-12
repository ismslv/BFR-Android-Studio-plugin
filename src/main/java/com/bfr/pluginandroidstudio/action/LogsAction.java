package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.DeviceManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class LogsAction extends AnAction {

    private String[] mId;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile _virtualFilePull = FileChooser.chooseFile(
                new FileChooserDescriptor(false, true, false, false, false, false),
                e.getProject(), null);
        if (_virtualFilePull != null && _virtualFilePull.exists()) {
            try {
                DeviceManager.CURRENT_DEVICE.pull(
                        new RemoteFile(getExternalPath()),
                        new File(_virtualFilePull.getPath() + "/" + getFileName())
                );
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        }
    }

    String getExternalPath() {
        switch (mId[2]) {
            case "update":
                return Common.CONFIG_LOGS_CUSTOM + getFileName();
            default:
                return "";
        }
    }

    String getFileName() {
        switch (mId[2]) {
            case "update":
                return "updateSession.log";
            default:
                return "";
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (mId == null)
            mId = Actions.getId(this);
        if (mId[2].equals("update"))
            Actions.setMenuEnabledIfDevice(iEvent);
        else
            iEvent.getPresentation().setEnabled(false);
    }
}