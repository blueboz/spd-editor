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

/**
 * 这个Service是用于关于state的存取容器,其实际内容需要
 */
@State(name = "cn.boz.jb.plugin.idea.configurable.CodeGenState",
        storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class CodeGenState implements PersistentStateComponent<CodeGenState> {

    //输出路径
    public String outputDest = "";

    //模板路径
    public String templatePath="/home/@chenweidian-yfzx/Code/spd-editor/src/main/resources/templates";

    public CodeGenState() {
    }

    @Override
    public @Nullable CodeGenState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CodeGenState codeGenState) {
        XmlSerializerUtil.copyBean(codeGenState, this);
    }

    public static CodeGenState getInstance(Project project) {
        return ServiceManager.getService(project, CodeGenState.class);
    }

}
