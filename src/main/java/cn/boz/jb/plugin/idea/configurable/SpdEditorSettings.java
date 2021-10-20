package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 配置项编辑器
 */
public class SpdEditorSettings implements Configurable {

    private SpdEditorSettingsComp settings;

    @Override
    public String getDisplayName() {
        return "SpdEditor";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settings = new SpdEditorSettingsComp();
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
}

