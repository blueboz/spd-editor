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
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.Button;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.configurable.SpdEditorSettings;
import cn.boz.jb.plugin.idea.configurable.SpdEditorSettingsComp;
import cn.boz.jb.plugin.idea.configurable.SpdEditorState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import icons.SpdEditorIcons;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SpdEditor extends JComponent implements MouseListener, ClipboardOwner {

    ChartPanel chartPanel;
    JBSplitter jbSplitter;
    MyJBTable jbTable;
    private JBScrollPane jbPropertyScroll;

    private JBScrollPane jbMenuScroll;
    private JPanel menu;

    public SpdEditor() {
        this.setLayout(new BorderLayout());
        jbSplitter = new JBSplitter(false, 0.8f);
        jbSplitter.setShowDividerControls(true);
        jbSplitter.setShowDividerIcon(true);
        add(jbSplitter, BorderLayout.CENTER);

        jbTable = new MyJBTable();

        chartPanel = new ChartPanel();
        chartPanel.registerShapeSelectedListener(jbTable);
        jbTable.bindChartPanel(chartPanel);

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
        Button flowbtn = new Button(IcoMoonUtils.getSequenceFlow(), true, "flowbtn", "oper");
        flowbtn.addMouseListener(this);
        flowbtn.setToolTipText("Flow Line");
        menuPanel.add(flowbtn);

        Button user = new Button(IcoMoonUtils.getUserTask(), true, "user", "oper");
        user.setToolTipText("User Task");
        user.addMouseListener(this);
        menuPanel.add(user);

        Button service = new Button(IcoMoonUtils.getServiceTask(), true, "service", "oper");
        service.addMouseListener(this);
        service.setToolTipText("Service Task");
        menuPanel.add(service);

        Button call = new Button(IcoMoonUtils.getCallActivity(), true, "call", "oper");
        call.addMouseListener(this);
        call.setToolTipText("Call Activity");
        menuPanel.add(call);

        Button exclude = new Button(IcoMoonUtils.getExclusiveGateway(), true, "exclude", "oper");
        exclude.addMouseListener(this);
        exclude.setToolTipText("Exclusive Gateway");
        menuPanel.add(exclude);

        Button parallel = new Button(IcoMoonUtils.getParallelGateway(), true, "parallel", "oper");
        parallel.addMouseListener(this);
        parallel.setToolTipText("Parallel Gateway");
        menuPanel.add(parallel);

        Button foreach = new Button(IcoMoonUtils.getForeachGateway(), true, "foreach", "oper");
        foreach.addMouseListener(this);
        foreach.setToolTipText("Foreach Gateway");
        menuPanel.add(foreach);

        Button start = new Button(IcoMoonUtils.getStartEvent(), true, "start", "oper");
        start.addMouseListener(this);
        start.setToolTipText("Start Event");

        menuPanel.add(start);


        Button end = new Button(IcoMoonUtils.getEndEvent(), true, "stop", "oper");
        end.addMouseListener(this);
        end.setToolTipText("End Event");
        menuPanel.add(end);

        Button movebtn = new Button(IcoMoonUtils.getMove(), true, "movebtn", "oper");
        movebtn.addMouseListener(this);
        movebtn.setToolTipText("Move");
        movebtn.setToggle(true);
        menuPanel.add(movebtn);

        Button handbtn = new Button(IcoMoonUtils.getScale(), true, "handbtn", "oper");
        handbtn.addMouseListener(this);
        handbtn.setToolTipText("Scale");
        menuPanel.add(handbtn);

        Button crosshairs = new Button(IcoMoonUtils.getAlign(), false, "crosshairs");
        crosshairs.addMouseListener(this);
        crosshairs.setToolTipText("Reset");
        menuPanel.add(crosshairs);


        Button equation = new Button(IcoMoonUtils.getEquation(), false, "equation");
        equation.addMouseListener(this);
        equation.setToolTipText("1:1");
        menuPanel.add(equation);

        Button undo = new Button(IcoMoonUtils.getUndo(), false, "undo");
        undo.addMouseListener(this);
        undo.setToolTipText("Undo");
        menuPanel.add(undo);

        Button redo = new Button(IcoMoonUtils.getRedo(), false, "redo");
        redo.addMouseListener(this);
        redo.setToolTipText("Redo");
        menuPanel.add(redo);

        Button erase = new Button(IcoMoonUtils.getErase(), false, "erase");
        erase.addMouseListener(this);
        erase.setToolTipText("Erase");
        menuPanel.add(erase);


        Button photo = new Button(IcoMoonUtils.getImage(), false, "photo");
        photo.addMouseListener(this);
        photo.setToolTipText("Export");
        menuPanel.add(photo);


        Button trash = new Button(IcoMoonUtils.getTrash(), false, "trash");
        trash.addMouseListener(this);
        trash.setToolTipText("Trash");
        menuPanel.add(trash);

        Button label = new Button(IcoMoonUtils.getHText(), false, "label", false);
        label.addMouseListener(this);
        label.setToolTipText("Label Show Switcher");
        menuPanel.add(label);

        Button sql = new Button(IcoMoonUtils.getSQL(), false, "sql", false);
        sql.addMouseListener(this);
        sql.setToolTipText("Sql");
        menuPanel.add(sql);

        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        menuPanel.setLayout(flowLayout);

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
            case "erase":
                chartPanel.clear();
                repaint();
                break;
            case "photo":
                chartPanel.export();
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

            case "sql":
                List<String> sqls = chartPanel.generateSql();
                if (sqls == null) {
                    return;
                }

                String joiningSql = sqls.stream().collect(Collectors.joining(";\n")) + ";";
                int idx = Messages.showDialog(joiningSql, "SQL", new String[]{"复制Sql", "更新至DB", "打开配置项", "确定"}, 3, SpdEditorIcons.FLOW_16_ICON);
                if (idx == 0) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(joiningSql);
                    clipboard.setContents(selection, this);
                } else if (idx == 1) {
                    //更新至db
                    SpdEditorState instance = SpdEditorState.getInstance();
                    try {
                        boolean b = DBUtils.executeSql(instance.jdbcUserName, instance.jdbcPassword, instance.jdbcUrl, instance.jdbcDriver, sqls);
                        if (b) {
                            Messages.showMessageDialog("更新成功!!", "更新成功", UIUtil.getInformationIcon());
                        }
                    } catch (SQLException sqlException) {
                        int selidx = Messages.showDialog("数据库连接发生错误，检查数据库配置?" + sqlException.getMessage(), "数据库错误", new String[]{"打开配置项", "取消"}, 0, UIUtil.getErrorIcon());
                        if (selidx == 0) {
                            ShowSettingsUtil.getInstance().showSettingsDialog(null, SpdEditorSettings.class);
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        Messages.showErrorDialog("发生错误", ee.getMessage());
                    }
                } else if (idx == 2) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(null, SpdEditorSettings.class);
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

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
