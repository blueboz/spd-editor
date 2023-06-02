package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GenerateEngineActionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (editor == null || psiFile == null || virtualFile == null) {
            return;
        }
        Project project = anActionEvent.getProject();
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getContextOfType(element, PsiMethod.class);
        if(method==null){
            NotificationUtils.warnning("无法定位方法","请移动到方法名或者方法作用域",anActionEvent.getProject());
            return;
        }
        PsiParameterList parameterList = method.getParameterList();
        PsiType returnType = method.getReturnType();


//        PsiElement psiRoot = PsiManager.getInstance(project).findFile(virtualFile);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof JavaFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
                return;
            }
        }

    }
}
