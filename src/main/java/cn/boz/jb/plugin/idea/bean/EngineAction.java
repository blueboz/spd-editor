package cn.boz.jb.plugin.idea.bean;


import java.util.List;

public class EngineAction {

    private String id;
    private String namespace;
    private String url;
    private String windowparam;
    private String actionscript;
    private String actionintercept;


    private List<EngineActionInput> inputs;
    private List<EngineActionOutput> outputs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getWindowparam() {
        return windowparam;
    }

    public void setWindowparam(String windowparam) {
        this.windowparam = windowparam;
    }


    public String getActionscript() {
        return actionscript;
    }

    public void setActionscript(String actionscript) {
        this.actionscript = actionscript;
    }


    public String getActionintercept() {
        return actionintercept;
    }

    public void setActionintercept(String actionintercept) {
        this.actionintercept = actionintercept;
    }


    public List<EngineActionInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<EngineActionInput> inputs) {
        this.inputs = inputs;
    }

    public List<EngineActionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<EngineActionOutput> outputs) {
        this.outputs = outputs;
    }
}
