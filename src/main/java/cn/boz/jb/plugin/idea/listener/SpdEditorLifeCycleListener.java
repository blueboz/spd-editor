package cn.boz.jb.plugin.idea.listener;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpdEditorLifeCycleListener implements AppLifecycleListener {


    @Override
    public void appStarting(@Nullable Project projectFromCommandLine) {
        ProjectManager instance = ProjectManager.getInstance();
        Project defaultProject = instance.getDefaultProject();
        @NotNull Project[] openProjects = instance.getOpenProjects();
        List<String> pluginHost = UpdateSettings.getInstance().getStoredPluginHosts();
        pluginHost.removeIf(item->item.contains("http://21.96.35.45:16789/jetbrainsPlugins/updateplugins2.xml"));
        pluginHost.add("http://21.96.35.45:16789/jetbrainsPlugins/updateplugins2.xml");

    }
}
