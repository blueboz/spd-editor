package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.EndEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.StartEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.action.flowSearch.FlowSearchCommentPanel;
import cn.boz.jb.plugin.idea.action.flowSearch.FlowSearchTable;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Consumer;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.List;

public class FindInSpdEditorAction extends DumbAwareAction {

    public FindInSpdEditorAction() {
    }

    private ChartPanel chartPanel;

    public FindInSpdEditorAction(ChartPanel chartPanel) {
        //拷贝快捷键
        //注册快捷
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_FIND);
        if (action != null) {
            this.copyShortcutFrom(action);
        }
        this.setEnabledInModalContext(true);
        this.chartPanel = chartPanel;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        List<PropertyObject> propertyObjects = chartPanel.getAllPropertyObject();
        showListPopup(propertyObjects, anActionEvent.getProject(), o -> {
            chartPanel.selectObject(o);
            chartPanel.repaint();
            chartPanel.activeCurrent();

        }, true, chartPanel.getId());


    }

    @SuppressWarnings("unchecked")
    public static void showListPopup(List<PropertyObject> objects, Project project, Consumer<? super Object> selectUserTaskConsumer, boolean showComments,String processId) {

        FlowSearchTable jbTable = new FlowSearchTable(objects,selectUserTaskConsumer,processId);
        PopupChooserBuilder builder = new PopupChooserBuilder(jbTable);
        if (showComments) {
            //注释
            builder.setSouthComponent(new FlowSearchCommentPanel(jbTable));
        }

        Runnable runnable = () -> {
            Object item = jbTable.getSelectedObject();
            if (item != null) {
                selectUserTaskConsumer.consume(item);
            }

        };
        builder.setTitle("UserTaskSearcher")
                .setItemChoosenCallback(runnable)
                .setResizable(true)
                .setDimensionServiceKey("UserTaskSearcher")
                .setMovable(true)
                .setMinSize(new JBDimension(300, 300));

        JBPopup popup = builder.createPopup();
        popup.showCenteredInCurrentWindow(project);

        ActionManager instance = ActionManager.getInstance();

        ActionGroup ag = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_TABLE_SEARCH_ID);

        PopupHandler.installPopupHandler(jbTable, ag, ActionPlaces.UPDATE_POPUP);


        //安装一个右键菜单供使用
    }



}
