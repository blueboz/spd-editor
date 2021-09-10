package cn.boz.jb.plugin.floweditor.gui.process.control;

import org.dom4j.Element;

public interface Diagram {
    Element buildProcessNode();

    Element buildDiagramNode();
}
