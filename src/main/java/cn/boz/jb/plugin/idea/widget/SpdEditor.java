package cn.boz.jb.plugin.idea.widget;

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
import cn.boz.jb.plugin.floweditor.gui.widget.Button;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class SpdEditor extends JComponent implements MouseListener {

    ChartPanel chartPanel;
    JBSplitter jbSplitter;
    MyJBTable jbTable;
    JBPanel<JBPanel> jbPanelJBPanel;
    DefaultTableModel defaultTableModel;

    private JBScrollPane jbPropertyScroll;

    private JBScrollPane jbMenuScroll;
    private JPanel menu;
    private JPanel menuContainer;


    public SpdEditor() {
        this.setLayout(new BorderLayout());
        jbSplitter = new JBSplitter(false, 0.8f);
        jbSplitter.setShowDividerControls(true);
        jbSplitter.setShowDividerIcon(true);
        add(jbSplitter, BorderLayout.CENTER);

        jbTable = new MyJBTable();

        chartPanel = new ChartPanel();
        chartPanel.registerShapeSelectedListener(jbTable);

        jbPropertyScroll = new JBScrollPane();
        jbPropertyScroll.setViewportView(jbTable);

        jbSplitter.setFirstComponent(chartPanel);
        jbSplitter.setSecondComponent(jbPropertyScroll);

        jbMenuScroll = new JBScrollPane();
        jbMenuScroll.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_NEVER);
        JBScrollBar jbScrollBar = new JBScrollBar();
//        jbScrollBar.setPreferredSize(new Dimension(0,4));

        jbMenuScroll.setHorizontalScrollBar(jbScrollBar);
        jbMenuScroll.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar horizontalScrollBar = jbMenuScroll.getHorizontalScrollBar();
                int value = horizontalScrollBar.getValue();
                int maximum = horizontalScrollBar.getMaximum();
                int minimum = horizontalScrollBar.getMinimum();

                final AtomicInteger starti = new AtomicInteger(0);
                final Timer timer = new Timer(1000 / 60, null);
                //经过调教，此规律是最顺滑的
                final long time = 15;
                final long pace = 60;
                if (e.getWheelRotation() == 1) {
                    //up
                    value += e.getScrollAmount() * 3;
                    if (value >= maximum) {
                        value = maximum;
                    }
                } else {
                    //down
                    value -= e.getScrollAmount() * 3;
                    if (value <= minimum) {
                        value = minimum;
                    }
                }
                horizontalScrollBar.setValue(value);
            }
        });

        menu = new JPanel();
        menu.setBackground(ConstantUtils.getInstance().getBtnBarColor());
        processMenu(menu);

        jbMenuScroll.setViewportView(menu);
        add(jbMenuScroll, BorderLayout.NORTH);

//        DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("spdgroup");
//        ActionToolbar spdToolbar = ActionManager.getInstance().createActionToolbar("SpdEditor", group, true);
//        menuContainer = new JPanel();
//        menuContainer.setPreferredSize(new Dimension(0,30));
//        spdToolbar.setTargetComponent(menuContainer);
//        menuContainer.add(spdToolbar.getComponent());
//        add(menuContainer,BorderLayout.NORTH);

    }

    private void processMenu(JPanel menuPanel) {
        Button flowbtn = new Button(IcoMoonUtils.getSequenceFlow(), true, "flowbtn", "oper", IcoMoonUtils.getFont());
        flowbtn.addMouseListener(this);
        menuPanel.add(flowbtn);
        Button user = new Button(IcoMoonUtils.getUserTask(), true, "user", "oper", IcoMoonUtils.getFont());
        user.addMouseListener(this);
        menuPanel.add(user);
        Button service = new Button(IcoMoonUtils.getServiceTask(), true, "service", "oper", IcoMoonUtils.getFont());
        service.addMouseListener(this);
        menuPanel.add(service);
        Button call = new Button(IcoMoonUtils.getCallActivity(), true, "call", "oper", IcoMoonUtils.getFont());
        call.addMouseListener(this);
        menuPanel.add(call);

        Button exclude = new Button(IcoMoonUtils.getExclusiveGateway(), true, "exclude", "oper", IcoMoonUtils.getFont());
        exclude.addMouseListener(this);
        menuPanel.add(exclude);

        Button parallel = new Button(IcoMoonUtils.getParallelGateway(), true, "parallel", "oper", IcoMoonUtils.getFont());
        parallel.addMouseListener(this);
        menuPanel.add(parallel);

        Button foreach = new Button(IcoMoonUtils.getForeachGateway(), true, "foreach", "oper", IcoMoonUtils.getFont());
        foreach.addMouseListener(this);
        menuPanel.add(foreach);

        Button start = new Button(IcoMoonUtils.getStartEvent(), true, "start", "oper", IcoMoonUtils.getFont());
        start.addMouseListener(this);
        menuPanel.add(start);


        Button end = new Button(IcoMoonUtils.getEndEvent(), true, "stop", "oper", IcoMoonUtils.getFont());
        end.addMouseListener(this);
        menuPanel.add(end);


        Button movebtn = new Button(FontUtils.arrows(), true, "movebtn", "oper");
        movebtn.addMouseListener(this);
        movebtn.setToggle(true);
        menuPanel.add(movebtn);
        Button handbtn = new Button(FontUtils.hand(), true, "handbtn", "oper");
        handbtn.addMouseListener(this);
        menuPanel.add(handbtn);
        Button crosshairs = new Button(FontUtils.crosshairs(), false, "crosshairs");
        crosshairs.addMouseListener(this);
        menuPanel.add(crosshairs);
//        Button winclose = new Button(FontUtils.windowclose(), false, "winclose");
//        winclose.addMouseListener(this);
//        menu.add(winclose);
        Button equation = new Button(FontUtils.cube(), false, "equation");
        equation.addMouseListener(this);
        menuPanel.add(equation);
        Button undo = new Button(FontUtils.rotateLeft(), false, "undo");
        undo.addMouseListener(this);
        menuPanel.add(undo);
        Button redo = new Button(FontUtils.rotateRight(), false, "redo");
        redo.addMouseListener(this);
        menuPanel.add(redo);

        Button erase = new Button(FontUtils.erase(), false, "erase");
        erase.addMouseListener(this);
        menuPanel.add(erase);

        Button load = new Button(FontUtils.load(), false, "load");
        load.addMouseListener(this);
        menuPanel.add(load);

        Button photo = new Button(FontUtils.photo(), false, "photo");
        photo.addMouseListener(this);
        menuPanel.add(photo);

        Button save = new Button(FontUtils.save(), false, "save");
        save.addMouseListener(this);
        menuPanel.add(save);

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
                    menuPanel.setBackground(ConstantUtils.getInstance().getBtnBarColor());
                    repaint();
                } else if (title.equals(FontUtils.moon())) {
                    ConstantUtils.getInstance().setColorModeDark();
                    menuPanel.setBackground(ConstantUtils.getInstance().getBtnBarColor());
                    repaint();
                } else if (title.equals(FontUtils.paint())) {
                    ConstantUtils.getInstance().setColorfulMode();
                    menuPanel.setBackground(ConstantUtils.getInstance().getBtnBarColor());
                    repaint();
                }
                super.mouseClicked(e);
            }
        });
        theme.addMouseListener(this);
        menuPanel.add(theme);

        Button trash = new Button(FontUtils.trash(), false, "trash");
        trash.addMouseListener(this);
        menuPanel.add(trash);

        Button label = new Button(FontUtils.starhalf(), false, "label", false);

        label.addMouseListener(this);
        menuPanel.add(label);


        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        menuPanel.setLayout(flowLayout);
//        menuPanel.setMaximumSize(new Dimension(0,40));
//        menuPanel.setPreferredSize(new Dimension(0,50));
        menuPanel.setBorder(null);
    }


    public void loadFromFile(File file) {
        chartPanel.loadFromFile(file);
    }

    public boolean isModified() {
        return chartPanel.isModified();
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        Button myButton = (Button) e.getSource();
        switch (myButton.getId()) {
            case "linebtn":
                chartPanel.setModeOfNewLine(Line.class);
                break;
            case "flowbtn":
                chartPanel.setModeOfNewLine(SequenceFlow.class);
                break;
            case "squarebtn":
                chartPanel.setModeOfNewShape(Shape.class);
                break;
            case "movebtn":

                chartPanel.setMode(ChartPanel.MODE_DEFAULT);
                break;
            case "user":
                chartPanel.setModeOfNewShape(UserTask.class);
                break;
            case "handbtn":
                chartPanel.setMode(ChartPanel.MODE_MOVE);
                break;
            case "crosshairs":
                chartPanel.resetOriginalPoint();
                break;
            case "circle":
                chartPanel.setModeOfNewShape(Circle.class);
                break;
            case "winclose":
                chartPanel.setModeOfNewShape(Prismatic.class);
                break;
            case "start":
                chartPanel.setModeOfNewShape(StartEvent.class);
                break;
            case "stop":
                chartPanel.setModeOfNewShape(EndEvent.class);
                break;
            case "foreach":
                chartPanel.setModeOfNewShape(ForeachGateway.class);
                break;
            case "parallel":
                chartPanel.setModeOfNewShape(ParallelGateway.class);
                break;
            case "exclude":
                chartPanel.setModeOfNewShape(ExclusiveGateway.class);
                break;
            case "call":
                chartPanel.setModeOfNewShape(CallActivity.class);
                break;
            case "service":
                chartPanel.setModeOfNewShape(ServiceTask.class);
                break;
            case "equation":
                chartPanel.resetScale();
                break;
            case "trash":
                chartPanel.shapeDelete();
                repaint();
                break;
            case "redo":
                chartPanel.redo();
                break;
            case "undo":
                chartPanel.undo();
                break;
            case "load":
                chartPanel.load();
                repaint();
                break;
            case "erase":
                chartPanel.clear();
                repaint();
                break;
            case "photo":
                chartPanel.export();
                break;
            case "save":
//                chartPanel.save();
                chartPanel.fireSavedListener();
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
