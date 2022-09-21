package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;

import java.util.List;
import java.util.Map;

public class EngineActionDataContainer {

    private EngineAction engineAction;
;

    public EngineAction getEngineAction() {
        return engineAction;
    }

    public void setEngineAction(EngineAction engineAction) {
        this.engineAction = engineAction;
    }

    public EngineActionDataContainer(EngineAction engineAction) {
        this.engineAction = engineAction;
    }
}
