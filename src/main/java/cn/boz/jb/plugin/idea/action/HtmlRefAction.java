package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.dialog.CallerSearcherCommentPanel;
import cn.boz.jb.plugin.idea.dialog.EcasMenuDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableCellRenderer;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.List;

public class HtmlRefAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile.getName();
        System.out.println(fileName);
        DBUtils instance = DBUtils.getInstance();
        try {
            Connection connection = DBUtils.getConnection();
            List<EcasMenu> result = instance.queryHtmlRefMenu(connection, fileName);
            showTablePopup(result, e.getProject());

        } catch (Exception ex) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            ex.printStackTrace(printWriter);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            try {
                Messages.showErrorDialog("Error while Getting Connection\n" + new String(bytes, "UTF-8"), "Error");
            } catch (UnsupportedEncodingException exc) {
                exc.printStackTrace();
            }
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
