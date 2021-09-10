package cn.boz.jb.plugin.floweditor.gui;

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
import cn.boz.jb.plugin.floweditor.gui.widget.Button;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class FlowEditorGui extends JFrame implements MouseListener {

    private ChartPanel gPanel;
    private JPanel menu;

    public FlowEditorGui() {
        this.setTitle("流程图工具v1.0");
        BufferedImage read = null;
        try {
            read = ImageIO.read(FlowEditorGui.class.getClassLoader().getResourceAsStream("flow2.png"));
            this.setIconImage(read);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gPanel = new ChartPanel();

        menu = new JPanel();
        menu.setBackground(Color.white);

        processMenu();
        this.add(menu, BorderLayout.PAGE_START, 0);
        this.add(gPanel, 1);
        this.pack();

    }

    /**
     * 处理菜单的相关细节
     */
    private void processMenu() {
        Button flowbtn = new Button(FontUtils.arrowleft(), true, "flowbtn", "oper");
        flowbtn.addMouseListener(this);
        menu.add(flowbtn);
        Button user = new Button(FontUtils.user(), true, "user", "oper");
        user.addMouseListener(this);
        menu.add(user);
        Button service = new Button(FontUtils.cog(), true, "service", "oper");
        service.addMouseListener(this);
        menu.add(service);
        Button call = new Button(FontUtils.plus(), true, "call", "oper");
        call.addMouseListener(this);
        menu.add(call);

        Button exclude = new Button(FontUtils.close(), true, "exclude", "oper");
        exclude.addMouseListener(this);
        menu.add(exclude);

        Button parallel = new Button(FontUtils.plus(), true, "parallel", "oper");
        parallel.addMouseListener(this);
        menu.add(parallel);

        Button foreach = new Button(FontUtils.circleo(), true, "foreach", "oper");
        foreach.addMouseListener(this);
        menu.add(foreach);

        Button start = new Button(FontUtils.play(), true, "start", "oper");
        start.addMouseListener(this);
        menu.add(start);

        Button end = new Button(FontUtils.stop(), true, "stop", "oper");
        end.addMouseListener(this);
        menu.add(end);



        Button movebtn = new Button(FontUtils.arrows(), true, "movebtn", "oper");
        movebtn.addMouseListener(this);
        menu.add(movebtn);
        Button handbtn = new Button(FontUtils.hand(), true, "handbtn", "oper");
        handbtn.addMouseListener(this);
        menu.add(handbtn);
        Button crosshairs = new Button(FontUtils.crosshairs(), false, "crosshairs");
        crosshairs.addMouseListener(this);
        menu.add(crosshairs);
        Button winclose = new Button(FontUtils.windowclose(), false, "winclose");
        winclose.addMouseListener(this);
        menu.add(winclose);
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
                super.mouseClicked(e);
            }
        });
        theme.addMouseListener(this);
        menu.add(theme);


        Button label = new Button(FontUtils.starhalf(), false, "label",false);

        label.addMouseListener(this);
        menu.add(label);


        menu.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        menu.setBorder(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //注册字体
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

                String[] fontNames = new String[]{"fontawesome-webfont.ttf", "PingFang Bold.ttf", "PingFang ExtraLight.ttf", "PingFang Heavy.ttf", "PingFang Light.ttf", "PingFang Medium.ttf",
                        "PingFang Regular.ttf"};
                for (String fontName : fontNames) {
                    Font fontawesome = Font.createFont(Font.PLAIN,
                            FlowEditorGui.class.getClassLoader().getResourceAsStream(fontName));
                    ge.registerFont(fontawesome);
                }
                FlowEditorGui xServiceGui = new FlowEditorGui();
                xServiceGui.setVisible(true);
                Rectangle rectangle = ge.getMaximumWindowBounds();
                xServiceGui.setBounds(rectangle.x + rectangle.width / 4, rectangle.y + rectangle.height / 4, rectangle.width / 2, rectangle.height / 2);
                xServiceGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

    }

    @Override
    public void mouseClicked(MouseEvent e) {
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
            case "user\n":
                gPanel.setModeOfNewShape(UserTask.class);
                break;
            case "equation":
                gPanel.resetScale();
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
                String title = myButton.getTitle();
                if (title.equals(FontUtils.sun())) {
                    ConstantUtils.getInstance().setColorModeLight();
                    repaint();
                } else if (title.equals(FontUtils.moon())) {
                    ConstantUtils.getInstance().setColorModeDark();
                    repaint();
                } else if (title.equals(FontUtils.paint())) {
                    ConstantUtils.getInstance().setColorfulMode();
                    repaint();
                }
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
    public void mousePressed(MouseEvent e) {

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
}
