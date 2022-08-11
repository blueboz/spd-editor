package cn.boz.jb.plugin.idea.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class SpdEditorNotification {
  private static final NotificationGroup DB_CONN_ERR_GROUP = NotificationGroup.balloonGroup("SpdEditor:DB Connection Failure");
  
  private static final NotificationGroup UPDATE_GROUP = NotificationGroup.balloonGroup("SonarLint: Configuration update");
  
  public static final NotificationGroup SERVER_NOTIFICATIONS_GROUP = new NotificationGroup("SonarLint: Server Notifications", NotificationDisplayType.STICKY_BALLOON, true, "SonarLint");
  
  private static final String UPDATE_SERVER_MSG = "\n<br>Please update the binding in the SonarLint Settings";
  
  private static final String UPDATE_BINDING_MSG = "\n<br>Please check the SonarLint project configuration";
  
  private static final String TITLE_SONARLINT_INVALID_BINDING = "<b>SonarLint - Invalid binding</b>";
  
  private volatile boolean shown = false;
  
  private final Project myProject;
  
  protected SpdEditorNotification(Project project) {
    this.myProject = project;
  }
  
  public static SpdEditorNotification get(Project project) {
    return (SpdEditorNotification) ServiceManager.getService(project, SpdEditorNotification.class);
  }
  
  public void reset() {
    this.shown = false;
  }
  
  public void notifyDbInvalid() {

    Notification notification = DB_CONN_ERR_GROUP.createNotification("<b>SpdEditor - DB Connection Failure </b>", "Database connection information maybe incorrect.", NotificationType.ERROR, null);
    notification.addAction((AnAction)new OpenProjectSettingsAction(this.myProject));
    notification.setImportant(true);
    notification.notify(this.myProject);
    this.shown = true;
  }
  
  public void notifyProjectStorageInvalid() {
    if (this.shown) {
      return;
    }
    Notification notification = DB_CONN_ERR_GROUP.createNotification("<b>SonarLint - Invalid binding</b>", "Project bound to an invalid remote project\n<br>Please check the SonarLint project configuration", NotificationType.WARNING, null);
//    notification.addAction((AnAction)new OpenProjectSettingsAction(this.myProject));
    notification.setImportant(true);
    notification.notify(this.myProject);
    this.shown = true;
  }
  
  public void notifyProjectStorageStale() {
    if (this.shown) {
      return;
    }
    Notification notification = DB_CONN_ERR_GROUP.createNotification("<b>SonarLint - Invalid binding</b>", "Local storage is outdated\n<br>Please check the SonarLint project configuration", NotificationType.WARNING, null);
//    notification.addAction((AnAction)new OpenProjectSettingsAction(this.myProject));
    notification.setImportant(true);
    notification.notify(this.myProject);
    this.shown = true;
  }
  
  public void notifyServerNeverUpdated(String serverId) {
    if (this.shown) {
      return;
    }
    Notification notification = DB_CONN_ERR_GROUP.createNotification("<b>SonarLint - Invalid binding</b>", "Missing local storage for connection '" + serverId + "'\n<br>Please update the binding in the SonarLint Settings", NotificationType.WARNING, null);
//    notification.addAction((AnAction)new OpenGlobalSettingsAction(this.myProject));
    notification.setImportant(true);
    notification.notify(this.myProject);
    this.shown = true;
  }
  
  public void notifyServerStorageNeedsUpdate(String serverId) {
    if (this.shown) {
      return;
    }
    Notification notification = DB_CONN_ERR_GROUP.createNotification("<b>SonarLint - Invalid binding</b>", "Local storage for connection '" + serverId + "' must be updated\n<br>Please update the binding in the SonarLint Settings", NotificationType.WARNING, null);
//    notification.addAction((AnAction)new OpenGlobalSettingsAction(this.myProject));
    notification.setImportant(true);
    notification.notify(this.myProject);
    this.shown = true;
  }
}
