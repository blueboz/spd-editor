package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.codegen.dlg.EngineActionSelectorDlg;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class EngineActionSelectorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        EngineActionSelectorDlg engineActionSelectorDlg = new EngineActionSelectorDlg(anActionEvent.getProject(), true);
        engineActionSelectorDlg.show();
    }
}
