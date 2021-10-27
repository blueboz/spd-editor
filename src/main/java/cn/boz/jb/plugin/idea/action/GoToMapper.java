package cn.boz.jb.plugin.idea.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.BaseCodeInsightAction;
import com.intellij.lang.CodeInsightActions;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public class GoToMapper extends BaseCodeInsightAction implements DumbAware {

    @Override
    protected @NotNull CodeInsightActionHandler getHandler() {
        return new CodeInsightActionHandler() {

            @Override
            public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
                int offset = editor.getCaretModel().getOffset();
                PsiElement psiElement = psiFile.findElementAt(offset);
//                PsiElement psiElement = PsiTreeUtil.getParentOfType(element, new Class[]{PsiFunctionalExpression.class, PsiMember.class});

                PsiTreeUtil.
                Language language = PsiUtilCore.getLanguageAtOffset(psiFile, offset);
                CodeInsightActionHandler actionHandler = CodeInsightActions.GOTO_SUPER.forLanguage(language);
                if(actionHandler!=null){
                    DumbService.getInstance(project).withAlternativeResolveEnabled(()->{
                        actionHandler.invoke(project,editor,psiFile);
                    });
                }


            }
        };
    }
}


