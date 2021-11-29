package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.apache.tools.ant.taskdefs.Exec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BuildAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);
        String _id = ActionManager.getInstance().getId(this);
        String[] _ids = _id.split("_");

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("gradlew.bat");

        String _pathToBuild = "";

        if (!_ids[1].equals("all")) {
            String _moduleName = getModuleName(_ids[1]);
            _pathToBuild += ":" + _moduleName + ":";
        }
        _pathToBuild += "build";
        cmds.add(_pathToBuild);

        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("cmd.exe /c cd \""+_project.getBasePath()+"\" & start cmd.exe /k \"" + String.join(" ", cmds) + "\"");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getModuleName(String iID) {
        switch (iID) {
            case "core":
                return "BuddyCore";
            case "sdk":
                return "BuddySDK";
            case "updater":
                return iID;
            default:
                return "service" + iID;
        }
    }
}
