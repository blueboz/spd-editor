package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ex.Settings;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 配置项编辑器
 */
public class SpdEditorDBSettings implements Configurable {

    private SpdEditorDBSettingsComp settings;

    @Override
    public String getDisplayName() {
        return "SpdEditorDBConfig";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settings = new SpdEditorDBSettingsComp();
        return settings.getPanel();
    }


    @Override
    public boolean isModified() {
        return settings.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        //保存方式
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

