package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.codegen.dlg.XFunCodeDefDlg;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class XFunCodeDefInvoker extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        XFunCodeDefDlg xFunCodeDefDlg = new XFunCodeDefDlg(anActionEvent.getProject());
        xFunCodeDefDlg.setModal(false);
        xFunCodeDefDlg.setSize(800, 600);
        xFunCodeDefDlg.show();
    }
}
