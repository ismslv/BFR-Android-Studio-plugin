package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.tools.DeviceManager;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class ConnectAction extends AnAction {

    private Project mProject;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        mProject = e.getRequiredData(CommonDataKeys.PROJECT);

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("adb");

        boolean _isDevice = DeviceManager.isDevice();

        if (_isDevice) {
            cmds.add("disconnect");
        } else {
            String last_ip = DeviceManager.PREFS.get("LAST_IP", "192.168.1.0");

            String _ip = Messages.showInputDialog(mProject, "Input the IP of the device", "Device IP", null, last_ip, null);
            if (_ip != null && !_ip.isEmpty()) {
                DeviceManager.PREFS.put("LAST_IP", _ip);
                cmds.add("connect");
                cmds.add(_ip);
            }
        }

        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(mProject.getBasePath());

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
        boolean _isDevice = DeviceManager.isDevice();
        iEvent.getPresentation().setText(_isDevice ? "Disconnect" : "Connect");
    }
}