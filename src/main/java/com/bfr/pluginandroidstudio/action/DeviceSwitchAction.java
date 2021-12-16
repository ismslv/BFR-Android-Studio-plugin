package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.DeviceManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeviceSwitchAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String[] _devices = new String[DeviceManager.CONNECTED_DEVICES.size()];
        for (int i = 0; i < _devices.length; i++) {
            _devices[i] = "Unknown";
            for (String[] _d : DeviceManager.ROBOTS) {
                if (_d[1].equals(DeviceManager.CONNECTED_DEVICES.get(i)))
                    _devices[i] = _d[0];
            }
            _devices[i] += "|" + DeviceManager.CONNECTED_DEVICES.get(i);
        }
        Messages.showChooseDialog("Choose device", "Multiple devices connected", _devices, _devices[0], null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

    }
}