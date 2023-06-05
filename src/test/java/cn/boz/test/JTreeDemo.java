package cn.boz.test;

import cn.boz.jb.plugin.idea.utils.DBUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class JTreeDemo {
    public static void main(String[] agrs) throws Exception {
        JFrame frame = new JFrame("教师学历信息");
        frame.setSize(330, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JTreeDemo().createComponent());
        frame.pack();
        frame.setVisible(true);
    }

    private JScrollPane createComponent() throws Exception {
        JScrollPane panel = new JScrollPane();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("教师学历信息");

        DBUtils instance = DBUtils.getInstance();
        List<Map<String, Object>> apps = instance.queryEcasAppl(null);
        for (Map<String, Object> app : apps) {
            NodeData nodeData = new NodeData("ECASAPPL", app);
            nodeData.setSubDataLoaded(false);
            var node = new DefaultMutableTreeNode(nodeData, true);
            root.add(node);
        }

        JTree tree = new JTree(root);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                Object pathComponent = path.getLastPathComponent();
                if (pathComponent instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
                    Object userObject = node.getUserObject();
                    if(userObject instanceof NodeData){
                        NodeData uo= (NodeData) userObject;
                        boolean subDataLoaded = uo.isSubDataLoaded();
                        if(subDataLoaded==false){
                            uo.setSubDataLoaded(true);
                            String nodeType = uo.getNodeType();
                            Map nodeData = (Map) uo.getNodeData();
                            if(nodeType.equals("ECASAPPL")){
                                try {
                                    List<Map<String, Object>> topMenus= instance.queryTopMenuOfApp(null, (BigDecimal) nodeData.get("APPLID"));
                                    for (Map<String, Object> topMenu: topMenus) {
                                        NodeData menu = new NodeData("MENU", topMenu);
                                        node.add(new DefaultMutableTreeNode(menu,true));
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }else if(nodeType.equals("MENU")){
                                List<Map<String, Object>> menus= null;
                                try {
                                    System.out.println("submenu");
                                    menus = instance.queryMenuOfAppMenu(null, (BigDecimal)nodeData.get("MENUID"),(BigDecimal) nodeData.get("APPLID"));
                                    for (Map<String, Object> menudata: menus) {
                                        NodeData menu = new NodeData("MENU", menudata);
                                        node.add(new DefaultMutableTreeNode(menu,true));
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }

                            }
                        }
                    }

                }

            }
        });
        TreeCellRenderer cellRenderer = tree.getCellRenderer();
        tree.setCellRenderer(new TreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                //默认都认为不是leaf节点，并且在userData里面设置信息

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                var uo = node.getUserObject();
                if (uo instanceof String) {
                    return new JLabel((String) uo);
                } else {
                    NodeData data = (NodeData) uo;
                    String nodeType = data.getNodeType();
                    if(nodeType.equals("ECASAPPL")){
                        Map nodeData = (Map) data.getNodeData();
                        return new JLabel((String) nodeData.get("CODE"));
                    }else {
                        Map nodeData = (Map) data.getNodeData();
                        return new JLabel((String) nodeData.get("NAME"));
                    }
                }
            }
        });

        panel.setViewportView(tree);
        return panel;
    }
}