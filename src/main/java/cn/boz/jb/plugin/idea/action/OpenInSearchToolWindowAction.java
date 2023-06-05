package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.codegen.dlg.EcasMenuIdSelectorDlg;
import cn.boz.jb.plugin.codegen.dlg.EngineActionSelectorDlg;
import cn.boz.jb.plugin.codegen.dlg.XFunCodeDefDlg;
import cn.boz.jb.plugin.idea.action.flowSearch.FlowSearchTable;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherCommentPanel;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherDetailComment;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherTablePanel;
import cn.boz.jb.plugin.idea.dialog.ActionPowerTreeTableDialog;
import cn.boz.jb.plugin.idea.dialog.EcasMenuTreeDialog;
import cn.boz.jb.plugin.idea.dialog.EcasMenuTreeTableDialog;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineRightDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class OpenInSearchToolWindowAction extends AnAction implements DumbAware {

    public <T > T findComponentWithAllPossible(Component component,Class<T> targetComponet){
        if(component==null){
            return null;
        }
        if(component.getClass().isAssignableFrom(targetComponet)){
            return (T) component;
        }
        DialogWrapper inst = DialogWrapper.findInstance(component);
        if(inst !=null&&inst.getClass().isAssignableFrom(targetComponet)){
            return (T)inst;
        }
        if(component instanceof JBScrollPane){
            JBScrollPane jbScrollPane = (JBScrollPane) component;
            JViewport viewport = jbScrollPane.getViewport();
            Component view = viewport.getView();
            if(view.getClass().isAssignableFrom(targetComponet)){
                return (T)component;
            }
        }
        T result = (T) SwingUtilities.getAncestorOfClass(targetComponet, component);
        if(result!=null){
            return result;
        }
        return null;
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //拿到要检索的内容，如代码片段，搜索db

        Component component = e.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (component instanceof CallerSearcherTablePanel) {
            doForCallerSearcherTable((CallerSearcherTablePanel) component, e);
            return;
        }
        if (component instanceof FlowSearchTable) {
            doForFlowSearcherTable((FlowSearchTable) component, e);
            return;
        }
        if (component instanceof EngineRightDialog) {
            doForEngineRights(e, component);
            return;
        }
        if (component instanceof EngineActionDialog) {
            doForEngineAction(e, component);
            return;
        }
        if (component instanceof EcasMenuTreeDialog) {
            doForEcasMenuTree(e, component);
        }
        EcasMenuTreeTableDialog ecasMenuTreeTableDialog = findComponentWithAllPossible(component, EcasMenuTreeTableDialog.class);
        if(ecasMenuTreeTableDialog!=null){
            doForEcasTreeTable(e,ecasMenuTreeTableDialog);
        }
        EcasMenuIdSelectorDlg ecasMenuIdSelectorDlg = findComponentWithAllPossible(component, EcasMenuIdSelectorDlg.class);
        if(ecasMenuIdSelectorDlg!=null){
            doForEcasMenuIdSelectDlg(e,ecasMenuIdSelectorDlg);
        }

        ActionPowerTreeTableDialog actionPowerTreeTableDialog = findComponentWithAllPossible(component, ActionPowerTreeTableDialog.class);
        if(actionPowerTreeTableDialog!=null){
            doForActionpowerTreeTableDlg(e,actionPowerTreeTableDialog);
        }
        XFunCodeDefDlg xFunCodeDefDlg = findComponentWithAllPossible(component, XFunCodeDefDlg.class);
        if(xFunCodeDefDlg!=null){
            doForXfunCodeDefDlg(e,xFunCodeDefDlg);
        }


        DialogWrapper instance = DialogWrapper.findInstance(component);
        if(instance instanceof EngineActionSelectorDlg){
            EngineActionSelectorDlg engineActionSelectorDlg= (EngineActionSelectorDlg) instance;
            doForEngineActionSelectDlg(e,engineActionSelectorDlg);
        }

        if (component instanceof JBScrollPane) {
            JBScrollPane jbScrollPane = (JBScrollPane) component;
            JViewport viewport = jbScrollPane.getViewport();
            Component view = viewport.getView();
            if (view instanceof EngineTaskDialog) {
                doForEngineTask(e, (EngineTaskDialog) view);
                return;
            } else if (view instanceof EngineRightDialog) {
                doForEngineRights(e, view);
            } else if (view instanceof EcasMenuTreeDialog) {
                doForEcasMenuTree(e, view);
            } else if (view instanceof FlowSearchTable) {
                doForFlowSearcherTable((FlowSearchTable) view, e);
                return;
            }
        }
        CallerSearcherTablePanel callerSearcherTablePanel = (CallerSearcherTablePanel) SwingUtilities.getAncestorOfClass(CallerSearcherTablePanel.class, component);
        FlowSearchTable flowSearchTable = (FlowSearchTable) SwingUtilities.getAncestorOfClass(FlowSearchTable.class, component);

        EngineRightDialog engineRightDialog = (EngineRightDialog) SwingUtilities.getAncestorOfClass(EngineRightDialog.class, component);
        EngineActionDialog engineActionDialog = (EngineActionDialog) SwingUtilities.getAncestorOfClass(EngineActionDialog.class, component);
        EngineTaskDialog engineTaskDialog = (EngineTaskDialog) SwingUtilities.getAncestorOfClass(EngineTaskDialog.class, component);
        EcasMenuTreeDialog ecasMenuTreeDialog = (EcasMenuTreeDialog) SwingUtilities.getAncestorOfClass(EcasMenuTreeDialog.class, component);

        if (callerSearcherTablePanel instanceof CallerSearcherTablePanel) {
            doForCallerSearcherTable(callerSearcherTablePanel, e);
        }
        if (flowSearchTable instanceof FlowSearchTable) {
            doForFlowSearcherTable(flowSearchTable, e);
        }
        if (engineRightDialog instanceof EngineRightDialog) {
            doForEngineRights(e, engineRightDialog);
        }
        if (engineActionDialog instanceof EngineActionDialog) {
            doForEngineAction(e, engineActionDialog);
        }
        if (engineTaskDialog instanceof EngineTaskDialog) {
            doForEngineTask(e, engineTaskDialog);
        }
        CallerSearcherCommentPanel callerSearcherCommentPanel = (CallerSearcherCommentPanel) SwingUtilities.getAncestorOfClass(CallerSearcherCommentPanel.class, component);
        if (callerSearcherCommentPanel instanceof CallerSearcherCommentPanel) {
            CallerSearcherTablePanel table = callerSearcherCommentPanel.getTable();
            doForCallerSearcherTable(table, e);
        }
        if (ecasMenuTreeDialog instanceof EcasMenuTreeDialog) {
            doForEcasMenuTree(e, ecasMenuTreeDialog);
        }


    }

    private void doForXfunCodeDefDlg(AnActionEvent e, XFunCodeDefDlg xFunCodeDefDlg) {
        JBScrollPane derive = xFunCodeDefDlg.derive();
        addToToolWindow(e,derive,"codeDef ");
        xFunCodeDefDlg.disposeIfNeeded();
    }

    private void doForActionpowerTreeTableDlg(AnActionEvent e, ActionPowerTreeTableDialog actionPowerTreeTableDialog) {
        JBScrollPane derive = actionPowerTreeTableDialog.derive();
        addToToolWindow(e,derive,"actionPowerTreeTable ");
        actionPowerTreeTableDialog.disposeIfNeeded();
    }

    private void doForEcasMenuIdSelectDlg(AnActionEvent e, EcasMenuIdSelectorDlg ecasMenuIdSelectorDlg) {
        JBScrollPane derive = ecasMenuIdSelectorDlg.derive();
       addToToolWindow(e,derive,"EcasMenuIdSelector");
        ecasMenuIdSelectorDlg.disposeIfNeeded();
    }

    private void doForEcasTreeTable(AnActionEvent e, EcasMenuTreeTableDialog ecasMenuTreeTableDialog) {
        JBScrollPane derive = ecasMenuTreeTableDialog.derive();
        addToToolWindow(e,derive,"Ecas 菜单树表");
        ecasMenuTreeTableDialog.disposeIfNeeded();
    }

    public void addToToolWindow(AnActionEvent e,JComponent component,String titleStr){
        ToolWindow callSearch = ToolWindowManager.getInstance(e.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content title = contentFactory.createContent(component, titleStr, true);
        title.setCloseable(true);
        callSearch.getContentManager().addContent(title);
        callSearch.getContentManager().requestFocus(title, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(title);
    }



    private void doForEngineActionSelectDlg(AnActionEvent e, EngineActionSelectorDlg engineActionSelectorDlg) {
        JBScrollPane derive = engineActionSelectorDlg.derive();
        ToolWindow callSearch = ToolWindowManager.getInstance(e.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content title = contentFactory.createContent(derive, "ActionPower", true);
        title.setCloseable(true);
        callSearch.getContentManager().addContent(title);
        callSearch.getContentManager().requestFocus(title, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(title);
        engineActionSelectorDlg.disposeIfNeeded();
    }

    private void doForEcasMenuTree(AnActionEvent anActionEvent, Component view) {
        EcasMenuTreeDialog ecasMenuTreeDialog = (EcasMenuTreeDialog) view;
        JBScrollPane derive = ecasMenuTreeDialog.derive();
        ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content title = contentFactory.createContent(derive, ecasMenuTreeDialog.getFileName(), true);
        title.setCloseable(true);
        callSearch.getContentManager().addContent(title);
        callSearch.getContentManager().requestFocus(title, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(title);

        PopupUtil.getPopupContainerFor(view).dispose();

    }


    private void doForEngineTask(AnActionEvent anActionEvent, EngineTaskDialog engineTaskDialog) {
        JComponent derive = engineTaskDialog.derive();

        ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JBScrollPane jbScrollPane = new JBScrollPane(derive);
        Content engineActionContent = contentFactory.createContent(jbScrollPane, engineTaskDialog.getEngineTask().getId(), true);
        engineActionContent.setCloseable(true);
        callSearch.getContentManager().addContent(engineActionContent);
        callSearch.getContentManager().requestFocus(engineActionContent, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(engineActionContent);
        PopupUtil.getPopupContainerFor(engineTaskDialog).dispose();
    }

    private void doForEngineAction(AnActionEvent anActionEvent, Component component) {
        EngineActionDialog engineActionDialog = (EngineActionDialog) component;
        JComponent derive = engineActionDialog.derive();

        ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JBScrollPane jbScrollPane = new JBScrollPane(derive);
        Content engineActionContent = contentFactory.createContent(jbScrollPane, engineActionDialog.getId(), true);
        engineActionContent.setCloseable(true);
        callSearch.getContentManager().addContent(engineActionContent);
        callSearch.getContentManager().requestFocus(engineActionContent, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(engineActionContent);
        PopupUtil.getPopupContainerFor(component).dispose();
    }

    private void doForEngineRights(@NotNull AnActionEvent anActionEvent, Component component) {
        EngineRightDialog engineRightDialog = (EngineRightDialog) component;
        JComponent derive = engineRightDialog.derive();
        ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JBScrollPane jbScrollPane = new JBScrollPane(derive);

        Content title = contentFactory.createContent(jbScrollPane, engineRightDialog.getUserTask().getRights(), true);
        title.setCloseable(true);
        callSearch.getContentManager().addContent(title);
        callSearch.getContentManager().requestFocus(title, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(title);

        PopupUtil.getPopupContainerFor(component).dispose();
    }

    private void doForFlowSearcherTable(FlowSearchTable outside, AnActionEvent anActionEvent) {
        if (outside == null) {
            return;
        }
        JBPopup popupContainerFor = PopupUtil.getPopupContainerFor(outside);

//        outside.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ListSelectionModel selectionModel = outside.getSelectionModel();
//        selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        outside.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){
                    ListTableModel model = (ListTableModel) outside.getModel();

                    Point point = e.getPoint();
                    int selectedRow = outside.rowAtPoint(point);
                    int col = outside.columnAtPoint(point);
                    //第0行不响应，仅仅作为焦点返回
                    if(col==0){
                        return ;
                    }
                    int i = outside.convertRowIndexToModel(selectedRow);
                    Object item = model.getItem(i);
                    outside.getSelectUserTaskConsumer().consume(item);
                }
            }
        });



        ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        JBScrollPane jbScrollPane = new JBScrollPane(outside);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(jbScrollPane,BorderLayout.CENTER);
        Content title = contentFactory.createContent(jPanel,"process:"+outside.getProcessId(), true);
        title.setCloseable(true);
        callSearch.getContentManager().addContent(title);
        callSearch.getContentManager().requestFocus(title, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(title);
        popupContainerFor.dispose();

    }


    private void doForCallerSearcherTable(CallerSearcherTablePanel outside, AnActionEvent anActionEvent) {
        if (outside != null) {
            ListTableModel model = (ListTableModel) outside.getModel();
            List items = model.getItems();
            CallerSearcherTablePanel tabTable = new CallerSearcherTablePanel(new ListTableModel<>(GotoRefFileAction.CALL_SEARCHER_TABLE_COLUMN_INFO, items, 0)) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return super.isCellEditable(row, column);
                }
            };

            JBScrollPane tabSp = new JBScrollPane(tabTable);
            JBSplitter jbSplitter = new JBSplitter();
            jbSplitter.setFirstComponent(tabSp);
            CallerSearcherDetailComment callerSearcherCommentPanel = new CallerSearcherDetailComment(tabTable, outside.getQueryName());
            JBScrollPane detailSp = new JBScrollPane(callerSearcherCommentPanel);
            jbSplitter.setSecondComponent(detailSp);


            ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);

            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            String queryName = outside.getQueryName();

            String qualifierName = outside.getQualifierName();
            if (qualifierName.lastIndexOf(".") != -1) {
                qualifierName = qualifierName.substring(qualifierName.lastIndexOf(".") + 1);
            }
            Content title = contentFactory.createContent(jbSplitter, String.format("%s.%s", qualifierName, queryName), true);
            title.setCloseable(true);
            callSearch.getContentManager().addContent(title);
            callSearch.getContentManager().requestFocus(title, true);
            if (!callSearch.isActive()) {
                callSearch.show();
            }
            callSearch.getContentManager().setSelectedContent(title);

            ActionManager instance = ActionManager.getInstance();
            ActionGroup ag = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_TABLE_SEARCH_ID);
            PopupHandler.installPopupHandler(tabTable, ag, ActionPlaces.UPDATE_POPUP);
            PopupUtil.getPopupContainerFor(outside).dispose();

        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

    }
}