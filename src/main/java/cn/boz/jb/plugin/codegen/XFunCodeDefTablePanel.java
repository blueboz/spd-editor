package cn.boz.jb.plugin.codegen;

import cn.boz.jb.plugin.idea.bean.XFunCodeDef;
import cn.boz.jb.plugin.idea.utils.DBUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class XFunCodeDefTablePanel extends JFrame {
    private JTextField typeField;
    private JTable table;
    private DefaultTableModel model;

    public XFunCodeDefTablePanel() {
        setLayout(new BorderLayout());

        // 创建输入框和查询按钮
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typeField = new JTextField(10);
        JButton searchButton = new JButton("查询");
        searchButton.addActionListener(e -> loadData());
        searchPanel.add(new JLabel("类型："));
        searchPanel.add(typeField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // 创建表格
        String[] colNames = {"ID", "父ID", "类型", "编码值", "有效标志", "名称", "简称", "显示顺序", "备注1", "备注2", "备注3", "备注4"};
        model = new DefaultTableModel(colNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 加载数据并渲染到JTable上
     */
    private void loadData() {
        String type = typeField.getText().trim();

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new XFunCodeDefTablePanel().setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
