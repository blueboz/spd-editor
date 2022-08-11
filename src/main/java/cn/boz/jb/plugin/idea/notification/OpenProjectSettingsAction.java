package cn.boz.jb.plugin.idea.notification;

import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;

class OpenProjectSettingsAction extends OpenConfigurableAction {
  OpenProjectSettingsAction(Project project) {
    super(project, "Open Spd Db Configuration",  SpdEditorDBSettings.class);
  }
}
