package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 这个Service是用于关于state的存取容器,其实际内容需要
 */
@State(name = "cn.boz.jb.plugin.idea.configurable.SpdEditorState",
        storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)}

)
public class SpdEditorDBState implements PersistentStateComponent<SpdEditorDBState> {

    public String jdbcUrl = "";

    public String jdbcUserName = "";

    public String jdbcPassword = "";

    public String jdbcDriver = "";

    //自动保存，默认为false
    public boolean autoSave = false;

    public int pageNum = 0;
    public int pageSize = 400;
    public int actionPowerStart=0;
    public int actionPowerPageSize=40;

    public SpdEditorDBState() {

    }


    @Override
    public @Nullable SpdEditorDBState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpdEditorDBState spdState) {
        XmlSerializerUtil.copyBean(spdState, this);

    }


    public static SpdEditorDBState getInstance(Project project) {
        return ServiceManager.getService(project, SpdEditorDBState.class);
    }


}
