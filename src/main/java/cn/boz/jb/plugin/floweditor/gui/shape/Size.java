package cn.boz.jb.plugin.floweditor.gui.shape;

//图形大小体积
public class Size {
    //宽度
    private double w;
    //高度
    private double h;

    public Size(double w, double h) {
        this.w = w;
        this.h = h;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public Size() {
    }
}
