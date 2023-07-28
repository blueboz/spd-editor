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
                resultSet.addElement(LookupElementBuilder.create("Blueboz"));
                resultSet.addElement(LookupElementBuilder.create("package"));
                resultSet.addElement(LookupElementBuilder.create("className"));
                resultSet.addElement(LookupElementBuilder.create("package"));
                resultSet.addElement(LookupElementBuilder.create("beanName"));
                resultSet.addElement(LookupElementBuilder.create("namespace"));
                resultSet.addElement(LookupElementBuilder.create("tableName"));
                resultSet.addElement(LookupElementBuilder.create("menuId"));
                resultSet.addElement(LookupElementBuilder.create("pmenuId"));
                resultSet.addElement(LookupElementBuilder.create("powerbits"));
                resultSet.addElement(LookupElementBuilder.create("moduleName"));
                resultSet.addElement(LookupElementBuilder.create("functionbase"));
                resultSet.addElement(LookupElementBuilder.create("title"));
                resultSet.addElement(LookupElementBuilder.create("exceptionFullName"));
                resultSet.addElement(LookupElementBuilder.create("exceptionName"));

            }
        });

    }


}
