package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.dialog.min.EngineActionDerivePanel;
import cn.boz.jb.plugin.idea.dialog.min.EngineTaskDerivePanel;
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

public class CopyUpdateEngineActionScriptAction extends CopyScriptBaseAction {

    @Override
    protected String engineActionSql(EngineAction action) {
        StringBuilder sqls = new StringBuilder();
        sqls.append(String.format("UPDATE ENGINE_ACTION SET ACTIONSCRIPT_='%s' WHERE ID_='%s';", action.getActionscript(), action.getId()));
        sqls.append("\n");
        return sqls.toString();
    }

    @Override
    protected String engineTaskSql(EngineTask engineTask) {

        StringBuilder sqls = new StringBuilder();
        sqls.append(String.format("update ENGINE_TASK set EXPRESSION_='%s' where ID_='%s';", engineTask.getExpressionRaw(), engineTask.getId()));
        sqls.append("\n");
        return sqls.toString();
    }

}
