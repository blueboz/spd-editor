package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.FlowSqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.LineBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SequenceFlow extends LineBridge implements FlowSqlAggregator {
    private String sourceRef;
    private String targetRef;
    private String conditionExpression;

    @Override
    public String getSourceRef() {
        return sourceRef;
    }

    @Override
    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    @Override
    public String getTargetRef() {
        return targetRef;
    }

    @Override
    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public SequenceFlow() {
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("sequenceFlow");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        element.addAttribute("sourceRef", this.getSourceRef());
        element.addAttribute("targetRef", this.getTargetRef());
        element.addAttribute("conditionExpression", this.getConditionExpression());
        return element;
    }

    public Element buildDiagramNode() {
        Element edge = DocumentHelper.createElement("Edge");
        edge.addAttribute("Element", this.getId());
        edge.addAttribute("id", "Shape_" + this.getId());

        for (HiPoint point : this.getAllPointWithStartEnd()) {
            Element waypoint = edge.addElement("Waypoint");
            waypoint.addAttribute("x", String.valueOf((int) Math.ceil(point.x)));
            waypoint.addAttribute("y", String.valueOf((int) Math.ceil(point.y)));
        }
        //是否输出label是取决于模式

        Label label = this.getLabel();
        if (label != null && label.visiable()) {
            Element lb = edge.addElement("Label");
            Element bound = lb.addElement("Bounds");
            bound.addAttribute("height", String.valueOf((int) Math.ceil(label.getHeight())));
            bound.addAttribute("width", String.valueOf((int) Math.ceil(label.getWidth())));
            bound.addAttribute("x", String.valueOf((int) Math.ceil(label.getX())));
            bound.addAttribute("y", String.valueOf((int) Math.ceil(label.getY())));
        }
        return edge;
    }


    @Override
    public String getIdPrefix() {
        return "flow";
    }

    /**
     * 初始化的必要操作
     *
     * @param startShape
     * @param endShape
     */
    @Override
    public void init(Shape startShape, Shape endShape) {
        super.init(startShape, endShape);
        this.setSourceRef(startShape.getId());
        this.setTargetRef(endShape.getId());
    }

    @Override
    public Property[] getPropertyEditors() {
        Property[] ps = new Property[]{
                new TextFieldProperty("name", this, (ctx) -> {
                    Label label = this.getLabel();
                    label.setText((String) ctx.getNewValue());
                }),
                new TextFieldProperty("conditionExpression", this),
        };
        return ps;
    }

    @Override
    public String toSql(String processId) {
        return String.format("INSERT INTO ENGINE_FLOW (PROCESSID_, SOURCE_, TARGET_, CONDITION_, ORDER_) " +
                "VALUES ('%s', '%s', '%s', null, 0);", processId, this.getSourceRef(), this.getTargetRef());
    }
}
