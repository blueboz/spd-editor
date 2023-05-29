package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.dialog.EngineRightDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class CopyEngineRightSqlAction extends AnAction  implements ClipboardOwner {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component component = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        EngineRightDialog dialog=null;
        if(component  instanceof EngineRightDialog){
           dialog= (EngineRightDialog) component;

        }
        if (component instanceof JBScrollPane) {
            JBScrollPane jbScrollPane = (JBScrollPane) component;
            JViewport viewport = jbScrollPane.getViewport();
            Component view = viewport.getView();
            if (view instanceof EngineRightDialog) {
                dialog= (EngineRightDialog) view;
            }
        }
        if(dialog==null){
            Container ancestorOfClass = SwingUtilities.getAncestorOfClass(EngineRightDialog.class, component);
            if(ancestorOfClass!=null &&ancestorOfClass instanceof EngineRightDialog){
                dialog= (EngineRightDialog) ancestorOfClass;
            }
        }
        if(dialog!=null){
            String s = dialog.generateSql();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(s);
            clipboard.setContents(selection, this);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
