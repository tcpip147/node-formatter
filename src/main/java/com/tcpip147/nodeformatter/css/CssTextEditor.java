package com.tcpip147.nodeformatter.css;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.tcpip147.nodeformatter.ipc.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CssTextEditor extends JPanel implements NodeJsDataListener {

    private CssContext ctx;
    private Editor editor;

    public CssTextEditor(CssContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout());
        NodeJsProcess.getInstance().addDataListener(this);

        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("css");
        Document document = FileDocumentManager.getInstance().getDocument(ctx.getFile());
        editor = EditorFactory.getInstance().createEditor(document, ctx.getProject(), fileType, false);
        editor.getContentComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.isAltDown() && e.getKeyCode() == 76) {
                    NodeJsProtocol request = new NodeJsProtocol(ProcessType.FORMATTER, LanguageType.CSS, ctx.getFile().toString());
                    request.putData("text", editor.getDocument().getText());
                    NodeJsProcess.getInstance().write(request);
                }
            }
        });
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String filename = ctx.getFile().toString();
                String text = editor.getDocument().getText();
                NodeJsProtocol request = new NodeJsProtocol(ProcessType.HIGHLIGHTER, LanguageType.CSS, filename);
                request.putData("text", text);
                NodeJsProcess.getInstance().write(request);
            }
        });
        add(editor.getComponent(), BorderLayout.CENTER);
    }

    public void releaseMemory() {
        NodeJsProcess.getInstance().removeDataListener(this);
        EditorFactory.getInstance().releaseEditor(editor);
    }

    @Override
    public void listen(NodeJsProtocol protocol) {
        if (ctx.getFile().toString().equals(protocol.getFilename())) {
            ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
                WriteCommandAction.runWriteCommandAction(ctx.getProject(), () -> {
                    if (ProcessType.FORMATTER.equals(protocol.getProcessType())) {
                        editor.getDocument().setText((String) protocol.getData("text"));
                    }
                });
            });
        }
    }
}
