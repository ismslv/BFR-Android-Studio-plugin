package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.ProjectManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class FileAction  extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);

        String _id = ActionManager.getInstance().getId(this);
        String[] _ids = _id.split("_");
        String _path = Common.APPS.get(_ids[1]).getLocalFilePath(_project.getBasePath());
        if (new File(_path).exists()) {
            try {
                Runtime.getRuntime().exec("explorer.exe /select," + _path.replace("/", "\\"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        iEvent.getPresentation().setEnabled(ProjectManager.isBuddyCore);
    }
}