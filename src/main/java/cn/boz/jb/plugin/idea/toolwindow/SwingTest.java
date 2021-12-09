package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.dialog.MyLayoutManager;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Map;

public class SwingTest extends JComponent {
    private JLabel applid;
    private JTextField applidt;
    private JLabel menuid;
    private JTextField menuidt;
    private JLabel name;
    private JTextField namet;
    private JLabel lvl;
    private JTextField lvlt;
    private JLabel url;
    private JTextField urlt;
    private JLabel parent;
    private JTextField parentt;
    private JLabel img;
    private JTextField imgt;
    private JLabel ischild;
    private JTextField ischildt;
    private JLabel groupid;
    private JTextField groupidt;
    private Map<String, Object> parentMap;
    private JPanel jPanel;

    private LayoutManager myLayoutManager;
    JComponent getComponent(){
        jPanel = new JPanel();
        myLayoutManager = new MyLayoutManager();
        jPanel.setLayout(myLayoutManager);
        applid = new JLabel("APPLID");
        applidt = new JTextField();
        menuid = new JLabel("MENUID");
        menuidt = new JTextField();
        name = new JLabel("NAME");
        namet = new JTextField();
        lvl = new JLabel("LVL");
        lvlt = new JTextField();
        url = new JLabel("URL");
        urlt = new JTextField();
        parent = new JLabel("PARENT");
        parentt = new JTextField();
        img = new JLabel("IMG");
        imgt = new JTextField();
        ischild = new JLabel("ISCHILD");
        ischildt = new JTextField();
        groupid = new JLabel("GROUPID");
        groupidt = new JTextField();
        jPanel.add(applid);
        jPanel.add(applidt);
        jPanel.add(menuid);
        jPanel.add(menuidt);
        jPanel.add(name);
        jPanel.add(namet);
        jPanel.add(lvl);
        jPanel.add(lvlt);
        jPanel.add(url);
        jPanel.add(urlt);
        jPanel.add(parent);
        jPanel.add(parentt);
        jPanel.add(img);
        jPanel.add(imgt);
        jPanel.add(ischild);
        jPanel.add(ischildt);
        jPanel.add(groupid);
        jPanel.add(groupidt);
        jPanel.setPreferredSize(new Dimension(400, 800));
        return jPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame jFrame = new JFrame();
            SwingTest swingTest = new SwingTest();
            jFrame.add(swingTest.getComponent());
            jFrame.pack();
            jFrame.setVisible(true);
        });

    }
}
