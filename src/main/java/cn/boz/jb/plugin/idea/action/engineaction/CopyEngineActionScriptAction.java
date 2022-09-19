package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.dialog.min.EngineActionDerivePanel;
import cn.boz.jb.plugin.idea.dialog.min.EngineTaskDerivePanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class CopyEngineActionScriptAction extends AnAction implements ClipboardOwner {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (requiredData instanceof JBTable) {
            process((JBTable) requiredData);
        }else if(requiredData instanceof EngineTaskDerivePanel) {
            EngineTaskDerivePanel engineTaskDerivePanel = (EngineTaskDerivePanel) requiredData;
            String id = engineTaskDerivePanel.getEngineTask().getId();
            StringBuilder sqls = new StringBuilder();
            sqls.append(String.format("select * from ENGINE_TASK where ID_='%s';", id));
            sqls.append("\n");
            sqls.append(String.format("select * from ENGINE_FLOW where PROCESSID_='%s';", id));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(sqls.toString());
            clipboard.setContents(selection, this);
        }else if(requiredData instanceof EngineActionDerivePanel){
            EngineActionDerivePanel derivePanel= (EngineActionDerivePanel) requiredData;
            String id = derivePanel.getId();
            StringBuilder sqls = new StringBuilder();
            sqls.append(String.format("select * from ENGINE_ACTION where ID_='%s';", id));
            sqls.append("\n");
            sqls.append(String.format("select * from ENGINE_ACTIONINPUT where ACTIONID_='%s';", id));
            sqls.append("\n");
            sqls.append(String.format("select * from ENGINE_ACTIONOUTPUT where ACTIONID_='%s';", id));

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(sqls.toString());
            clipboard.setContents(selection, this);
        }

    }

    private void process( JBTable jbTable) {
//            JBTable jbTablle = GoToRefFile.this.jbTable;
        int selectedRow = jbTable.getSelectedRow();
        int modelidx = jbTable.convertRowIndexToModel(selectedRow);
        //如何拿到当前控件
        ListTableModel model = (ListTableModel) jbTable.getModel();
        Object item = model.getItem(modelidx);

        if (item instanceof EngineTask) {
            EngineTask engineTask = (EngineTask) item;
            String id = engineTask.getId();
            StringBuilder sqls = new StringBuilder();
            sqls.append(String.format("select * from ENGINE_TASK where ID_='%s';", id));
            sqls.append("\n");
            sqls.append(String.format("select * from ENGINE_FLOW where PROCESSID_='%s';", id));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(sqls.toString());
            clipboard.setContents(selection, this);
        } else if (item instanceof EngineAction) {
            EngineAction engineAction = (EngineAction) item;
            String id = engineAction.getId();

            StringBuilder sqls = new StringBuilder();
            sqls.append(String.format("select * from ENGINE_ACTION where ID_='%s';", id));
            sqls.append("\n");
            sqls.append(String.format("select * from ENGINE_ACTIONINPUT where ACTIONID_='%s';", id));
            sqls.append("\n");
            sqls.append(String.format("select * from ENGINE_ACTIONOUTPUT where ACTIONID_='%s';", id));

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(sqls.toString());
            clipboard.setContents(selection, this);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }
}
