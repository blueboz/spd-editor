package cn.boz.jb.plugin.floweditor.gui.process.definition;

import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ProcessDefinition {
    private String id;
    private String name;
    private List<Shape> shapes=new ArrayList<>();
    private List<Line> lines=new ArrayList<>();

    public List<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
