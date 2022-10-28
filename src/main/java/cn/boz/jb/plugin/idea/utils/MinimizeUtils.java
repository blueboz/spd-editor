package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.idea.dialog.EcasMenuTreeDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

public class MinimizeUtils {

    public static void minimize(JComponent component, Project project,String title){
        JBPopup popupContainerFor = PopupUtil.getPopupContainerFor(component);

        ToolWindow callSearch = ToolWindowManager.getInstance(project).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content titleContent = contentFactory.createContent(component,title, true);
        titleContent.setCloseable(true);
        callSearch.getContentManager().addContent(titleContent);
        callSearch.getContentManager().requestFocus(titleContent, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(titleContent);

        if(popupContainerFor!=null&&!popupContainerFor.isDisposed()){

            popupContainerFor.dispose();
        }

    }
}
