package cn.boz.jb.plugin.idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;

public class MinimizeUtils {

    public static void minimize(JBPopup popup, JComponent component, Project project, String title,boolean wrapScroll) {
        JBPopup popupContainerFor;
        if (popup == null) {
            popupContainerFor = PopupUtil.getPopupContainerFor(component);
        } else {
            popupContainerFor = popup;
        }



        ToolWindow callSearch = ToolWindowManager.getInstance(project).getToolWindow(Constants.TOOL_WINDOW_CALLSEARCH);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content titleContent = contentFactory.createContent(wrapScroll?new JBScrollPane(component):component, title, true);
        titleContent.setCloseable(true);
        callSearch.getContentManager().addContent(titleContent);
        callSearch.getContentManager().requestFocus(titleContent, true);
        if (!callSearch.isActive()) {
            callSearch.show();
        }
        callSearch.getContentManager().setSelectedContent(titleContent);

        if (popupContainerFor != null && !popupContainerFor.isDisposed()) {

            popupContainerFor.dispose();
        }

    }
}
