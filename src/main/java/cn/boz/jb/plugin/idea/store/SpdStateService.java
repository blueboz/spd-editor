package cn.boz.jb.plugin.idea.store;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 这个Service是用于关于state的存取容器,其实际内容需要
 */
@State(name = "spdeditor.xml")
public class SpdStateService implements PersistentStateComponent<SpdState> {

    public SpdStateService(){
        this.spdState=new SpdState();
    }

    private SpdState spdState;

    @Override
    public @Nullable SpdState getState() {
        return spdState;
    }

    @Override
    public void loadState(@NotNull SpdState spdState) {
        this.spdState = spdState;
    }

    public static SpdStateService getInstance() {
        return ApplicationManager.getApplication().getService(SpdStateService.class);
    }


}
