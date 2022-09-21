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

public class CopyQueryEngineActionScriptAction extends CopyScriptBaseAction{


     protected String engineActionSql(EngineAction engineAction) {
        String id=engineAction.getId();
        StringBuilder sqls = new StringBuilder();
        sqls.append(String.format("select * from ENGINE_ACTION where ID_='%s';", id));
        sqls.append("\n\n");
        sqls.append(String.format("select * from ENGINE_ACTIONINPUT where ACTIONID_='%s';", id));
        sqls.append("\n\n");
        sqls.append(String.format("select * from ENGINE_ACTIONOUTPUT where ACTIONID_='%s';", id));
        sqls.append("\n");
        return sqls.toString();
    }

     protected String engineTaskSql(EngineTask engineTask) {
        String id = engineTask.getId();
        StringBuilder sqls = new StringBuilder();
        sqls.append(String.format("select * from ENGINE_TASK where ID_='%s';", id));
        sqls.append("\n\n");
        sqls.append(String.format("select * from ENGINE_FLOW where PROCESSID_='%s';", id));
        sqls.append("\n");
        return sqls.toString();
    }
}
