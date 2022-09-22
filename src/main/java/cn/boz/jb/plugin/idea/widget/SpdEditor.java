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
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SpdEditor extends JComponent implements DataProvider, MouseListener, ClipboardOwner, FocusListener {

    ChartPanel chartPanel;
    JBSplitter jbSplitter;
    MyJBTable jbTable;
    private JBScrollPane jbPropertyScroll;

    private Project project;
    private JBScrollPane jbMenuScroll;
    private VirtualFile virtualFile;
    private JPanel menu;

    public SpdEditor(Project project, VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
        this.project=project;
        this.setLayout(new BorderLayout());
        jbSplitter = new JBSplitter(false);
        jbSplitter.setShowDividerControls(true);
        jbSplitter.setShowDividerIcon(true);
        jbSplitter.setSplitterProportionKey("spdEditorPropotion");
        add(jbSplitter, BorderLayout.CENTER);

        jbTable = new MyJBTable(project);

        chartPanel = new ChartPanel(project, virtualFile, this);
        chartPanel.registerShapeSelectedListener(jbTable);
        jbTable.bindChartPanel(chartPanel);

        jbPropertyScroll = new JBScrollPane();
        jbPropertyScroll.setViewportView(jbTable);

        jbSplitter.setFirstComponent(chartPanel);
        jbSplitter.setSecondComponent(jbPropertyScroll);

        jbMenuScroll = new JBScrollPane();
        jbMenuScroll.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_NEVER);
        JBScrollBar jbScrollBar = new JBScrollBar();

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

        chartPanel.addFocusListener(this);

        SpdSpeedSearch spdSpeedSearch = new SpdSpeedSearch(chartPanel);


    }


    private Button automation = null;
    private JLabel fileChangeHint = null;

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

        JSeparator jSeparator = new JSeparator();
        jSeparator.setOrientation(JSeparator.HORIZONTAL);
        menuPanel.add(jSeparator);
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

        if (SpdEditorDBState.getInstance(project).autoSave) {
            automation = new Button(IcoMoonUtils.getAutomation(), false, "automation", false);
            automation.addMouseListener(this);
            automation.setToolTipText("Disable AutoSave");
        } else {
            automation = new Button(IcoMoonUtils.getManual(), false, "automation", false);
            automation.addMouseListener(this);
            automation.setToolTipText("Enable AutoSave");
        }
        menuPanel.add(automation);

        Button save = new Button(IcoMoonUtils.getSave(), false, "save", false);
        save.addMouseListener(this);
        save.setToolTipText("Save");
        menuPanel.add(save);

        Button reload = new Button(IcoMoonUtils.getReload(), false, "reload", false);
        reload.addMouseListener(this);
        reload.setToolTipText("Reload");
        menuPanel.add(reload);

        fileChangeHint = new JLabel("File Not Change");
        menuPanel.add(fileChangeHint);


        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        menuPanel.setLayout(flowLayout);

        menuPanel.setBorder(null);
        chartPanel.addChangeListener((cp) -> {
            int currentHistoryHash = cp.getCurrentHistoryHash();
            int topHistoryHash = cp.getTopHistoryHash();
            if (topHistoryHash == currentHistoryHash) {
                this.fileChangeHint.setText("File Not Change");
            } else {
                this.fileChangeHint.setText("File Changed!");
            }
        });
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
                int result = Messages.showYesNoDialog("ERASE CAN NOT BE UNDO!", "WARNNING", Messages.getWarningIcon());
                if (result == Messages.OK) {
                    chartPanel.clear();
                    repaint();
                }
                break;
            case "reload":
                try {
                    chartPanel.loadFromInputStream(this.virtualFile.getInputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Messages.showErrorDialog("something wrong happen", ex.getMessage());
                }
                break;
            case "photo":
                chartPanel.export();
                break;
            case "save":
                chartPanel.fireSavedListener();
                break;
            case "automation":
                String automation = myButton.getTitle();
                if (automation.equals(IcoMoonUtils.getAutomation())) {
                    myButton.setTitle(IcoMoonUtils.getManual());
                    SpdEditorDBState.getInstance(project).autoSave = false;
                    myButton.setToolTipText("Enable AutoSave");

                    NotificationGroup.findRegisteredGroup("Spd Editor")
                            .createNotification("disable auto save spd editor", NotificationType.INFORMATION).notify(null);
                    repaint();
                } else if (automation.equals(IcoMoonUtils.getManual())) {
                    SpdEditorDBState.getInstance(project).autoSave = true;
                    myButton.setTitle(IcoMoonUtils.getAutomation());

                    myButton.setToolTipText("Disable AutoSave");
                    NotificationGroup.findRegisteredGroup("Spd Editor")
                            .createNotification("enable auto save spd editor", NotificationType.INFORMATION).notify(null);
                    repaint();
                }
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

                //FIXME 建议SQL优化抽离为好用的方法
                String joiningSql = sqls.stream().map(sql -> sql.replace("\n", "")).collect(Collectors.joining(";\n")) + ";";
                int idx = Messages.showDialog(joiningSql, "SQL", new String[]{"复制Sql", "更新至DB", "打开配置项","导出到目录", "确定"}, 3, SpdEditorIcons.FLOW_16_ICON);
                if (idx == 0) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(joiningSql);
                    clipboard.setContents(selection, this);
                } else if (idx == 1) {
                    //更新至db
                    SpdEditorDBState instance = SpdEditorDBState.getInstance(project);
                    try {
                        boolean b = DBUtils.executeSql(instance.jdbcUserName, instance.jdbcPassword, instance.jdbcUrl, instance.jdbcDriver, sqls);
                        if (b) {
                            Messages.showMessageDialog("更新成功!!", "更新成功", UIUtil.getInformationIcon());
                        }
                    } catch (SQLException sqlException) {
                        int selidx = Messages.showDialog("数据库连接发生错误，检查数据库配置?" + sqlException.getMessage(), "数据库错误", new String[]{"打开配置项", "取消"}, 0, UIUtil.getErrorIcon());
                        if (selidx == 0) {
                            ShowSettingsUtil.getInstance().showSettingsDialog(null, SpdEditorDBSettings.class);
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        Messages.showErrorDialog("发生错误", ee.getMessage());
                    }
                } else if (idx == 2) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManager.getInstance().getDefaultProject(), SpdEditorDBSettings.class);
                } else if (idx == 3) {
                    Project project = ProjectUtil.guessProjectForFile(virtualFile);
                    FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);

                    VirtualFile selectPath = FileChooser.chooseFile(fileChooserDescriptor, project, null);
                    //当前文件名称
                    String name = virtualFile.getName();
                    String nameSub = name.substring(0, name.lastIndexOf(".spd"));
//                    String path = selectPath.getPath();
                    //写入到文件
//                    selectPath.create
                    WriteCommandAction.runWriteCommandAction(ProjectManager.getInstance().getDefaultProject(), () -> {

                        try{
                            byte[] bytes = joiningSql.getBytes(StandardCharsets.UTF_8);
                            VirtualFile vfile = selectPath.createChildData(null, nameSub + ".sql");
                            vfile.setCharset(StandardCharsets.UTF_8);
                            vfile.setBOM(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf});
                            vfile.setBinaryContent(bytes);
                        }catch (Exception ee){
                        }
                    });


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

    @Override
    public void focusGained(FocusEvent e) {
        if (SpdEditorDBState.getInstance(project).autoSave) {
            if (!automation.getTitle().equals(IcoMoonUtils.getAutomation())) {
                automation.setTitle(IcoMoonUtils.getAutomation());
                automation.setToolTipText("Disable AutoSave");
            }
        } else {
            if (!automation.getTitle().equals(IcoMoonUtils.getManual())) {
                automation.setTitle(IcoMoonUtils.getManual());
                automation.setToolTipText("Enable AutoSave");
            }
        }

    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    /**
     * 重新从文件中进行加载
     */
    public void load() {
        byte[] bytes = new byte[0];
        try {
            this.chartPanel.loadFromInputStream(virtualFile.getInputStream());
        } catch (IOException e) {
            Messages.showErrorDialog("Something wrong happen", e.getMessage());
            e.printStackTrace();
        }
    }

    public static final DataKey<SpdEditor> SPD_EDITOR = DataKey.create("SPD_EDITOR");

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (SPD_EDITOR.is(dataId)) {
            return this;
        }
        return null;
    }
}
