package cn.boz.jb.plugin.codegen.dlg;

import cn.boz.jb.plugin.idea.bean.XFunCodeDef;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class XFunCodeDefDlg extends DialogWrapper {
    private JTextField typeField;
    private JTable table;
    private DefaultTableModel model;

    public XFunCodeDefDlg(Project project) {
        super(project);
        setTitle("备选代码");
        init();
        setSize(800,600);

    }

    /**
     * 加载数据并渲染到JTable上
     */
    private void loadData() {
        String type = typeField.getText().trim();
        model.setRowCount(0);

        Object[] r = {"l", "o", "a", "d",
                "i", "n", "g", ".",
                ".", ".", ".", ""};
        model.addRow(r);
        // 查询数据
        List<XFunCodeDef> list = queryData(type);

        // 更新表格数据
        model.setRowCount(0);
        for (XFunCodeDef data : list) {
            Object[] row = {data.getId(), data.getParentId(), data.getType(), data.getCodeVal(),
                    data.getIsVal(), data.getName(), data.getsName(), data.getDispSeq(),
                    data.getRmk1(), data.getRmk2(), data.getRmk3(), data.getRmk4()};
            model.addRow(row);
        }
    }

    /**
     * 从数据库中查询数据
     */
    private List<XFunCodeDef> queryData(String type) {
        // 连接数据库并执行查询操作，在这里省略具体代码，直接返回一个空列表
        try {
            List<XFunCodeDef> w07 = DBUtils.getInstance().queryXfundsCodeDef(null, type);
            return w07;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    JPanel jPanel=null;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());

            // 创建输入框和查询按钮
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            typeField = new JTextField(10);
            typeField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if(e.getKeyCode()==KeyEvent.VK_ENTER) {
                        loadData();
                    }

                }
            });
            JButton searchButton = new JButton("查询");
            searchButton.addActionListener(e -> loadData());
            searchPanel.add(new JLabel("类型："));
            searchPanel.add(typeField);
            searchPanel.add(searchButton);

            jPanel.add(searchPanel, BorderLayout.NORTH);

            // 创建表格
            String[] colNames = {"ID", "父ID", "类型", "编码值", "有效标志", "名称", "简称", "显示顺序", "备注1", "备注2", "备注3", "备注4"};
            model = new DefaultTableModel(colNames, 0);
            table = new JBTable(model);
            JScrollPane scrollPane = new JBScrollPane(table);
            jPanel.add(scrollPane, BorderLayout.CENTER);
            ActionManager instance = ActionManager.getInstance();

            ActionGroup actionGroup = (ActionGroup) instance.getAction("spd.openintoolwindow.action.group");
            ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);
            JComponent tableNavigator = spd_tb.getComponent();
            jPanel.add(tableNavigator,BorderLayout.SOUTH);
            jPanel.setPreferredSize(new Dimension(900, 800));
        }
        return jPanel;
    }

    public JBScrollPane derive() {
        return new JBScrollPane(this.jPanel);
    }
}
