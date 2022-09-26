package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.extensions.BaseExtensionPointName;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

/**
 * 配置项编辑器
 */
public class SpdEditorNormSettings implements Configurable,Configurable.WithEpDependencies {

    private SpdEditorNormSettingsComp settings;

    @Override
    public String getDisplayName() {
        return "SpdEditorBasicConfig";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settings = new SpdEditorNormSettingsComp();
        return settings.getPanel();
    }


    @Override
    public boolean isModified() {
        return settings.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settings.apply();
    }

    @Override
    public void reset() {
        settings.reset();
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

