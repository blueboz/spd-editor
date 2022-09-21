package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherCommentPanel;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherDetailComment;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherTablePanel;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.dialog.min.EngineActionDerivePanel;
import cn.boz.jb.plugin.idea.dialog.min.EngineTaskDerivePanel;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public abstract class CopyScriptBaseAction extends AnAction implements ClipboardOwner {

    protected AnActionEvent currentAnAction = null;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.currentAnAction = anActionEvent;
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (requiredData instanceof JBTable) {
            process((JBTable) requiredData);
        } else if (requiredData instanceof JBScrollPane) {
            JBScrollPane jbScrollPane = (JBScrollPane) requiredData;
            JViewport viewport = jbScrollPane.getViewport();
            Component view = viewport.getView();
            processLogic(view);

        } else {
            processLogic(requiredData);
        }
    }

    private void processLogic(Component view) {
        if (view instanceof EngineTaskDerivePanel) {
            doEngineTask(((EngineTaskDerivePanel) view).getEngineTask());
        } else if (view instanceof EngineActionDerivePanel) {
            doEngineAction(((EngineActionDerivePanel) view).getEngineAction());
        } else if (view instanceof EngineTaskDialog) {
            doEngineTask(((EngineTaskDialog) view).getEngineTask());
        } else if (view instanceof EngineActionDialog) {
            doEngineAction(((EngineActionDialog) view).getEngineAction());
        } else {
            Container container = SwingUtilities.getAncestorOfClass(EngineTaskDerivePanel.class, view);
            if (container != null) {
                processLogic(container);
            }

            container = SwingUtilities.getAncestorOfClass(EngineActionDerivePanel.class, view);
            if (container != null) {
                processLogic(container);
            }

            container = SwingUtilities.getAncestorOfClass(EngineTaskDialog.class, view);
            if (container != null) {
                processLogic(container);
            }

            container = SwingUtilities.getAncestorOfClass(EngineActionDialog.class, view);
            if (container != null) {
                processLogic(container);
            }
            container = SwingUtilities.getAncestorOfClass(CallerSearcherDetailComment.class, view);
            if(container!=null){
                CallerSearcherDetailComment callerSearcherDetailComment= (CallerSearcherDetailComment) container;
                CallerSearcherTablePanel table = callerSearcherDetailComment.getTable();
                process(table);
            }
            container = SwingUtilities.getAncestorOfClass(CallerSearcherCommentPanel.class, view);
            if(container!=null){
                CallerSearcherCommentPanel callerSearcherCommentPanel= (CallerSearcherCommentPanel) container;
                CallerSearcherTablePanel table = callerSearcherCommentPanel.getTable();
                process(table);
            }


        }
    }

    public void doEngineAction(EngineAction engineAction) {
        try {
            String sql = engineActionSql(engineAction);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(sql.toString());
            clipboard.setContents(selection, this);
        } catch (Exception e) {
            DBUtils.dbExceptionProcessor(e, currentAnAction.getProject());

        }
    }

    protected abstract String engineActionSql(EngineAction action);

    protected abstract String engineTaskSql(EngineTask engineTask);

    public void doEngineTask(EngineTask engineTask) {
        String sql = engineTaskSql(engineTask);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(sql.toString());
        clipboard.setContents(selection, this);
    }

    private void process(JBTable jbTable) {
//            JBTable jbTablle = GoToRefFile.this.jbTable;
        int selectedRow = jbTable.getSelectedRow();
        int modelidx = jbTable.convertRowIndexToModel(selectedRow);
        //如何拿到当前控件
        ListTableModel model = (ListTableModel) jbTable.getModel();
        Object item = model.getItem(modelidx);

        if (item instanceof EngineTask) {
            EngineTask engineTask = (EngineTask) item;
            doEngineTask(engineTask);
        } else if (item instanceof EngineAction) {
            EngineAction engineAction = (EngineAction) item;
            doEngineAction(engineAction);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }
}
