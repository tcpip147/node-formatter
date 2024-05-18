package com.tcpip147.nodeformatter.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "com.tcpip147.nodeformatter.setting.NodeSettingsState", storages = @Storage("NodeFormatter.xml"))
@Getter
@Setter
public class NodeSettingsState implements PersistentStateComponent<NodeSettingsState> {

    private String nodeExecutable = "";

    public static NodeSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(NodeSettingsState.class);
    }

    @Override
    public @Nullable NodeSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull NodeSettingsState state) {
        nodeExecutable = state.getNodeExecutable();
    }
}
