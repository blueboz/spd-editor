package cn.boz.jb.plugin.floweditor.gui.utils;

import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DiagramUtils {

    public static Element buildDiagramNode(Shape shape) {
        Element element = DocumentHelper.createElement("Shape");
        element.addAttribute("Element", shape.getId());
        element.addAttribute("id", "Shape_" + shape.getId());
        Element bounds = element.addElement("Bounds");
        bounds.addAttribute("width", String.valueOf((int) Math.ceil(shape.getWidth())));
        bounds.addAttribute("height", String.valueOf((int) Math.ceil(shape.getHeight())));
        bounds.addAttribute("x", String.valueOf((int) Math.ceil(shape.getX())));
        bounds.addAttribute("y", String.valueOf((int) Math.ceil(shape.getY())));
        return element;
    }
}
