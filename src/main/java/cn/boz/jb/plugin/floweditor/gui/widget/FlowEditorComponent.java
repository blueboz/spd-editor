package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.EndEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.StartEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Circle;
import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.Prismatic;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.utils.ConstantUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class FlowEditorComponent extends JComponent implements MouseListener {

    private ChartPanel gPanel;
    private JPanel menu;
    PropertyEditor propertyEditor;

    public FlowEditorComponent() {
        //处理图形组件
        gPanel = new ChartPanel(null,null,null);

        //处理菜单组件
        menu = new JPanel();
        menu.setBackground(ConstantUtils.getInstance().getBtnBarColor());
        processMenu();

        //处理属性编辑器组件
        propertyEditor = new PropertyEditor(gPanel);

        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        this.add(gPanel,BorderLayout.CENTER);
        this.add(menu,BorderLayout.NORTH);
        this.add(propertyEditor,BorderLayout.SOUTH);
    }

    /**
     * 处理菜单的相关细节
     */
    private void processMenu() {
        Button flowbtn = new Button(IcoMoonUtils.getSequenceFlow(), true, "flowbtn", "oper");
        flowbtn.addMouseListener(this);
        flowbtn.setToolTipText("Flow Line");
        menu.add(flowbtn);

        Button user = new Button(IcoMoonUtils.getUserTask(), true, "user", "oper");
        user.setToolTipText("User Task");
        user.addMouseListener(this);
        menu.add(user);

        Button service = new Button(IcoMoonUtils.getServiceTask(), true, "service", "oper");
        service.addMouseListener(this);
        service.setToolTipText("Service Task");
        menu.add(service);

        Button call = new Button(IcoMoonUtils.getCallActivity(), true, "call", "oper");
        call.addMouseListener(this);
        call.setToolTipText("Call Activity");
        menu.add(call);

        Button exclude = new Button(IcoMoonUtils.getExclusiveGateway(), true, "exclude", "oper");
        exclude.addMouseListener(this);
        exclude.setToolTipText("Exclusive Gateway");
        menu.add(exclude);

        Button parallel = new Button(IcoMoonUtils.getParallelGateway(), true, "parallel", "oper");
        parallel.addMouseListener(this);
        parallel.setToolTipText("Parallel Gateway");
        menu.add(parallel);

        Button foreach = new Button(IcoMoonUtils.getForeachGateway(), true, "foreach", "oper");
        foreach.addMouseListener(this);
        foreach.setToolTipText("Foreach Gateway");
        menu.add(foreach);

        Button start = new Button(IcoMoonUtils.getStartEvent(), true, "start", "oper");
        start.addMouseListener(this);
        start.setToolTipText("Start Event");

        menu.add(start);


        Button end = new Button(IcoMoonUtils.getEndEvent(), true, "stop", "oper");
        end.addMouseListener(this);
        end.setToolTipText("End Event");
        menu.add(end);


        Button movebtn = new Button(IcoMoonUtils.getMove(), true, "movebtn", "oper");
        movebtn.addMouseListener(this);
        movebtn.setToolTipText("Move");
        movebtn.setToggle(true);
        menu.add(movebtn);

        Button handbtn = new Button(IcoMoonUtils.getScale(), true, "handbtn", "oper");
        handbtn.addMouseListener(this);
        handbtn.setToolTipText("Hand");
        menu.add(handbtn);

        Button crosshairs = new Button(IcoMoonUtils.getAlign(), false, "crosshairs");
        crosshairs.addMouseListener(this);
        crosshairs.setToolTipText("Reset");
        menu.add(crosshairs);


        Button equation = new Button(IcoMoonUtils.getEquation(), false, "equation");
        equation.addMouseListener(this);
        equation.setToolTipText("1:1");
        menu.add(equation);

        Button undo = new Button(IcoMoonUtils.getUndo(), false, "undo");
        undo.addMouseListener(this);
        undo.setToolTipText("Undo");
        menu.add(undo);

        Button redo = new Button(IcoMoonUtils.getRedo(), false, "redo");
        redo.addMouseListener(this);
        redo.setToolTipText("Redo");
        menu.add(redo);

        Button erase = new Button(IcoMoonUtils.getErase(), false, "erase");
        erase.addMouseListener(this);
        erase.setToolTipText("Erase");
        menu.add(erase);


        Button photo = new Button(IcoMoonUtils.getImage(), false, "photo");
        photo.addMouseListener(this);
        photo.setToolTipText("Export");
        menu.add(photo);


        Button trash = new Button(IcoMoonUtils.getTrash(), false, "trash");
        trash.addMouseListener(this);
        trash.setToolTipText("Trash");
        menu.add(trash);

        Button label = new Button(IcoMoonUtils.getHText(), false, "label", false);
        label.addMouseListener(this);
        label.setToolTipText("Label Show Switcher");
        menu.add(label);


        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        menu.setLayout(flowLayout);

        menu.setBorder(null);

    }


    @Override
    public void mouseClicked(MouseEvent e) {


    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        Button myButton = (Button) e.getSource();
        switch (myButton.getId()) {
            case "linebtn":
                gPanel.setModeOfNewLine(Line.class);
                break;
            case "flowbtn":
                gPanel.setModeOfNewLine(SequenceFlow.class);
                break;
            case "squarebtn":
                gPanel.setModeOfNewShape(Shape.class);
                break;
            case "movebtn":

                gPanel.setMode(ChartPanel.MODE_DEFAULT);
                break;
            case "user":
//                gPanel.setModeOfNewShape(UserTask.class);
                gPanel.setModeOfNewShape(UserTask.class,IcoMoonUtils.initCursor(IcoMoonUtils.getUserTask()));
                break;
            case "handbtn":
                gPanel.setMode(ChartPanel.MODE_MOVE);
                break;
            case "crosshairs":
                gPanel.resetOriginalPoint();
                break;
            case "circle":
                gPanel.setModeOfNewShape(Circle.class);
                break;
            case "winclose":
                gPanel.setModeOfNewShape(Prismatic.class);
                break;
            case "start":
                gPanel.setModeOfNewShape(StartEvent.class,IcoMoonUtils.initCursor(IcoMoonUtils.getStartEvent()));

                break;
            case "stop":
                gPanel.setModeOfNewShape(EndEvent.class,IcoMoonUtils.initCursor(IcoMoonUtils.getEndEvent()));
                break;
            case "foreach":
                gPanel.setModeOfNewShape(ForeachGateway.class,IcoMoonUtils.initCursor(IcoMoonUtils.getForeachGateway()));
                break;
            case "parallel":
                gPanel.setModeOfNewShape(ParallelGateway.class,IcoMoonUtils.initCursor(IcoMoonUtils.getParallelGateway()));
                break;
            case "exclude":
                gPanel.setModeOfNewShape(ExclusiveGateway.class,IcoMoonUtils.initCursor(IcoMoonUtils.getExclusiveGateway()));
                break;
            case "call":
                gPanel.setModeOfNewShape(CallActivity.class,IcoMoonUtils.initCursor(IcoMoonUtils.getCallActivity()));
                break;
            case "service":
                gPanel.setModeOfNewShape(ServiceTask.class,IcoMoonUtils.initCursor(IcoMoonUtils.getServiceTask()));
                break;
            case "equation":
                gPanel.resetScale();
                break;
            case "trash":
                gPanel.shapeDelete();
                repaint();
                break;
            case "redo":
                gPanel.redo();
                break;
            case "undo":
                gPanel.undo();
                break;
            case "load":
                gPanel.load();
                repaint();
                break;
            case "erase":
                gPanel.clear();
                repaint();
                break;
            case "photo":
                gPanel.export();
                break;
            case "save":
                gPanel.save();
                break;
            case "theme":


                break;
            case "label":

                String ttl = myButton.getTitle();
                if (ttl.equals(IcoMoonUtils.getHText())) {
                    ConstantUtils.getInstance().setLabelShowMode(ConstantUtils.LABEL_SHOW_FULL);
                    myButton.setTitle(IcoMoonUtils.getFText());
                    repaint();
                } else if (ttl.equals(IcoMoonUtils.getFText())) {
                    ConstantUtils.getInstance().setLabelShowMode(ConstantUtils.LABEL_SHOW_NONE);
                    myButton.setTitle(IcoMoonUtils.getEText());
                    repaint();
                } else if (ttl.equals(IcoMoonUtils.getEText())) {
                    ConstantUtils.getInstance().setLabelShowMode(ConstantUtils.LABEL_SHOW_ONLY_GATEWAY);
                    myButton.setTitle(IcoMoonUtils.getHText());
                    repaint();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public ChartPanel getgPanel() {
        return gPanel;
    }

    public void setgPanel(ChartPanel gPanel) {
        this.gPanel = gPanel;
    }
}
