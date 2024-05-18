package com.tcpip147.nodeformatter.css;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.tcpip147.nodeformatter.ipc.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CssCompletionContributor extends CompletionContributor {

    public CssCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if ("css".equals(parameters.getOriginalFile().getVirtualFile().getExtension())) {
                    int line = parameters.getEditor().getDocument().getLineNumber(parameters.getOffset());
                    int column = parameters.getOffset() - parameters.getEditor().getDocument().getLineStartOffset(line);
                    NodeJsProtocol request = new NodeJsProtocol(ProcessType.COMPLETION, LanguageType.CSS, parameters.getOriginalFile().getVirtualFile().toString());
                    request.putData("text", parameters.getEditor().getDocument().getText());
                    request.putData("line", line);
                    request.putData("column", column);
                    NodeJsProtocol response = NodeJsProcess.getInstance().write(request);
                    List<String> candidates = (List<String>) response.getData("completion");
                    for (String candidate : candidates) {
                        result.addElement(LookupElementBuilder.create(candidate));
                    }
                }
            }
        });
    }
}
