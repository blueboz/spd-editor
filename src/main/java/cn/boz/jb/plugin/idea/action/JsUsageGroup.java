package cn.boz.jb.plugin.idea.action;

import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class JsUsageGroup extends DefaultActionGroup {


    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            try {
                Class.forName("com.intellij.lang.javascript.JavaScriptFileType");
                if (fileType instanceof JavaScriptFileType) {
                    e.getPresentation().setEnabledAndVisible(true);
                    return;
                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        e.getPresentation().setEnabledAndVisible(false);
    }


    static class JsUsageFindAction extends AnAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
            if (editor == null || psiFile == null) {
                return;
            }
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof JavaScriptFileType) {
                String fileName = virtualFile.getName();
                FindInProjectManager instance = FindInProjectManager.getInstance(anActionEvent.getProject());
                FindModel findModel = new FindModel();
                findModel.setStringToFind(fileName);
                instance.findInProject(anActionEvent.getDataContext(), findModel);
            }
        }
    }

    static class JsUsageAction extends AnAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
            if (editor == null || psiFile == null) {
                return;
            }
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof JavaScriptFileType) {
                String cpath = virtualFile.getPath();
                String ppath = virtualFile.getParent().getParent().getPath();
                String destpath = cpath.replace(ppath, "");
                destpath = destpath.substring(1);
                String replace = destpath.replace(".js", ".html");
                GoToRefFile.goToPathSearchRecusive(psiFile, anActionEvent.getProject(), replace);

            }
        }
    }

}



