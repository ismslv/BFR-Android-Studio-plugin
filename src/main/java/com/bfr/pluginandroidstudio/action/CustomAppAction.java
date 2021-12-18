package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.*;
import com.intellij.notification.NotificationType;
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

public class CustomAppAction extends AnAction {
    private String[] ids;

    @Override
    public void actionPerformed(@NotNull AnActionEvent iEvent) {
        if (ids[1].equals("install")) {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell("mkdir -p /sdcard/Updates/Local");
                DeviceManager.CURRENT_DEVICE.executeShell("rm /sdcard/Updates/Local/*");
            } catch (IOException | JadbException e) {
                e.printStackTrace();
            }
            VirtualFile virtualFile = FileChooser.chooseFile(new FileChooserDescriptor(
                    true, false, false, false, false, false
            ), null, null);
            if (virtualFile != null && virtualFile.exists() && virtualFile.getPath().endsWith(".apk")) {
                try {
                    pushFile(virtualFile);
                    DeviceManager.CURRENT_DEVICE.executeShell("am start -n " + Common.APPS.get("updater").LaunchPackage);
                } catch (IOException | JadbException e) {
                    e.printStackTrace();
                }
            } else {
                Actions.showError("Bad file");
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (ids == null)
            ids = Actions.getId(this);
        Actions.setMenuEnabledIfDevice(iEvent);
    }

    void pushFile(VirtualFile iFile) throws IOException, JadbException {
        DeviceManager.CURRENT_DEVICE.push(
                new File(iFile.getPath()),
                new RemoteFile("sdcard/Updates/Local/" + iFile.getNameWithoutExtension() + ".apk")
        );
    }
}
