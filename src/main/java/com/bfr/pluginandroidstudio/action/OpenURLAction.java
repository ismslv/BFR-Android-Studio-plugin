package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.DeviceManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class OpenURLAction extends AnAction {
    String[] ids;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (ids[1].equals("server"))
            Actions.openURL(Common.UPDATE_SERVERS.get(ids[2]));
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (ids == null) ids = Actions.getId(this);
    }
}
