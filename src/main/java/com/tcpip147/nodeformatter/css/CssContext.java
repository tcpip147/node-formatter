package com.tcpip147.nodeformatter.css;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class CssContext {

    private boolean modified;
    private Project project;
    private VirtualFile file;

    public CssContext(Project project, VirtualFile file) {
        this.project = project;
        this.file = file;
    }

    public boolean isModified() {
        return modified;
    }

    public Project getProject() {
        return project;
    }

    public VirtualFile getFile() {
        return file;
    }
}
