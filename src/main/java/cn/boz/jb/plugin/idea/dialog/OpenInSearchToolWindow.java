package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.action.GoToRefFile;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherDetailComment;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherTable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class OpenInSearchToolWindow extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        //拿到要检索的内容，如代码片段，搜索db
        JBTable outside = (JBTable) anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if(outside!=null){
            ListTableModel model = (ListTableModel) outside.getModel();
            List items = model.getItems();
            CallerSearcherTable tabTable = new CallerSearcherTable(new ListTableModel<>(GoToRefFile.CALL_SEARCHER_TABLE_COLUMN_INFO,items,0)){
                @Override
                public boolean isCellEditable(int row, int column) {
                    if(column==1||column==2){
                        return true;
                    }
                    return super.isCellEditable(row, column);

                }
            };

            JBScrollPane tabSp = new JBScrollPane(tabTable);
            JBSplitter jbSplitter = new JBSplitter();
            jbSplitter.setFirstComponent(tabSp);
            CallerSearcherDetailComment callerSearcherCommentPanel = new CallerSearcherDetailComment(tabTable);
            JBScrollPane detailSp = new JBScrollPane(callerSearcherCommentPanel);
            jbSplitter.setSecondComponent(detailSp);


            ToolWindow callSearch = ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow("CallSearch");

            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content title = contentFactory.createContent(jbSplitter, "CallerSearcher", true);
            title.setCloseable(true);
            callSearch.getContentManager().addContent(title);;
            callSearch.getContentManager().requestFocus(title,true);
            if(!callSearch.isActive()){
                callSearch.show();
            }

            ActionManager instance = ActionManager.getInstance();
            ActionGroup ag = (ActionGroup) instance.getAction("spd.gotorefaction.callersearcher.group");
            PopupHandler.installPopupHandler(tabTable, ag, ActionPlaces.UPDATE_POPUP);
            PopupUtil.getPopupContainerFor(outside).dispose();

        }

    }
}
