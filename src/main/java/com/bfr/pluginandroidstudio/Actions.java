package com.bfr.pluginandroidstudio;

import com.bfr.pluginandroidstudio.tools.DeviceManager;
import com.google.wireless.android.sdk.stats.AndroidStudioEvent;
import com.google.wireless.android.sdk.stats.GradleLibrary;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;

public class Actions {
    public static void showNotification(Project iProject, String iText, NotificationType iType) {
        NotificationGroup _group = new NotificationGroup("bfrplugin", NotificationDisplayType.BALLOON, true);
        _group.createNotification("BFR action", iText, iType, null).notify(iProject);
    }

    public static void setMenuEnabled(AnActionEvent iEvent) {
        Project _project = iEvent.getData(CommonDataKeys.PROJECT);
        boolean _isDevice = DeviceManager.isDevice();
        iEvent.getPresentation().setEnabled(_isDevice);
    }

    public static void setMenuDisabled(AnActionEvent iEvent) {
        Project _project = iEvent.getData(CommonDataKeys.PROJECT);
        boolean _isDevice = DeviceManager.isDevice();
        iEvent.getPresentation().setEnabled(!_isDevice);
    }

    void a() {

    }
}
