package cn.boz.jb.plugin.floweditor.gui;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextAreaProperty;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.widget.MyTable;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Field;

public class TestMyTable extends JFrame {

    public TestMyTable() {
        MyTable myTable = new MyTable();
        this.setLayout(new BorderLayout());
        this.add(myTable, BorderLayout.CENTER);
        UserTask userTask = new UserTask();
        Field[] declaredFields = userTask.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            myTable.addProperty(new TextAreaProperty(declaredField.getName(),userTask));
        }
        myTable.resetRowHeight();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //注册字体
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                TestMyTable testm = new TestMyTable();
                testm.setVisible(true);
                Rectangle rectangle = ge.getMaximumWindowBounds();
                testm.setBounds((int) rectangle.getWidth() / 2, 0, rectangle.width / 2, rectangle.height);
                testm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
