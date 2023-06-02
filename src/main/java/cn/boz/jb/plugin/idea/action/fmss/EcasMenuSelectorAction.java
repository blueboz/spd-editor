package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.codegen.dlg.EcasMenuIdSelectorDlg;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class EcasMenuSelectorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        EcasMenuIdSelectorDlg engineActionSelectorDlg = new EcasMenuIdSelectorDlg(anActionEvent.getProject(), true);
        engineActionSelectorDlg.show();
    }
}
