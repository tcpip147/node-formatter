package com.tcpip147.nodeformatter.css;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CssFileType extends LanguageFileType {

    public static final CssFileType INSTANCE = new CssFileType();
    public static final Icon ICON = IconLoader.getIcon("/css.png", CssFileType.class);
    public static final String EXTENSION = "css";

    protected CssFileType() {
        super(CssLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Css";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Css Language File";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }
}
