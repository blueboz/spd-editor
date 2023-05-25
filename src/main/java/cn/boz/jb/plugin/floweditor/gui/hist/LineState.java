package cn.boz.jb.plugin.floweditor.gui.hist;

import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;

import java.util.LinkedList;

public class LineState extends BaseState {
    public Shape startShape;
    public Shape endShape;
    public LinkedList<HiPoint> points = new LinkedList<>();
    //图形的引用指针，方便后面进行

    public Label label;

    @Override
    public String toString() {
        return "LineState{" +
                "points=" + points +
                '}';
    }
}
