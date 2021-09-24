package cn.boz.jb.plugin.floweditor.gui.file;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.CircleBridge;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.LineBridge;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.PrismaticBridge;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.process.control.Diagram;
import cn.boz.jb.plugin.floweditor.gui.process.definition.ProcessDefinition;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.Bound;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.EndEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.StartEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.WayPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateLoaderImpl implements TemplateLoader {

    private static TemplateLoaderImpl INSTANCE = new TemplateLoaderImpl();

    private TemplateLoaderImpl() {

    }

    public static TemplateLoaderImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public ProcessDefinition loadFromFile(String filename) {
        try {
            ProcessDefinition processDefinition = new ProcessDefinition();

            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(filename);
            Element definitions = doc.getRootElement();


            Element diagram = definitions.element("Diagram");
            HashMap<String, Object> diagramMap = new HashMap<>();
            List<Element> diagrams = diagram.elements();
            for (Element d : diagrams) {
                String element = d.attribute("Element").getValue();
                diagramMap.put(element, d);
            }

            Map<String, Object> processMap = new HashMap<>();
            Element process = definitions.element("process");
            processDefinition.setId(attributeValue(process.attribute("id")));
            processDefinition.setName(attributeValue(process.attribute("name")));

            List<Element> processs = process.elements();
            for (Element p : processs) {
                String eName = p.getName();
                String id = p.attribute("id").getValue();
                String name = p.attribute("name").getValue();
                Element showp = (Element) diagramMap.get(id);
                if ("startEvent".equals(eName)) {
                    StartEvent startEvent = new StartEvent();
                    setCircleBridge(startEvent, id, name);
                    setBound(startEvent, readBounds(showp).get(0));
                    processMap.put(id, startEvent);
                } else if ("endEvent".equals(eName)) {
                    EndEvent endEvent = new EndEvent();
                    setCircleBridge(endEvent, id, name);
                    setBound(endEvent, readBounds(showp).get(0));
                    processMap.put(id, endEvent);
                } else if ("serviceTask".equals(eName)) {

                    ServiceTask serviceTask = new ServiceTask();
                    setRectBridge(serviceTask, id, name);
                    serviceTask.setExpression(attributeValue(p.attribute("expression")));
                    serviceTask.setListener(attributeValue(p.attribute("listener")));
                    setBound(serviceTask, readBounds(showp).get(0));
                    processMap.put(id, serviceTask);
                } else if ("sequenceFlow".equals(eName)) {
                    SequenceFlow sequenceFlow = new SequenceFlow();
                    setLineBridge(sequenceFlow, id, name);
                    sequenceFlow.setSourceRef(attributeValue(p.attribute("sourceRef")));
                    sequenceFlow.setTargetRef(attributeValue(p.attribute("targetRef")));
                    sequenceFlow.setConditionExpression(attributeValue(p.attribute("conditionExpression")));

                    List<WayPoint> wayPoints = readWaypoint(showp);
                    for (WayPoint wayPoint : wayPoints) {
                        sequenceFlow.getPoints().add(new HiPoint(wayPoint.getX(), wayPoint.getY()));
                    }
                    Element labelElement = showp.element("Label");
                    if (labelElement != null) {
                        Bound bound = readBounds(labelElement).get(0);
                        Label label = new Label(bound.getX(), bound.getY(), bound.getWidth(), bound.getHeight(), name);
                        //建立双向绑定
                        sequenceFlow.setLabel(label);
                        label.setBoundLine(sequenceFlow);
                    } else {
                        Label label = new Label(10, 0, 0, 0, name);
                        //建立双向绑定
                        sequenceFlow.setLabel(label);
                        label.setBoundLine(sequenceFlow);
                    }
                    processMap.put(id, sequenceFlow);
                } else if ("exclusiveGateway".equals(eName)) {
                    ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
                    exclusiveGateway.setId(id);
                    exclusiveGateway.setName(name);

                    setBound(exclusiveGateway, readBounds(showp).get(0));
                    processMap.put(id, exclusiveGateway);

                } else if ("foreachGateway".equals(eName)) {
                    ForeachGateway foreachGateway = new ForeachGateway();
                    foreachGateway.setId(id);
                    foreachGateway.setName(name);

                    setBound(foreachGateway, readBounds(showp).get(0));
                    processMap.put(id, foreachGateway);

                } else if ("parallelGateway".equals(eName)) {
                    ParallelGateway parallelGateway = new ParallelGateway();
                    parallelGateway.setId(id);
                    parallelGateway.setName(name);

                    setBound(parallelGateway, readBounds(showp).get(0));
                    processMap.put(id, parallelGateway);

                } else if ("userTask".equals(eName)) {
                    UserTask userTask = new UserTask();
                    setRectBridge(userTask, id, name);
                    userTask.setBussinesId(attributeValue(p.attribute("bussinesId")));
                    userTask.setBussinesKey(attributeValue(p.attribute("bussinesKey")));
                    userTask.setBussinesDescrition(attributeValue(p.attribute("bussinesDescrition")));
                    userTask.setRights(attributeValue(p.attribute("rights")));
                    userTask.setExpression(attributeValue(p.attribute("expression")));
                    userTask.setEventListener(attributeValue(p.attribute("eventListener")));
                    userTask.setValidSecond(attributeValue(p.attribute("validSecond")));
                    userTask.setOpenSecond(attributeValue(p.attribute("openSecond")));
                    setBound(userTask, readBounds(showp).get(0));
                    processMap.put(id, userTask);
                } else if ("callActivity".equals(eName)) {
                    CallActivity callActivity = new CallActivity();
                    setRectBridge(callActivity, id, name);
                    callActivity.setCalledElement(attributeValue(p.attribute("calledElement")));
                    setBound(callActivity, readBounds(showp).get(0));
                    processMap.put(id, callActivity);

                }
            }

            //过滤，绑定流程图的startShape与endShape
            List<SequenceFlow> seqFlows = processMap.values().stream().filter(value -> value instanceof SequenceFlow).map(o -> (SequenceFlow) o).collect(Collectors.toList());
            for (SequenceFlow seqFlow : seqFlows) {

                seqFlow.setStartShape((Shape) processMap.get(seqFlow.getSourceRef()));
                seqFlow.setEndShape((Shape) processMap.get(seqFlow.getTargetRef()));
                seqFlow.getStartShape().headOfLine.add(seqFlow);
                seqFlow.getEndShape().tailOfLine.add(seqFlow);
                if (seqFlow.getStartShape() == null || seqFlow.getEndShape() == null) {
                    throw new RuntimeException(seqFlow.getId() + " " + seqFlow.getName());
                }
            }
            //binRelation
            processMap.forEach((key, value) -> {
                if (value instanceof Shape) {
                    processDefinition.getShapes().add((Shape) value);
                } else if (value instanceof Line) {
                    processDefinition.getLines().add((Line) value);
                }
            });
            return processDefinition;

        } catch (DocumentException e) {
            e.printStackTrace();
        }


        return null;
    }


    private void setLineBridge(LineBridge lineBridge, String id, String name) {
        lineBridge.setId(id);
        lineBridge.setName(name);
    }


    private void setCircleBridge(CircleBridge circleBridge, String id, String name) {
        circleBridge.setId(id);
        circleBridge.setName(name);
    }

    private void setRectBridge(RectBridge rectBridge, String id, String name) {
        rectBridge.setId(id);
        rectBridge.setName(name);
    }

    private void setPrismaticBridge(PrismaticBridge prismaticBridge, String id, String name) {
        prismaticBridge.setId(id);
        prismaticBridge.setName(name);
    }

    private void setBound(Shape shape, Bound bound) {
        shape.setX(bound.getX());
        shape.setY(bound.getY());
        shape.setWidth(bound.getWidth());
        shape.setHeight(bound.getHeight());
    }

    private String attributeValue(Attribute attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();

    }

    private List<WayPoint> readWaypoint(Element element) {
        List<Element> bounds = element.elements("Waypoint");
        List<WayPoint> res = new ArrayList<>();
        for (int i = 0; i < bounds.size(); i++) {
            //掐头去尾
            if (i == 0) {
                continue;
            }
            if (i == bounds.size() - 1) {
                continue;
            }
            Element ele = bounds.get(i);
            int x = Integer.parseInt(ele.attribute("x").getValue());
            int y = Integer.parseInt(ele.attribute("y").getValue());
            WayPoint bound = new WayPoint(x, y);
            res.add(bound);
        }

        return res;
    }

    private List<Bound> readBounds(Element element) {
        List<Element> bounds = element.elements("Bounds");
        List<Bound> res = new ArrayList<>();
        for (Element ele : bounds) {
            int x = Integer.parseInt(ele.attribute("x").getValue());
            int y = Integer.parseInt(ele.attribute("y").getValue());
            int width = Integer.parseInt(ele.attribute("width").getValue());
            int height = Integer.parseInt(ele.attribute("height").getValue());
            Bound bound = new Bound(x, y, width, height);
            res.add(bound);
        }
        return res;
    }


    @Override
    public void saveToFile(ProcessDefinition processDefinition, String filename) {
        Document doc = DocumentHelper.createDocument();
        Element definitions = doc.addElement("definitions");
        Element process = definitions.addElement("process");
        process.addAttribute("id", processDefinition.getId());
        process.addAttribute("name", processDefinition.getName());

        Element diagram = definitions.addElement("Diagram");
        diagram.addAttribute("id", "Diagram_" + processDefinition.getId());

        for (Shape shape : processDefinition.getShapes()) {
            if (shape instanceof Diagram) {
                Diagram d = (Diagram) shape;
                Element pNode = d.buildProcessNode();
                if (pNode != null) {
                    process.add(pNode);
                }
                Element dnode = d.buildDiagramNode();
                if (dnode != null) {
                    diagram.add(dnode);
                }
            }
        }

        for (Line line : processDefinition.getLines()) {
            if (line instanceof SequenceFlow) {
                Element pnode = ((SequenceFlow) line).buildProcessNode();
                process.add(pnode);
                Element dnode = ((SequenceFlow) line).buildDiagramNode();
                diagram.add(dnode);
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();

        format.setIndent(true);
        format.setIndentSize(4);  // 行缩进
        format.setNewlines(true); // 一个结点为一行
        format.setTrimText(true); // 去重空格
        format.setPadText(true);
        format.setNewLineAfterDeclaration(false);
        format.setEncoding("UTF-8");
        try (FileOutputStream fis = new FileOutputStream(filename)) {
            XMLWriter xmlWriter = new XMLWriter(fis, format);
            xmlWriter.write(doc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
