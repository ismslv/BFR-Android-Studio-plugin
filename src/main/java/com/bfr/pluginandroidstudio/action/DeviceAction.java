package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.tools.DeviceManager;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.JadbException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DeviceAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);
        String _id = ActionManager.getInstance().getId(this);

        if (_id.equals("device_scrcpy")) {
            Actions.tryCallScrcpy(_project, () -> {
                String _scrcpyAddress = DeviceManager.PREFS.get("SCRCPY", "");
                if (_scrcpyAddress != null && !_scrcpyAddress.isEmpty() && new File(_scrcpyAddress).exists()) {
                    // Run from memory
                    try {
                        Runtime.getRuntime().exec(_scrcpyAddress);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Is not saved - ask for
                    FileChooserDescriptor _fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
                    _fileChooserDescriptor.setTitle("Choose scrcpy.exe");
                    VirtualFile _scrcpyFile = FileChooser.chooseFile(_fileChooserDescriptor, _project, null);
                    if (_scrcpyFile != null && _scrcpyFile.getPath().endsWith("scrcpy.exe")) {
                        // Good file
                        _scrcpyAddress = _scrcpyFile.getPath();
                        DeviceManager.PREFS.put("SCRCPY", _scrcpyAddress);
                        try {
                            Runtime.getRuntime().exec(_scrcpyAddress);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        } else {
            try {
                DeviceManager.CURRENT_DEVICE.executeShell(getCommand(_id));
            } catch (IOException | JadbException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        Actions.setMenuEnabledIfDevice(iEvent);
    }

    String getCommand(String iId) {

        switch (iId) {
            case "device_reboot":
                return "reboot";
            case "device_shutdown":
                return "reboot -p";
            case "device_settings":
                return "am start -a android.settings.SETTINGS";
            case "device_apps":
                return "am start -a android.settings.APPLICATION_SETTINGS";
            case "device_wifi":
                return "am start -a android.settings.WIFI_SETTINGS";
        }

        return "";
    }
}
