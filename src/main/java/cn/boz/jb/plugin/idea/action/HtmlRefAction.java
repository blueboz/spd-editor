package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.dialog.EcasMenuDialog;
import cn.boz.jb.plugin.idea.dialog.EcasMenuTreeDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

public class HtmlRefAction extends AnAction {
    private JBPopup popup;
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile.getName();
        DBUtils instance = DBUtils.getInstance();
        try {
            EcasMenuTreeDialog ecasMenuTreeDialog = new EcasMenuTreeDialog(e.getProject(), fileName);
            popup = JBPopupFactory.getInstance().createComponentPopupBuilder(ecasMenuTreeDialog, null)
                    .setRequestFocus(true)
                    .setTitle("Html Ref For:"+fileName)
                    .setMovable(true)
                    .setProject(e.getProject()).setFocusable(true).setCancelOnOtherWindowOpen(true).createPopup();

            //应该
            popup.showCenteredInCurrentWindow(e.getProject());


        } catch (Exception ex) {
            DBUtils.dbExceptionProcessor(ex,e.getProject());
        }

    }

    public void showTablePopup(List<EcasMenu> ecasMenus, Project project) {
        EcasMenuDialog ecasMenuDialog = new EcasMenuDialog(ecasMenus);
        ecasMenuDialog.show();

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (psiFile == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        FileType fileType = psiFile.getFileType();
        if (fileType instanceof HtmlFileType) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }


    }
}
