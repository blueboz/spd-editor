package cn.boz.jb.plugin.idea.action.fmss;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddPluginAddress extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        List<String> pluginHost = UpdateSettings.getInstance().getStoredPluginHosts();
        pluginHost.removeIf(item->item.contains("http://21.96.35.45:16789/jetbrainsPlugins/updateplugins2.xml"));
        pluginHost.add("http://21.96.35.45:16789/jetbrainsPlugins/updateplugins2.xml");
    }
}
