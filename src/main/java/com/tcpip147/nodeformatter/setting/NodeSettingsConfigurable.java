package com.tcpip147.nodeformatter.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NodeSettingsConfigurable implements Configurable {

    private NodeSettingsComponent component;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Node Formatter";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new NodeSettingsComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        NodeSettingsState state = NodeSettingsState.getInstance();
        boolean modified = !component.getNodeExecutable().equals(state.getNodeExecutable());
        return modified;
    }

    @Override
    public void apply() {
        NodeSettingsState state = NodeSettingsState.getInstance();
        state.setNodeExecutable(component.getNodeExecutable());
    }

    @Override
    public void reset() {
        NodeSettingsState state = NodeSettingsState.getInstance();
        component.setNodeExecutable(state.getNodeExecutable());
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
