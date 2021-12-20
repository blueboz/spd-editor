package cn.boz.jb.plugin.idea.dialog;

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

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GotoScriptAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component data = anActionEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (data instanceof EngineActionDialog) {
            EngineActionDialog engineActionDialog = (EngineActionDialog) data;
            Map<String, Object> engineAction = engineActionDialog.getEngineAction();
            String actionscript = (String) engineAction.get("ACTIONSCRIPT_");
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

                    }else{
                        if (selectedValue.contains("=")) {
                            selectedValue = selectedValue.split("=")[1];
                        }
                        if(selectedValue.contains("(")){
                            selectedValue = selectedValue.split("\\(")[0];
                        }
                        Project defaultProject = ProjectManager.getInstance().getDefaultProject();

                        SearchEverywhereManager instance = SearchEverywhereManager.getInstance(defaultProject);
                        String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
                        instance.show(allContributorsGroupId, selectedValue, anActionEvent);

                    }
                }
            });
            search.showInCenterOf(engineActionDialog);
        }
    }
}
