package cn.boz.jb.plugin.floweditor.gui.shape;

/**
 * 附带父子关系的节点链接
 */
public class PointLink {
    //前一个节点
    private HiPoint prev;
    //虚拟的当前节点，用于对offset进行参考与引用,在鼠标点击下去的时候记录
    private HiPoint virtual;
    //下一个节点
    private HiPoint next;
    //当前节点的实际上的坐标
    private HiPoint val;
    //是否为虚拟节点
    private boolean real;
    //头部节点
    private HiPoint head;
    //上一个是头部
    private boolean prevIsHead;
    //尾部节点
    private HiPoint tail;
    //下一个是尾部
    private boolean nextIsTail;

    public HiPoint getPrev() {
        return prev;
    }

    public void setPrev(HiPoint prev) {
        this.prev = prev;
    }

    public HiPoint getVirtual() {
        return virtual;
    }

    public void setVirtual(HiPoint virtual) {
        this.virtual = virtual;
    }

    public HiPoint getNext() {
        return next;
    }

    public void setNext(HiPoint next) {
        this.next = next;
    }

    public HiPoint getVal() {
        return val;
    }

    public void setVal(HiPoint val) {
        this.val = val;
    }

    public boolean isReal() {
        return real;
    }

    public void setReal(boolean real) {
        this.real = real;
    }

    public HiPoint getHead() {
        return head;
    }

    public void setHead(HiPoint head) {
        this.head = head;
    }

    public boolean isPrevIsHead() {
        return prevIsHead;
    }

    public void setPrevIsHead(boolean prevIsHead) {
        this.prevIsHead = prevIsHead;
    }

    public HiPoint getTail() {
        return tail;
    }

    public void setTail(HiPoint tail) {
        this.tail = tail;
    }

    public boolean isNextIsTail() {
        return nextIsTail;
    }

    public void setNextIsTail(boolean nextIsTail) {
        this.nextIsTail = nextIsTail;
    }
}
