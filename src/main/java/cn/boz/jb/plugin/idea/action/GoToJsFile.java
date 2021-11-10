package cn.boz.jb.plugin.idea.action;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 用于
 */
public class GoToJsFile extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);

        PsiElement element = psiFile.findElementAt(offset);
        XmlAttribute parentOfType = PsiTreeUtil.getParentOfType(element, XmlAttribute.class);
        if (parentOfType != null) {
            PsiElement pareddnt = element.getParent();
            //才有继续处理的必要

            XmlAttribute attribute = (XmlAttribute) parentOfType;
            String name = attribute.getName();
            String value = attribute.getValue();
            if ("data-main".equals(name)) {
                PsiDirectory directory = psiFile.getContainingDirectory();
//                PsiFile file = directory.findFile(value);
                //RelatiePalce

                String[] split = value.split("\\?");
                VirtualFile fileByRelativePath = psiFile.getVirtualFile().getParent().findFileByRelativePath(split[0]);

                PsiFile targetJsFile = PsiManager.getInstance(project).findFile(fileByRelativePath);

                JBPopup jbpopup = NavigationUtil.getPsiElementPopup(new PsiElement[]{targetJsFile}, "选择文件");
                jbpopup.showCenteredInCurrentWindow(project);
                return;
            }
        }
        //parent must be data-main

    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof HtmlFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
            } else {
                e.getPresentation().setEnabled(false);
                e.getPresentation().setVisible(false);
            }
        }
    }
}
