package cn.boz.jb.plugin.idea.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class NotificationUtils {

    public static void warnning(String title,String message, Project project){
        Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.WARNING);
        spdEditorNotification.setTitle(title);
        spdEditorNotification.setContent(message);
        spdEditorNotification.notify(project);
    }

    public static void info(String title,String message, Project project){
        Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.INFORMATION);
        spdEditorNotification.setTitle(title);
        spdEditorNotification.setContent(message);
        spdEditorNotification.notify(project);
    }

    public static void notifyWithLink(File file,String hint,Project project){
        Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.INFORMATION);
        spdEditorNotification.setTitle("exported");
        spdEditorNotification.addAction(new DumbAwareAction() {
            {
                Presentation presentation = this.getTemplatePresentation();
                presentation.setText("Open");
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }


            }
        });
        spdEditorNotification.setContent(hint);
        spdEditorNotification.notify(project);
    }

}
