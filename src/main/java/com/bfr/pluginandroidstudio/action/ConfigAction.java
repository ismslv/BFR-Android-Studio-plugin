package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.google.wireless.android.sdk.stats.GradleBuildProject;
import com.google.wireless.android.sdk.stats.GradleLibrary;
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
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class ConfigAction extends AnAction {
    private String mPath;
    private String mType;
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);
        String _id = ActionManager.getInstance().getId(this);

        ArrayList<String> _commands = null;

        if (_id.equals("config_pull")) {
            mType = Messages.showEditableChooseDialog("What config to get?", "Choose config", null,
                    new String[]{"System", "Default user"}, "System", null);
            if (mType != null && !mType.isEmpty()) {
                VirtualFile _virtualFile = FileChooser.chooseFile(
                        new FileChooserDescriptor(false, true, false, false, false, false),
                        e.getProject(), null);
                if (_virtualFile != null && _virtualFile.exists())
                    mPath = _virtualFile.getPath();
                else
                    return;
            } else
                return;
        } else if (_id.equals("config_push")) {
            mType = Messages.showEditableChooseDialog("What config to send?", "Choose config", null,
                    new String[]{"System", "Default user"}, "System", null);
            if (mType != null && !mType.isEmpty()) {
                VirtualFile _virtualFile = FileChooser.chooseFile(
                        new FileChooserDescriptor(true, false, false, false, false, false),
                        e.getProject(), null);
                if (_virtualFile != null && _virtualFile.exists())
                    if (_virtualFile.getFileType().getDefaultExtension().equals("xml"))
                        mPath = _virtualFile.getPath();
                else
                    return;
            } else
                return;
        } else if (_id.equals("config_remove")) {
            mType = Messages.showEditableChooseDialog("What config to delete?", "Choose config", null,
                    new String[]{"System", "Default user"}, "System", null);
            if (mType == null || mType.isEmpty())
                return;
        }

        _commands = getCommands(e.getProject().getBasePath());

        if (_commands == null || _commands.isEmpty())
            return;

        GeneralCommandLine generalCommandLine = new GeneralCommandLine(_commands);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(_project.getBasePath());

        ProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(generalCommandLine);
            processHandler.addProcessListener(new ProcessListener() {
                @Override
                public void startNotified(@NotNull ProcessEvent event) {

                }

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    if (event.getExitCode() != 0)
                        Actions.showNotification(e.getProject(), "Command error", NotificationType.ERROR);
                }

                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    Actions.showNotification(e.getProject(), event.getText(), NotificationType.INFORMATION);
                }
            });
            processHandler.startNotify();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        Actions.setMenuEnabled(iEvent);
    }

    ArrayList<String> getCommands(String iProjectPath) {
        String _id = ActionManager.getInstance().getId(this);
        ArrayList<String> oCmds = new ArrayList<>();
        if (_id.equals("config_permission")) {
            //oCmds.add(iProjectPath + "/Scripts/config.bat");
        } else if (_id.equals("config_folders")) {
            oCmds.add(iProjectPath + "/Scripts/files_folders.bat");
        } else if (_id.equals("config_push")) {
            if (mPath != null && !mPath.isEmpty()) {
                oCmds.add("adb");
                oCmds.add("push");
                oCmds.add(mPath);
                if (mType.equals("System"))
                    oCmds.add("sdcard/Configs/System/cfg_system.xml");
                else
                    oCmds.add("sdcard/Configs/Users/Default/cfg_user.xml");
            }
        } else if (_id.equals("config_pull")) {
            if (mPath != null && !mPath.isEmpty()) {
                oCmds.add("adb");
                oCmds.add("pull");
                if (mType.equals("System"))
                    oCmds.add("sdcard/Configs/System/cfg_system.xml");
                else
                    oCmds.add("sdcard/Configs/Users/Default/cfg_user.xml");
                oCmds.add(mPath);
            }
        } else if (_id.equals("config_remove")) {
            oCmds.add("adb");
            oCmds.add("shell");
            oCmds.add("rm");
            if (mType.equals("System"))
                oCmds.add("sdcard/Configs/System/cfg_system.xml");
            else
                oCmds.add("sdcard/Configs/Users/Default/cfg_user.xml");
        } else if (_id.equals("devmode_on")) {
            oCmds.add(iProjectPath + "/Scripts/devmode.bat");
            oCmds.add("on");
        } else if (_id.equals("devmode_off")) {
            oCmds.add(iProjectPath + "/Scripts/devmode.bat");
            oCmds.add("off");
        }
        return oCmds;
    }
}
