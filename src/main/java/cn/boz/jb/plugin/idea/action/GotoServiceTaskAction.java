package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;


/**
 * 跳转到ServiceTask的Action
 */
public class GotoServiceTaskAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }
        ChartPanel cp = (ChartPanel) requiredData;
        if (cp.getSelectedObject() instanceof ServiceTask) {
            anActionEvent.getPresentation().setEnabledAndVisible(true);
            return;
        }
        anActionEvent.getPresentation().setEnabledAndVisible(false);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            return;
        }
        ChartPanel cp = (ChartPanel) requiredData;
        PropertyObject selectedObject = cp.getSelectedObject();
        if (selectedObject instanceof ServiceTask) {
            processServiceTask(anActionEvent, cp);
        }
    }

    private void processServiceTask(AnActionEvent anActionEvent, ChartPanel cp) {
        ServiceTask serviceTask = (ServiceTask) cp.getSelectedObject();
        String expression = serviceTask.getExpression();
        String[] scriptRows = expression.split("\n");
        ListPopup listPopup = JBPopupFactory.getInstance()
                .createListPopup(new BaseListPopupStep<String>("Select script to go",
                        scriptRows) {
                    @Override
                    public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                        if (finalChoice) {
                            return doFinalStep(() -> doRun(selectedValue));
                        }
                        return PopupStep.FINAL_CHOICE;
                    }

                    private void doRun(String selectedValue) {
                        SearchEverywhereManager instance = SearchEverywhereManager.getInstance(anActionEvent.getProject());
                        String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
                        instance.show(allContributorsGroupId, selectedValue, anActionEvent);
                    }
                });
        InputEvent inputEvent = anActionEvent.getInputEvent();
        if (inputEvent instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) inputEvent;
            listPopup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));
        } else {
            listPopup.showInFocusCenter();
        }
    }
}
