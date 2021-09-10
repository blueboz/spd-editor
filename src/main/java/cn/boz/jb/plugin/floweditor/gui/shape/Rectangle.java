package cn.boz.jb.plugin.floweditor.gui.shape;

import java.awt.Color;

/**
 * 矩形
 */
public class Rectangle extends Shape{

    public Rectangle(double x, double y, int status, Color c) {
        super(x, y,  c);
    }

    public Rectangle(double x, double y, int status) {
        super(x, y);
    }

    public Rectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public Rectangle(double x, double y, double width, double height, int status) {
        super(x, y, width, height);
    }

    public Rectangle(double x, double y, double width, double height, int status, Color c) {
        super(x, y, width, height, c);
    }

}
