package com.bfr.pluginandroidstudio.action;

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
        String _path = getPath(_project.getBasePath(), _ids[1]);
        if (new File(_path).exists()) {
            try {
                System.out.println("explorer.exe /select," + _path.replace("/", "\\"));
                Runtime.getRuntime().exec("explorer.exe /select," + _path.replace("/", "\\"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    String getPath(String iBasePath, String iID) {
        String _path = iBasePath + "/";
        String _name = "";
        String _dir1 = "apk";
        switch (iID) {
            case "core":
                _name = "BuddyCore";
                break;
            case "updater":
                _name = "Updater";
                break;
            case "sdk":
                _name = "BuddySDK";
                _dir1 = "aar";
                break;
            default:
                _name = "service" + iID;
                break;
        }
        _path += _name + "/build/outputs/" + _dir1 + "/";

        if (_dir1.equals("apk")) {
            File _f = new File(_path + "release/");
            if (_f.exists())
                return _path + "release/" + _name + "-release.apk";
            else
                return _path + "debug/" + _name + "-debug.apk";
        } else {
            if (new File(_path + _name + "-release.aar").exists())
                return _path + _name + "-release.aar";
            else
                return _path + _name + "-debug.aar";
        }
    }
}