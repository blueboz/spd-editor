package cn.boz.jb.plugin.floweditor.gui.process.bridge;

import cn.boz.jb.plugin.floweditor.gui.process.control.Diagram;
import cn.boz.jb.plugin.floweditor.gui.shape.Circle;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.DiagramUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.Element;

public class CircleBridge extends Circle implements Diagram {


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
        this.setX(hiPoint.x-17);
        this.setY(hiPoint.y-17);
        this.setWidth(35);
        this.setHeight(35);
    }

    @Override
    public void init(Rect rect) {
        this.setX(rect.getX());
        this.setY(rect.getY());
        this.setWidth(35);
        this.setHeight(35);
    }

    @Override
    public void drawContent(ChartPanel chartPanel) {
        double diameter;
        if(this.width>this.height){
            diameter=this.height;
        }else{
            diameter=this.width;
        }
        double newx=this.x+this.width/2-diameter/2;
        double newy=this.y+this.height/2-diameter/2;
        chartPanel.fillOval(newx, newy, diameter, diameter);
        super.drawInnerBorder(chartPanel, this, borderWidth, borderStyle, borderColor);
    }
}
