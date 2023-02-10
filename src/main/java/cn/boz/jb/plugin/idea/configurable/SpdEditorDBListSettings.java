package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.extensions.BaseExtensionPointName;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

/**
 * 配置项编辑器
 */
public class SpdEditorDBListSettings implements Configurable,Configurable.WithEpDependencies {

    private Project project;

    private SpdEditorDBListSettingsComp settings;

    public SpdEditorDBListSettings(Project project) {
        this.project = project;
    }


    @Override
    public String getDisplayName() {
        return "SpdEditorDBConfig";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settings = new SpdEditorDBListSettingsComp(project);

        return settings.getPanel();
    }


    @Override
    public boolean isModified() {
        return settings.isModified(project);
    }

    @Override
    public void apply() throws ConfigurationException {
        //保存方式
        settings.apply(project);
    }

    @Override
    public void reset() {
        settings.reset(project);
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return settings.getPreferredFocusedComponent();
    }

    @Override
    public @NotNull Collection<BaseExtensionPointName<?>> getDependencies() {
        return Collections.emptyList();
    }
}

