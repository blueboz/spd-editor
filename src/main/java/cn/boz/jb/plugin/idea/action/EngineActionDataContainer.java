package cn.boz.jb.plugin.idea.action;

import java.util.List;
import java.util.Map;

public class EngineActionDataContainer {

    private Map<String,Object> engineAction;
    private List<Map<String,Object>> engineActionInput;
    private List<Map<String,Object>> engineActionOutput;

    public Map<String, Object> getEngineAction() {
        return engineAction;
    }

    public void setEngineAction(Map<String, Object> engineAction) {
        this.engineAction = engineAction;
    }

    public List<Map<String, Object>> getEngineActionInput() {
        return engineActionInput;
    }

    public void setEngineActionInput(List<Map<String, Object>> engineActionInput) {
        this.engineActionInput = engineActionInput;
    }

    public List<Map<String, Object>> getEngineActionOutput() {
        return engineActionOutput;
    }

    public void setEngineActionOutput(List<Map<String, Object>> engineActionOutput) {
        this.engineActionOutput = engineActionOutput;
    }

    public EngineActionDataContainer(Map<String, Object> engineAction, List<Map<String, Object>> engineActionInput, List<Map<String, Object>> engineActionOutput) {
        this.engineAction = engineAction;
        this.engineActionInput = engineActionInput;
        this.engineActionOutput = engineActionOutput;
    }
}
