package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.idea.dialog.EcasMenuTreeTableDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class EcasMenuTreeTableAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        EcasMenuTreeTableDialog ecasMenuTreeTableDialog = new EcasMenuTreeTableDialog(anActionEvent.getProject(),true);
        ecasMenuTreeTableDialog.setModal(false);
        ecasMenuTreeTableDialog.setSize(800,600);
        ecasMenuTreeTableDialog.show();

    }
}
