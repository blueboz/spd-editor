package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

public class WhoCallMeCodeActionGroup {

    public static class WhoCallMeDBAction extends DumbAwareAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            ChartPanel chartPanel = (ChartPanel) anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
            if (chartPanel instanceof ChartPanel) {
                String id = chartPanel.getId();
                if (id == null || "".equals(id.trim())) {
                    NotificationGroup.findRegisteredGroup("Spd Editor")
                            .createNotification("", NotificationType.WARNING)
                            .notify(anActionEvent.getProject());
                    Notification spdEditorNotification = new Notification("Spd Editor", SpdEditorIcons.FLOW_16_ICON, NotificationType.WARNING);
                    spdEditorNotification.setTitle("warn");
                    spdEditorNotification.setContent("Flow id is null");
                    spdEditorNotification.notify(anActionEvent.getProject());
                } else {
                    GoToRefFile.tryToSearchUsageByCodeFragment(anActionEvent, id,"process");
                }
            }
        }
    }

    public static class WhoCallMeCodeAction extends DumbAwareAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            ChartPanel chartPanel = (ChartPanel) anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
            if (chartPanel instanceof ChartPanel) {
                String id = chartPanel.getId();
                if (id == null || "".equals(id.trim())) {
                    NotificationGroup.findRegisteredGroup("Spd Editor")
                            .createNotification("Flow id is null", NotificationType.WARNING)
                            .notify(anActionEvent.getProject());
                } else {
                    FindInProjectManager instance = FindInProjectManager.getInstance(anActionEvent.getProject());
                    FindModel findModel = new FindModel();
                    findModel.setStringToFind(id);
                    instance.findInProject(anActionEvent.getDataContext(), findModel);
                }
            }
        }
    }
}
