package cn.boz.jb.plugin.floweditor.gui.hist;

public class StateChange {
    public BaseState before;
    public BaseState after;
    public StateChange(BaseState before ,BaseState after){
        this.before=before;
        this.after=after;
    }
}
