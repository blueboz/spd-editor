package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.find.FindInProjectSettings;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;

import java.awt.Component;

/**
 * 转到流程的Action
 * 两种防范可以转到流程
 * 1.Ctrl+Shift+F
 * 2.Shift*2
 */
public class GotoProcessFindAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            return;
        }
        ChartPanel cp = (ChartPanel) requiredData;
        PropertyObject selectedObject = cp.getSelectedObject();
        if (selectedObject instanceof CallActivity) {
            processCallActivity(anActionEvent, cp);
            return;
        }

    }

    private void processCallActivity(@NotNull AnActionEvent anActionEvent, ChartPanel cp) {
        //目标调用元素
        CallActivity callActivity = (CallActivity) cp.getSelectedObject();
        final String calledElement = callActivity.getCalledElement();
        SearchEverywhereManager.getInstance(anActionEvent.getProject());
        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
        FindModel findModel = new FindModel();
        findModel.setStringToFind("id=\"" + calledElement + "\"");
        findModel.setFileFilter("*.spd");
        FindInProjectSettings settings = FindInProjectSettings.getInstance(anActionEvent.getProject());
        findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
    }
}
