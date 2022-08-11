package cn.boz.jb.plugin.idea.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

class OpenConfigurableAction extends NotificationAction {
    private final Project project;

    private final Class<? extends Configurable> configurable;

    OpenConfigurableAction(Project project, String text, Class<? extends Configurable> configurable) {
        super(text);
        this.project = project;
        this.configurable = configurable;
    }

    @Override
    public final void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
        if (!this.project.isDisposed()) {
            try {
                ShowSettingsUtil.getInstance().editConfigurable(this.project, configurable.newInstance());
            } catch (InstantiationException instantiationException) {
                instantiationException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        } else {
            notification.expire();
        }
    }
}
