package cn.boz.jb.plugin.floweditor.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.boz.jb.plugin.floweditor.gui.cust.MyTreeCellRenderer;
public class MyJFrame extends JFrame {
    public MyJFrame() {
        // 创建根节点
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        
        // 创建 Demo 数据
        DefaultMutableTreeNode folder1 = new DefaultMutableTreeNode("Folder 1");
        DefaultMutableTreeNode file1 = new DefaultMutableTreeNode(new MyFile("File 1"));
        DefaultMutableTreeNode file2 = new DefaultMutableTreeNode(new MyFile("File 2"));
        folder1.add(file1);
        folder1.add(file2);
        
        DefaultMutableTreeNode folder2 = new DefaultMutableTreeNode("Folder 2");
        DefaultMutableTreeNode file3 = new DefaultMutableTreeNode(new MyFile("File 3"));
        DefaultMutableTreeNode file4 = new DefaultMutableTreeNode(new MyFile("File 4"));
        folder2.add(file3);
        folder2.add(file4);
        
        root.add(folder1);
        root.add(folder2);
        
        // 创建 JTree 并设置渲染器
        JTree tree = new JTree(root);
        MyTreeCellRenderer renderer = new MyTreeCellRenderer();
        tree.setCellRenderer(renderer);
        
        // 将 JTree 放入 JScrollPane 中，并添加到 JFrame 的 content pane 中
        JScrollPane scrollPane = new JScrollPane(tree);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        setTitle("My JFrame with JTree");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new MyJFrame();
    }
}
