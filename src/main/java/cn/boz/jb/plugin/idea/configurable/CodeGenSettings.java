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
public class CodeGenSettings implements Configurable,Configurable.WithEpDependencies {

    private CodeGenComponent settings;

    private Project project;

    public CodeGenSettings(Project project){
        this.project=project;
    }
    @Override
    public String getDisplayName() {
        return "CodeGenSettings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settings = new CodeGenComponent();
        return settings.getPanel();
    }


    @Override
    public boolean isModified() {
        return settings.isModified(project);
    }

    @Override
    public void apply() throws ConfigurationException {
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

