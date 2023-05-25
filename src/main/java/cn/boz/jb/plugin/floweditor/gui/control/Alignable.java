package cn.boz.jb.plugin.floweditor.gui.control;

public interface Alignable {

    int REL_POS_NAIL = 0x0000;
    int REL_POS_LEFT_OUT = 0x0001;
    int REL_POS_LEFT_IN = 0x0002;
    int REL_POS_CENTER = 0x0004;
    int REL_POS_RIGHT_IN = 0x0008;
    int REL_POS_RIGHT_OUT = 0x0010;
    /**
     * 水平对齐
     */
    int REL_HORIZONTAL = 0x0100;
    /**
     * 垂直对齐
     */
    int REL_VERTICAL = 0x0200;

    /**
     * 参考
     * {@link #REL_HORIZONTAL}
     * {@link #REL_VERTICAL}
     * {@link #REL_POS_LEFT_IN}
     * {@link #REL_POS_LEFT_OUT}
     * {@link #REL_POS_CENTER}
     * {@link #REL_POS_RIGHT_IN}
     * {@link #REL_POS_RIGHT_OUT}
     *
     * @param relpos
     */
    void setAlign(int relpos);

    /**
     *
     */
    void resetAlign();

    /**
     * 是否可以作为对齐参考物?
     *
     * @return
     */
    boolean alignRefAble();

    /**
     * 是否对齐自身
     *
     * @return
     */
    boolean alignSelf();

}
