package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.find.FindInProjectSettings;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.usages.impl.AllSearchOptions;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.util.List;
import java.util.Map;

/**
 * 转到流程的Action
 * 两种防范可以转到流程
 * 1.Ctrl+Shift+F
 * 2.Shift*2
 */
public class GotoProcessAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }
        ChartPanel cp = (ChartPanel) requiredData;
        if (!(cp.getSelectedObject() instanceof CallActivity)) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }
        anActionEvent.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            return;
        }
        ChartPanel cp = (ChartPanel) requiredData;
        if (!(cp.getSelectedObject() instanceof CallActivity)) {
            return;
        }
        CallActivity callActivity = (CallActivity) cp.getSelectedObject();
        //目标调用元素
        final String calledElement = callActivity.getCalledElement();


        ListPopup listPopup = JBPopupFactory.getInstance()
                .createListPopup(new BaseListPopupStep<String>("Find in project or Search Everything",
                        "Search EveryThing", "Find In Project") {

                    @Override
                    public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                        if (finalChoice) {
                            return doFinalStep(() -> doRun(selectedValue));
                        }
                        return PopupStep.FINAL_CHOICE;
                    }

                    private void doRun(String selectedValue) {
                        if ("Find In Project".equals(selectedValue)) {
                            SearchEverywhereManager.getInstance(anActionEvent.getProject());
                            FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                            FindModel findModel = new FindModel();
                            findModel.setStringToFind("id=\"" + calledElement + "\"");
                            findModel.setFileFilter("*.spd");
                            FindInProjectSettings settings = FindInProjectSettings.getInstance(anActionEvent.getProject());

                            findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
                        } else {
                            SearchEverywhereManager instance = SearchEverywhereManager.getInstance(anActionEvent.getProject());
                            String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
                            instance.show(allContributorsGroupId, calledElement, anActionEvent);
                        }
                    }
                });
        listPopup.showInFocusCenter();


    }
}
