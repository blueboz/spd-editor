package cn.boz.jb.plugin.floweditor.gui.hist;

/**
 * 可被回滚
 */
public interface Restorable {
    BaseState serialize();

    void restore(BaseState state);
}
