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
@State(name = "cn.boz.jb.plugin.idea.configurable.SpdEditorState", storages = {@Storage("spdeditor.xml")})
public class SpdEditorState implements PersistentStateComponent<SpdEditorState> {

    public String jdbcUrl="";

    public String jdbcUserName="";

    public String jdbcPassword="";

    public String jdbcDriver="";

    //自动保存，默认为false
    public boolean autoSave=false;

    public int pageNum=0;
    public int pageSize=400;

    public SpdEditorState() {

    }


    @Override
    public @Nullable SpdEditorState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpdEditorState spdState) {
        XmlSerializerUtil.copyBean(spdState, this);

    }

    public static SpdEditorState getInstance() {
        return ApplicationManager.getApplication().getService(SpdEditorState.class);
    }


}
