package cn.boz.test;

import com.intellij.ui.table.JBTable;
import com.intellij.ui.table.TableView;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Container;

public class JTableHoverDemo {

    public static void main(String[] agrs) throws Exception {
        JFrame frame = new JFrame("学生成绩表");
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();

        JLayeredPane layeredPane1 = frame.getLayeredPane();


//        JLayeredPane layeredPane = new JLayeredPane();

        JLayeredPane layeredPane = frame.getLayeredPane();
        JComponent component = createComponent();
        contentPane.add(component,BorderLayout.CENTER);
        JLabel jLabel = new JLabel();
        jLabel.setText("gg love mm");;
        jLabel.setBounds(0,1,120,30);
        layeredPane.add(jLabel,JLayeredPane.POPUP_LAYER);
//        frame.setContentPane(layeredPane);

        frame.setVisible(true);




    }


    private static JComponent createComponent() throws Exception {
        JPanel jPanel = new JPanel();
        JTable jTable = new JTable(){

        };

        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        model.setColumnIdentifiers(new Object[]{"A","B","C"});
        model.addRow(new Object[]{"1","2","3"});

        TableColumn a = jTable.getColumn("A");
        a.setPreferredWidth(80);
        a.setMaxWidth(120);


        jTable.setRowHeight(30);
        jPanel.setLayout(new BorderLayout());
        jPanel.add(jTable.getTableHeader(), BorderLayout.NORTH);
        jPanel.add(jTable, BorderLayout.CENTER);
        return jPanel;
    }
}