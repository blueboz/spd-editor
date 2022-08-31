package cn.boz.jb.plugin.idea.toolwindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
    }
}
