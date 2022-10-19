package com.bfr.pluginandroidstudio;

import com.intellij.execution.*;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.ExecutionManagerImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.macro.MacroManager;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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

    public static void showFileInExplorer(String iPath, boolean iIsFolder) {
//        String command = "explorer.exe "
//                      + (iIsFolder ? "" : "/")
//                      + "select,\""
//                      + iPath.replace("/", "\\")
//                      + "\"";
//        try {
//            Runtime.getRuntime().exec(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        File file = new File(iPath);
        if (!iIsFolder) file = new File(file.getParent());

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void copyFile(String iPathFrom, String iPathTo) {
        String command = "xcopy "
                      + "\""
                      + iPathFrom.replace("/", "\\")
                      + "\" \""
                      + iPathTo.replace("/", "\\")
                      + "\"";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDir(String iPath) {
        try {
            Runtime.getRuntime().exec("cmd /c rmdir /S /Q \"" + iPath.replace("/", "\\") + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openURL(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getBuildConfig(String iApp, String iAct) {
        String oConfName;
        if (iApp.equals("all"))
            oConfName = "Build Everything";
        else {
            switch (iAct) {
                case "buildrun":
                    oConfName = Common.APPS.get(iApp).BuildRunConfig;
                    break;
                case "build":
                default:
                    oConfName = Common.APPS.get(iApp).BuildConfig;
                    break;
            }
        }
        return oConfName;
    }

    public static void runConf(String iName, AnActionEvent iEvent, Runnable iAction) {
        RunManagerEx runManager = RunManagerEx.getInstanceEx(ProjectManager.getProject());
        RunnerAndConfigurationSettings runConfig = runManager.findConfigurationByName(iName);

        if (runConfig == null) {
            System.out.println("Unable to find Run configuration with name: " + iName);
            return;
        }

        Executor executor = Executor.EXECUTOR_EXTENSION_NAME.getExtensionList().get(0);
        if (executor == null) {
            System.out.println("Unable to find Executor");
            return;
        }
        //String executorId = executor.getId();
        //String executionTargetId = ExecutionTargetManager.getInstance(iProject).getActiveTarget().getId();

        ExecutionTarget target = getExecutionTarget(ProjectManager.getProject(), runConfig, null);
        MacroManager.getInstance().cacheMacrosPreview(iEvent.getDataContext());
        ExecutionUtil.doRunConfiguration(runConfig, executor, target, null, iEvent.getDataContext());

        if (iAction != null) {
            Disposable disposable = Disposer.newDisposable();
            iEvent.getProject().getMessageBus().connect(disposable).subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                @Override
                public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {

                }

                @Override
                public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
                    if (env.getExecutionTarget().getDisplayName().equals(target.getDisplayName()))
                        iAction.run();
                    Disposer.dispose(disposable);
                }
            });
        }
    }

    public static File getLastFileInDir(String iDirName, FileFilter iFilter) {
        File dir = new File(iDirName);

        if (!dir.exists() || !dir.isDirectory())
            return null;

        File[] files = dir.listFiles(iFilter);
        if (files != null && files.length > 0) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            return files[files.length - 1];
        }

        return null;
    }

    @NotNull
    private static ExecutionTarget getExecutionTarget(@NotNull Project project, @NotNull RunnerAndConfigurationSettings runConfig, String iTargetId) {
        ExecutionTargetManager targetManager = ExecutionTargetManager.getInstance(project);
        ExecutionTarget active = targetManager.getActiveTarget();
        if (iTargetId == null || iTargetId.equals(active.getId())) {
            return active;
        }

        List<ExecutionTarget> targets = targetManager.getTargetsFor(runConfig.getConfiguration());
        for (ExecutionTarget target : targets) {
            if (target.getId().equals(iTargetId)) {
                return target;
            }
        }
        return active;
    }
}
