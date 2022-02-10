package cn.boz.test;

import com.intellij.ui.table.JBTable;
import com.intellij.ui.table.TableView;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Container;

public class JTableHoverDemo {

    public static void main(String[] agrs) throws Exception {
        JFrame frame = new JFrame("学生成绩表");
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();


        frame.setVisible(true);

        JBTable jbTable = new JBTable();

    }

    private JComponent createComponent() throws Exception {
        JPanel jPanel = new JPanel();
        JTable jTable = new JTable();

        jTable.setRowHeight(30);
        jPanel.setLayout(new BorderLayout());
        jPanel.add(jTable, BorderLayout.CENTER);
        return jPanel;
    }
}