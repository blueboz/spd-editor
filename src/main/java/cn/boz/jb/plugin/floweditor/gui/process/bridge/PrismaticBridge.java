package cn.boz.jb.plugin.floweditor.gui.process.bridge;

import cn.boz.jb.plugin.floweditor.gui.process.control.Diagram;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Prismatic;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.DiagramUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.Element;

public class PrismaticBridge extends Prismatic implements Diagram {
    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);
    }

    @Override
    public Element buildProcessNode() {
        return null;
    }

    @Override
    public Element buildDiagramNode(){
        return DiagramUtils.buildDiagramNode(this);
    }

    @Override
    public void init(HiPoint hiPoint){
        this.setX(hiPoint.x-20);
        this.setY(hiPoint.y-20);
        this.setWidth(40);
        this.setHeight(40);
    }

    @Override
    public void init(Rect rect) {
        this.setX(rect.getX());
        this.setY(rect.getY());
        this.setWidth(40);
        this.setHeight(40);
    }

    @Override
    public Property[] getPropertyEditors() {
        Property[] ps = new Property[]{
                new TextFieldProperty("name", this),
        };
        return ps;
    }
}
