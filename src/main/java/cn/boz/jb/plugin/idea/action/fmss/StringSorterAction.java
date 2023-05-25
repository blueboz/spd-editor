package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.idea.action.fmss.dialog.StringSorterDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class StringSorterAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        StringSorterDialog stringSorterDialog = new StringSorterDialog(anActionEvent.getProject(),true);
        stringSorterDialog.show();
    }
}
