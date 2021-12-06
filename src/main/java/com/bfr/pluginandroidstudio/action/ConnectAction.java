package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.DeviceManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.ConnectionToRemoteDeviceException;
import se.vidstige.jadb.JadbException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ConnectAction extends AnAction {

    private Project mProject;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        mProject = e.getRequiredData(CommonDataKeys.PROJECT);

        boolean _isDevice = DeviceManager.isDevice();

        if (_isDevice) {
            try {
                DeviceManager.ADB.disconnectFromTcpDevice(new InetSocketAddress(DeviceManager.CURRENT_IP, 5555));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            ArrayList<String> cmds = new ArrayList<>();
            cmds.add("adb");

            String last_ip = DeviceManager.PREFS.get("LAST_IP", "192.168.1.0");

            String _ip = Messages.showInputDialog(mProject, "Input the IP of the device", "Device IP", null, last_ip, null);
            if (_ip != null && !_ip.isEmpty()) {
                DeviceManager.PREFS.put("LAST_IP", _ip);
                cmds.add("connect");
                cmds.add(_ip);
            } else
                return;

            try {
                DeviceManager.ADB.connectToTcpDevice(new InetSocketAddress(_ip, 5555));
            } catch (IOException | ConnectionToRemoteDeviceException | JadbException ex) {
                ex.printStackTrace();
            }

//            GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
//            generalCommandLine.setCharset(Charset.forName("UTF-8"));
//            generalCommandLine.setWorkDirectory(mProject.getBasePath());
//
//            ProcessHandler processHandler = null;
//            try {
//                processHandler = new OSProcessHandler(generalCommandLine);
//                processHandler.startNotify();
//            } catch (ExecutionException ex) {
//                ex.printStackTrace();
//            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        iEvent.getPresentation().setText(DeviceManager.isDevice() ? "Disconnect" : "Connect");
    }
}