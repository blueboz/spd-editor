package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.property.InputLongTextDialog;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherCommentPanel;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherDetailComment;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.dialog.min.EngineActionDerivePanel;
import cn.boz.jb.plugin.idea.dialog.min.EngineTaskDerivePanel;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GotoScriptAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        EngineActionDialog engineActionDialog = (EngineActionDialog) SwingUtilities.getAncestorOfClass(EngineActionDialog.class, anActionEvent.getInputEvent().getComponent());
        if (engineActionDialog instanceof EngineActionDialog) {
            EngineAction engineAction = engineActionDialog.getEngineAction();
            String actionscript = (String) engineAction.getActionscript();
            processScriptContent(actionscript, anActionEvent, engineActionDialog, () -> {
            });
            return;
        }
        EngineTaskDialog engineTaskDialog = (EngineTaskDialog) SwingUtilities.getAncestorOfClass(EngineTaskDialog.class, anActionEvent.getInputEvent().getComponent());
        if (engineTaskDialog instanceof EngineTaskDialog) {
            EngineTask engineTask = engineTaskDialog.getEngineTask();
            String expression = engineTask.getExpression();
            processScriptContent(expression, anActionEvent, engineTaskDialog, () -> {
            });
            return;
        }
        CallerSearcherCommentPanel caller = (CallerSearcherCommentPanel) SwingUtilities.getAncestorOfClass(CallerSearcherCommentPanel.class, anActionEvent.getInputEvent().getComponent());
        if (caller instanceof CallerSearcherCommentPanel) {
            String script = caller.getScript();
            processScriptContent(script, anActionEvent, caller, () -> {

            });
            return;
        }
        CallerSearcherDetailComment detailComment = (CallerSearcherDetailComment) SwingUtilities.getAncestorOfClass(CallerSearcherDetailComment.class, anActionEvent.getInputEvent().getComponent());
        if (detailComment instanceof CallerSearcherDetailComment) {
            String script = detailComment.getScript();
            processScriptContent(script, anActionEvent, detailComment, () -> {

            });
            return;
        }

        EngineTaskDerivePanel engineTaskDerivePanel = (EngineTaskDerivePanel) SwingUtilities.getAncestorOfClass(EngineTaskDerivePanel.class, anActionEvent.getInputEvent().getComponent());
        if (engineTaskDerivePanel instanceof EngineTaskDerivePanel) {
            String script = engineTaskDerivePanel.getEngineTask().getExpression();
            processScriptContent(script, anActionEvent, engineTaskDerivePanel, () -> {

            });
            return;
        }

        EngineActionDerivePanel engineActionDerivePanel = (EngineActionDerivePanel) SwingUtilities.getAncestorOfClass(EngineActionDerivePanel.class, anActionEvent.getInputEvent().getComponent());
        if (engineActionDerivePanel instanceof EngineActionDerivePanel) {
            String script = engineActionDerivePanel.getActionScript();
            processScriptContent(script, anActionEvent, engineActionDerivePanel, () -> {

            });
            return;
        }



        Component component = anActionEvent.getInputEvent().getComponent();
        InputLongTextDialog dialog = (InputLongTextDialog) InputLongTextDialog.findInstance(component);
        if(dialog!=null){
            String inputText = dialog.getInputText();

            processScriptContent(inputText, anActionEvent, dialog.getRootPane(), () -> {
                dialog.close(0);
            });
        }

        Component contextComponent = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if(contextComponent instanceof CallerSearcherCommentPanel){
            CallerSearcherCommentPanel cscp= (CallerSearcherCommentPanel) contextComponent;
            String script = cscp.getScript();
            processScriptContent(script, anActionEvent, cscp.getRootPane(), () -> {
            });
        }
    }

    public void processScriptContent(String actionscript, AnActionEvent anActionEvent, JComponent centerOf, Runnable callback) {

        String[] split = actionscript.split("[\n;]");
        List<String> collect = Arrays.asList(split).stream().filter(it -> it != null && !it.trim().equals("")).collect(Collectors.toList());
        ListPopup search = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<String>("please select", collect) {

            @Override
            public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                if (finalChoice) {
                    return doFinalStep(() -> doRun(selectedValue));
                }
                return PopupStep.FINAL_CHOICE;
            }

            private void doRun(String selectedValue) {
                //选择的值可以进行跳转
                EventQueue.invokeLater(() -> {
                    callback.run();
                    gotoSelectedValue(selectedValue, anActionEvent);
                });

            }


        });
        search.showInCenterOf(centerOf);
    }

    public static void gotoSelectedValue(String selectedValue, AnActionEvent anActionEvent) {
        if (selectedValue.contains("processEngine.start")) {
            Pattern pattern = Pattern.compile("processEngine\\.start\\(\"(\\w+)\",CONTEXT\\)");
            Matcher matcher = pattern.matcher(selectedValue);
            if (matcher.find()) {
                String group = matcher.group(1);
                Project defaultProject = ProjectManager.getInstance().getDefaultProject();
                SearchEverywhereManager instance = SearchEverywhereManager.getInstance(defaultProject);
                String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
                instance.show(allContributorsGroupId, group, anActionEvent);
            }
            //支持另外的一种形式
        } else {
            if (selectedValue.contains("=")) {
                selectedValue = selectedValue.split("=")[1];
            }
            if (selectedValue.contains("(")) {
                selectedValue = selectedValue.split("\\(")[0];
            }


            SearchEverywhereManager instance = SearchEverywhereManager.getInstance(anActionEvent.getProject());
            String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
            instance.show(allContributorsGroupId, selectedValue, anActionEvent);

        }
    }

}
