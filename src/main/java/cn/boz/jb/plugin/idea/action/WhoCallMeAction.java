package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;

public class WhoCallMeAction extends AnAction {
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
