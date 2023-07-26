package cn.boz.jb.plugin;

import cn.boz.jb.plugin.bean.CodeGenrator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SwingForm extends JFrame {
    private JSONObject data;
    // 在这里添加其他需要的组件
    private JTextField packageNameTextField;
    private JTextField classNameTextField;
    private JTextField beanNameTextField;
    private JTextField namespaceTextField;
    private JTextField tableNameTextField;
    private JTextField menuIDTextField;
    private JTextField pmenuIDTextField;
    private JTextField powerBitsTextField;
    private JTextField moduleNameTextField;
    private JTextField functionBaseTextField;
    private JTextField titleTextField;
    private JTextField exceptionFullNameTextField;
    private JTextField exceptionNameTextField;
    private JTextField queryNamespaceTextField;
    private JCheckBox importExcelCheckBox;


    public SwingForm(JSONObject data) {

        try {
            String predefined = new String(CodeGenrator.class.getResourceAsStream("predefinedform.json").readAllBytes());
            JSONObject configPredefined = JSON.parseObject(predefined);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.data = data;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Swing Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2));

        // 根据CodeGenerator中的columns字段添加表单元素
        // 添加packageName属性文本框和标签
        JLabel packageNameLabel = new JLabel("package:");
        panel.add(packageNameLabel);
        packageNameTextField = new JTextField(data.getString("package"));
        panel.add(packageNameTextField);

        // 添加className属性文本框和标签
        JLabel classNameLabel = new JLabel("className:");
        panel.add(classNameLabel);
        classNameTextField = new JTextField(data.getString("className"));
        panel.add(classNameTextField);

        // 添加beanName属性文本框和标签
        JLabel beanNameLabel = new JLabel("beanName:");
        panel.add(beanNameLabel);
        beanNameTextField = new JTextField(data.getString("beanName"));
        panel.add(beanNameTextField);

        // 添加namespace属性文本框和标签
        JLabel namespaceLabel = new JLabel("namespace:");
        panel.add(namespaceLabel);
        namespaceTextField = new JTextField(data.getString("namespace"));
        panel.add(namespaceTextField);

        // 添加tableName属性文本框和标签
        JLabel tableNameLabel = new JLabel("tableName:");
        panel.add(tableNameLabel);
        tableNameTextField = new JTextField(data.getString("tableName"));
        panel.add(tableNameTextField);

        // 添加menuID属性文本框和标签
        JLabel menuIDLabel = new JLabel("menuID:");
        panel.add(menuIDLabel);
        menuIDTextField = new JTextField(String.valueOf(data.getString("menuID")));
        panel.add(menuIDTextField);

        // 添加pmenuID属性文本框和标签
        JLabel pmenuIDLabel = new JLabel("pmenuID:");
        panel.add(pmenuIDLabel);
        pmenuIDTextField = new JTextField(String.valueOf(data.getString("pmenuID")));
        panel.add(pmenuIDTextField);

        // 添加powerBits属性文本框和标签
        JLabel powerBitsLabel = new JLabel("powerBits:");
        panel.add(powerBitsLabel);
        powerBitsTextField = new JTextField(data.getString("powerbits"));
        panel.add(powerBitsTextField);

        // 添加moduleName属性文本框和标签
        JLabel moduleNameLabel = new JLabel("moduleName:");
        panel.add(moduleNameLabel);
        moduleNameTextField = new JTextField(data.getString("moduleName"));
        panel.add(moduleNameTextField);

        // 添加functionBase属性文本框和标签
        JLabel functionBaseLabel = new JLabel("functionBase:");
        panel.add(functionBaseLabel);
        functionBaseTextField = new JTextField(data.getString("functionBase"));
        panel.add(functionBaseTextField);

        // 添加title属性文本框和标签
        JLabel titleLabel = new JLabel("title:");
        panel.add(titleLabel);
        titleTextField = new JTextField(data.getString("title"));
        panel.add(titleTextField);

        // 添加exceptionFullName属性文本框和标签
        JLabel exceptionFullNameLabel = new JLabel("exceptionFullName:");
        panel.add(exceptionFullNameLabel);
        exceptionFullNameTextField = new JTextField(data.getString("exceptionFullName"));
        panel.add(exceptionFullNameTextField);

        // 添加exceptionName属性文本框和标签
        JLabel exceptionNameLabel = new JLabel("exceptionName:");
        panel.add(exceptionNameLabel);
        exceptionNameTextField = new JTextField(data.getString("exceptionName"));
        panel.add(exceptionNameTextField);

        // 添加queryNamespace属性文本框和标签
        JLabel queryNamespaceLabel = new JLabel("queryNamespace:");
        panel.add(queryNamespaceLabel);
        queryNamespaceTextField = new JTextField(data.getString("queryNamespace"));
        panel.add(queryNamespaceTextField);

        // 添加importExcel属性复选框
        JLabel importExcelLabel = new JLabel("importExcel:");
        panel.add(importExcelLabel);
        importExcelCheckBox = new JCheckBox();
        importExcelCheckBox.setSelected(data.getBoolean("importExcel"));
        panel.add(importExcelCheckBox);
        JButton saveButton = new JButton("Save"); // 保存按钮
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 在按钮点击事件中获取文本框和复选框的值并更新CodeGenerator对象的属性

                // 保存成功提示
                JOptionPane.showMessageDialog(SwingForm.this, "保存成功！");
            }
        });
        panel.add(saveButton);
        // 在这里添加其他表单元素，例如按钮等

        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null); // 居中显示窗体
    }

    public static void main(String[] args) throws IOException {
        // 设置相关属性和数据...
        SwingUtilities.invokeLater(() -> {
            try {
                JSONObject jsonObject = JSON.parseObject(new String(Files.readAllBytes(Path.of("/home/@chenweidian-yfzx/Code/spd-editor/src/main/resources/config.json"))));
                new SwingForm(jsonObject).setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
