package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.idea.dialog.ActionPowerTreeTableDialog;
import cn.boz.jb.plugin.idea.dialog.EcasMenuTreeTableDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ActionPowerTreeTableAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        ActionPowerTreeTableDialog actionPowerTreeTableDialog = new ActionPowerTreeTableDialog(anActionEvent.getProject(),true);
        actionPowerTreeTableDialog.setModal(false);
        actionPowerTreeTableDialog.setSize(800,600);
        actionPowerTreeTableDialog.show();

    }
}
