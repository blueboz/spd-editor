package cn.boz.jb.plugin.floweditor.gui.hist;

/**
 * 最基础的状态对象，只有记录状态的对象主体
 */
public class BaseState {
    //被操作的对象
    public Restorable operated;

    public BaseState() {

    }

    public BaseState(Restorable operated) {
        this.operated = operated;
    }
}
