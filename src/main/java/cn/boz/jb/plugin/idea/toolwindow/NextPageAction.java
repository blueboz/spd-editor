package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.codegen.dlg.EngineActionSelectorDlg;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

public class NextPageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        InputEvent inputEvent = anActionEvent.getInputEvent();
        Component component = inputEvent.getComponent();
        Container ancestorOfClass = SwingUtilities.getAncestorOfClass(MenuIdDialog.class, component);
        if (ancestorOfClass instanceof MenuIdDialog) {
            MenuIdDialog dlg = (MenuIdDialog) ancestorOfClass;
            dlg.loadNextPage();
        }

        DialogWrapper instance = DialogWrapper.findInstance(component);
        if(instance instanceof EngineActionSelectorDlg){
            EngineActionSelectorDlg dlg = (EngineActionSelectorDlg) instance;
            dlg.loadNextPage();
            return ;
        }
    }
}
