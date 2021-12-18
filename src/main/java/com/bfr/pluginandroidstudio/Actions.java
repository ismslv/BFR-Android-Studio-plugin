package com.bfr.pluginandroidstudio;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Actions {

    public static void showNotification(Project iProject, String iText, NotificationType iType) {
        NotificationGroup _group = new NotificationGroup("bfrplugin", NotificationDisplayType.BALLOON, true);
        _group.createNotification("BFR action", iText, iType, null).notify(iProject);
    }

    public static void showError(String iMessage) {
        Messages.showErrorDialog(iMessage, "Error");
    }

    public static void askQuestion(Project iProject, String iTitle, String iMessage, String[] iOptions, Consumer<Integer> iCallback) {
        int _answer = Messages.showDialog(iProject, iMessage, iTitle, iOptions, 0, null);
        if (iCallback != null)
            iCallback.accept(_answer);
    }

    public static void setMenuEnabledIfDevice(AnActionEvent iEvent) {
        Project _project = iEvent.getData(CommonDataKeys.PROJECT);
        boolean _isDevice = DeviceManager.isDevice();
        iEvent.getPresentation().setEnabled(_isDevice);
    }

    public static void tryCallScrcpy(Project iProject, Runnable iCallbackOnFail) {
        GeneralCommandLine generalCommandLine = new GeneralCommandLine("scrcpy");
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(iProject.getBasePath());

        ProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(generalCommandLine);
            processHandler.startNotify();
        } catch (ExecutionException ex) {
            if (iCallbackOnFail != null && ex.getMessage().contains("Cannot run program \"scrcpy\""))
                iCallbackOnFail.run();
            else
                System.out.println(ex.getMessage());
        }
    }

    public static String getCommandOutput(InputStream iStream) {
        return new BufferedReader(
            new InputStreamReader(iStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    public static String[] getId(AnAction iAction) {
        return ActionManager.getInstance().getId(iAction).split("_");
    }
}
