package cn.boz.jb.plugin.idea.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import icons.SpdEditorIcons;

public class NotificationUtils {

    public static void warnning(String title,String message, Project project){
        Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.WARNING);
        spdEditorNotification.setTitle(title);
        spdEditorNotification.setContent(message);
        spdEditorNotification.notify(project);
    }
}
