package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 这个Service是用于关于state的存取容器,其实际内容需要
 */
@State(name = "cn.boz.jb.plugin.idea.configurable.SpdEditorDBListState",
        storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)}
)
public class SpdEditorDBListState implements PersistentStateComponent<SpdEditorDBListState> {

    public String currentActiveId;

    public List<DBInfo> configList;


    public SpdEditorDBListState() {

    }


    @Override
    public @Nullable SpdEditorDBListState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpdEditorDBListState spdState) {
        XmlSerializerUtil.copyBean(spdState, this);

    }


    public static SpdEditorDBListState getInstance(Project project) {
        return ServiceManager.getService(project, SpdEditorDBListState.class);
    }


}
