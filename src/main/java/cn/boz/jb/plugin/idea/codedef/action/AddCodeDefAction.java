package cn.boz.jb.plugin.idea.codedef.action;

import cn.boz.jb.plugin.idea.codedef.dialog.AddCodeDefDialog;
import cn.boz.jb.plugin.idea.toolwindow.AddMenuDialog;
import cn.boz.jb.plugin.idea.toolwindow.NodeData;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

/**
 * 用于添加备选代码的
 */
public class AddCodeDefAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        AddCodeDefDialog addCodeDefDialog = new AddCodeDefDialog(anActionEvent.getProject(),true);
        addCodeDefDialog.showAndGet();

    }
}
