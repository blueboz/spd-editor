package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 这个Service是用于关于state的存取容器,其实际内容需要
 */
@State(name = "cn.boz.jb.plugin.idea.configurable.SpdEditorNormState", storages = {@Storage("spdeditor.xml")})
public class SpdEditorNormState implements PersistentStateComponent<SpdEditorNormState> {

    public String webroot="";

    public SpdEditorNormState() {

    }


    @Override
    public @Nullable SpdEditorNormState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpdEditorNormState spdState) {
        XmlSerializerUtil.copyBean(spdState, this);

    }

    public static SpdEditorNormState getInstance() {
        return ApplicationManager.getApplication().getService(SpdEditorNormState.class);
    }


}