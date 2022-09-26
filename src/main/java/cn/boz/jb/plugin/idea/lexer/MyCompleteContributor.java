package cn.boz.jb.plugin.idea.lexer;


import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class MyCompleteContributor extends CompletionContributor {

    public MyCompleteContributor() {
        this.extend(CompletionType.BASIC,PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters,
                                          @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {

                resultSet.addElement(LookupElementBuilder.create("Hello"));

            }
        });

    }


}
