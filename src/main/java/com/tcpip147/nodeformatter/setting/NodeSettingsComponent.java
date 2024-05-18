package com.tcpip147.nodeformatter.setting;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class NodeSettingsComponent {

    private JPanel panel;
    private TextFieldWithBrowseButton tfNodeExecutable = new TextFieldWithBrowseButton();

    public NodeSettingsComponent() {
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel("NodeJS Executable: "), tfNodeExecutable, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        tfNodeExecutable.addBrowseFolderListener("Select...", null, null, descriptor);
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getNodeExecutable() {
        return tfNodeExecutable.getText();
    }

    public void setNodeExecutable(String text) {
        tfNodeExecutable.setText(text);
    }
}
