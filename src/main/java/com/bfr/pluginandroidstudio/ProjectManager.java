package com.bfr.pluginandroidstudio;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class ProjectManager {
    private static Project mProject;
    public static boolean isBuddyCore;

    public static Project getProject() {
        return mProject;
    }

    public static void setProject(AnActionEvent iEvent) {
        if (mProject == iEvent.getProject()) return;

        mProject = iEvent.getProject();
        if (mProject != null)
            isBuddyCore = mProject.getName().equals("BuddyOS");
        else
            isBuddyCore = false;
    }


}
