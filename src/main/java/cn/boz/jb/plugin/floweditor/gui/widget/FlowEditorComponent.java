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
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class FlowEditorComponent extends JComponent implements MouseListener {

    private ChartPanel gPanel;
    private JPanel menu;
    PropertyEditor propertyEditor;

    public FlowEditorComponent() {
        //处理图形组件
        gPanel = new ChartPanel();

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
        Button flowbtn = new Button(IcoMoonUtils.getSequenceFlow(), true, "flowbtn", "oper", IcoMoonUtils.getFont());
        flowbtn.addMouseListener(this);
        menu.add(flowbtn);
        Button user = new Button(IcoMoonUtils.getUserTask(), true, "user", "oper", IcoMoonUtils.getFont());
        user.addMouseListener(this);
        menu.add(user);
        Button service = new Button(IcoMoonUtils.getServiceTask(), true, "service", "oper", IcoMoonUtils.getFont());
        service.addMouseListener(this);
        menu.add(service);
        Button call = new Button(IcoMoonUtils.getCallActivity(), true, "call", "oper", IcoMoonUtils.getFont());
        call.addMouseListener(this);
        menu.add(call);

        Button exclude = new Button(IcoMoonUtils.getExclusiveGateway(), true, "exclude", "oper", IcoMoonUtils.getFont());
        exclude.addMouseListener(this);
        menu.add(exclude);

        Button parallel = new Button(IcoMoonUtils.getParallelGateway(), true, "parallel", "oper", IcoMoonUtils.getFont());
        parallel.addMouseListener(this);
        menu.add(parallel);

        Button foreach = new Button(IcoMoonUtils.getForeachGateway(), true, "foreach", "oper", IcoMoonUtils.getFont());
        foreach.addMouseListener(this);
        menu.add(foreach);

        Button start = new Button(IcoMoonUtils.getStartEvent(), true, "start", "oper", IcoMoonUtils.getFont());
        start.addMouseListener(this);
        menu.add(start);


        Button end = new Button(IcoMoonUtils.getEndEvent(), true, "stop", "oper", IcoMoonUtils.getFont());
        end.addMouseListener(this);
        menu.add(end);


        Button movebtn = new Button(FontUtils.arrows(), true, "movebtn", "oper");
        movebtn.addMouseListener(this);
        movebtn.setToggle(true);
        menu.add(movebtn);
        Button handbtn = new Button(FontUtils.hand(), true, "handbtn", "oper");
        handbtn.addMouseListener(this);
        menu.add(handbtn);
        Button crosshairs = new Button(FontUtils.crosshairs(), false, "crosshairs");
        crosshairs.addMouseListener(this);
        menu.add(crosshairs);
//        Button winclose = new Button(FontUtils.windowclose(), false, "winclose");
//        winclose.addMouseListener(this);
//        menu.add(winclose);
        Button equation = new Button(FontUtils.cube(), false, "equation");
        equation.addMouseListener(this);
        menu.add(equation);
        Button undo = new Button(FontUtils.rotateLeft(), false, "undo");
        undo.addMouseListener(this);
        menu.add(undo);
        Button redo = new Button(FontUtils.rotateRight(), false, "redo");
        redo.addMouseListener(this);
        menu.add(redo);

        Button erase = new Button(FontUtils.erase(), false, "erase");
        erase.addMouseListener(this);
        menu.add(erase);

        Button load = new Button(FontUtils.load(), false, "load");
        load.addMouseListener(this);
        menu.add(load);

        Button photo = new Button(FontUtils.photo(), false, "photo");
        photo.addMouseListener(this);
        menu.add(photo);

        Button save = new Button(FontUtils.save(), false, "save");
        save.addMouseListener(this);
        menu.add(save);

        Button theme = new Button(FontUtils.sun(), false, "theme");
        theme.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                String title = theme.getTitle();
                if (title.equals(FontUtils.sun())) {
                    theme.setTitle(FontUtils.moon());
                } else if (title.equals(FontUtils.moon())) {
                    theme.setTitle(FontUtils.paint());
                } else if (title.equals(FontUtils.paint())) {
                    theme.setTitle(FontUtils.sun());
                }
                title = theme.getTitle();
                if (title.equals(FontUtils.sun())) {
                    ConstantUtils.getInstance().setColorModeLight();
                    menu.setBackground(ConstantUtils.getInstance().getBtnBarColor());
                    repaint();
                } else if (title.equals(FontUtils.moon())) {
                    ConstantUtils.getInstance().setColorModeDark();
                    menu.setBackground(ConstantUtils.getInstance().getBtnBarColor());
                    repaint();
                } else if (title.equals(FontUtils.paint())) {
                    ConstantUtils.getInstance().setColorfulMode();
                    menu.setBackground(ConstantUtils.getInstance().getBtnBarColor());
                    repaint();
                }
                super.mouseClicked(e);
            }
        });
        theme.addMouseListener(this);
        menu.add(theme);

        Button trash = new Button(FontUtils.trash(), false, "trash");
        trash.addMouseListener(this);
        menu.add(trash);

        Button label = new Button(FontUtils.starhalf(), false, "label", false);

        label.addMouseListener(this);
        menu.add(label);


        menu.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
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
                gPanel.setModeOfNewShape(UserTask.class);
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
                gPanel.setModeOfNewShape(StartEvent.class);
                break;
            case "stop":
                gPanel.setModeOfNewShape(EndEvent.class);
                break;
            case "foreach":
                gPanel.setModeOfNewShape(ForeachGateway.class);
                break;
            case "parallel":
                gPanel.setModeOfNewShape(ParallelGateway.class);
                break;
            case "exclude":
                gPanel.setModeOfNewShape(ExclusiveGateway.class);
                break;
            case "call":
                gPanel.setModeOfNewShape(CallActivity.class);
                break;
            case "service":
                gPanel.setModeOfNewShape(ServiceTask.class);
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
                if (ttl.equals(FontUtils.starhalf())) {
                    ConstantUtils.getInstance().setLabelShowMode(ConstantUtils.LABEL_SHOW_FULL);
                    myButton.setTitle(FontUtils.starfull());
                    repaint();
                } else if (ttl.equals(FontUtils.starfull())) {
                    ConstantUtils.getInstance().setLabelShowMode(ConstantUtils.LABEL_SHOW_NONE);
                    myButton.setTitle(FontUtils.starempty());
                    repaint();
                } else if (ttl.equals(FontUtils.starempty())) {
                    ConstantUtils.getInstance().setLabelShowMode(ConstantUtils.LABEL_SHOW_ONLY_GATEWAY);
                    myButton.setTitle(FontUtils.starhalf());
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
