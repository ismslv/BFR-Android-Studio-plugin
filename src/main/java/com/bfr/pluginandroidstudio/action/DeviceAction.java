package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class DeviceAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);

        GeneralCommandLine generalCommandLine = new GeneralCommandLine(getCommands());
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(_project.getBasePath());

        ProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(generalCommandLine);
            processHandler.startNotify();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        Actions.setMenuEnabled(iEvent);
    }

    ArrayList<String> getCommands() {
        String _id = ActionManager.getInstance().getId(this);
        ArrayList<String> oCmds = new ArrayList<>();
        if (_id.equals("device_scrcpy")) {
            oCmds.add("scrcpy");
        } else if (_id.equals("device_reboot")) {
            oCmds.add("adb");
            oCmds.add("reboot");
        } else if (_id.equals("device_shutdown")) {
            oCmds.add("adb");
            oCmds.add("reboot");
            oCmds.add("-p");
        } else if (_id.equals("device_disconnect")) {
            oCmds.add("adb");
            oCmds.add("disconnect");
        } else if (_id.equals("device_settings")) {
            oCmds.add("adb");
            oCmds.add("shell");
            oCmds.add("am start -a android.settings.SETTINGS");
        } else if (_id.equals("device_apps")) {
            oCmds.add("adb");
            oCmds.add("shell");
            oCmds.add("am start -a android.settings.APPLICATION_SETTINGS");
        } else if (_id.equals("device_wifi")) {
            oCmds.add("adb");
            oCmds.add("shell");
            oCmds.add("am start -a android.settings.WIFI_SETTINGS");
        }
        return oCmds;
    }
}
