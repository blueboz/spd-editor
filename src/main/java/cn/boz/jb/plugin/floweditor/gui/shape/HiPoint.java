package cn.boz.jb.plugin.floweditor.gui.shape;

public class HiPoint implements Cloneable {
    public double x;
    public double y;

    public HiPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "HiPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        HiPoint hiPoint = new HiPoint(x, y);
        return hiPoint;
    }
}
