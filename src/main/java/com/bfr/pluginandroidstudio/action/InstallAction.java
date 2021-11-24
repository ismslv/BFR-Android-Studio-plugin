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
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class InstallAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String _id = ActionManager.getInstance().getId(this);
        String[] _ids = _id.split("_");
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);

        ArrayList<String> cmds = new ArrayList<>();
        if (_ids[2].equals("install")) {
            cmds.add(_project.getBasePath() + "/Scripts/install.bat");
            cmds.add(_ids[1]);
        } else if (_ids[2].equals("uninstall")) {
            cmds.add(_project.getBasePath() + "/Scripts/uninstall.bat");
            cmds.add(_ids[1]);
        } else if (_ids[2].equals("stop")) {
            cmds.add(_project.getBasePath() + "/Scripts/stop.bat");
            cmds.add(_ids[1]);
        } else if (_ids[2].equals("launch")) {
            cmds.add("adb");
            cmds.add("shell");
            cmds.add("am start -n com.bfr.buddy.core/com.bfr.buddy.core.LauncherActivity");
        }
        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(_project.getBasePath() + "/Scripts");

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
                      Actions.showNotification(e.getProject(), _ids[1] + " not installed", NotificationType.ERROR);
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
}
