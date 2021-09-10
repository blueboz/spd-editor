package cn.boz.jb.plugin.floweditor.gui.process.bridge;

import cn.boz.jb.plugin.floweditor.gui.process.control.Diagram;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.utils.DiagramUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.Element;

public class RectBridge extends Shape implements Diagram
{
    @Override
    public void drawContent(ChartPanel chartPanel) {

        super.drawContent(chartPanel);
    }

    @Override
    public Element buildProcessNode() {
        return null;
    }

    @Override
    public Element buildDiagramNode() {
        return DiagramUtils.buildDiagramNode(this);
    }

    @Override
    public void init(HiPoint hiPoint){
        this.setX(hiPoint.x);
        this.setY(hiPoint.y);
        this.setWidth(105);
        this.setHeight(55);
    }

    @Override
    public void init(Rect rect) {
        this.setX(rect.getX());
        this.setY(rect.getY());
        this.setWidth(105);
        this.setHeight(55);
    }
}
