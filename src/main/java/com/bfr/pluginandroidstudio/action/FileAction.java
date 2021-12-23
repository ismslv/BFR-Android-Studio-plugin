package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.App;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.ProjectManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FileAction  extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);

        String _id = ActionManager.getInstance().getId(this);
        String[] _ids = _id.split("_");

        if (_ids[2].equals("showfile")) {
            String _path = Common.APPS.get(_ids[1]).getLocalFilePath(_project.getBasePath());
            if (new File(_path).exists()) {
                try {
                    Runtime.getRuntime().exec("explorer.exe /select,\"" + _path.replace("/", "\\") + "\"");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (_ids[2].equals("collect")) {
            VirtualFile virtualFolder = FileChooser.chooseFile(
                    new FileChooserDescriptor(false, true, false, false, false, false),
                    e.getProject(), null);
            if (virtualFolder != null && virtualFolder.exists()) {
                int toIncludeBat = Messages.showDialog("Do you want to include install.bat?", "Collect files", new String[]{"No", "Yes"}, 0, null);
                if (toIncludeBat == -1)
                    return;
                List<String> files = new ArrayList<>();
                Common.APPS.forEach((s, app) -> {
                    if (app.FileType.equals("apk")) {
                        String localFilePath = app.getLocalFilePath(e.getProject().getBasePath());
                        if (!localFilePath.isEmpty())
                            files.add(localFilePath.replace("/", "\\"));
                    }
                });
                if (!files.isEmpty()) {
                    files.forEach(f -> {
                        try {
                            Runtime.getRuntime().exec("xcopy " + "\"" + f + "\" \"" + virtualFolder.getPath().replace("/", "\\") + "\"");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    try {
                        if (toIncludeBat == 1)
                            Runtime.getRuntime().exec("xcopy " + "\"" + e.getProject().getBasePath().replace("/", "\\") + "\\Scripts\\install.bat\" \"" + virtualFolder.getPath().replace("/", "\\") + "\"");
                        Runtime.getRuntime().exec("explorer.exe select,\"" + virtualFolder.getPath().replace("/", "\\") + "\"");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else
                    Actions.showError("No files found. You have to build project.");
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        iEvent.getPresentation().setEnabled(ProjectManager.isBuddyCore);
    }
}