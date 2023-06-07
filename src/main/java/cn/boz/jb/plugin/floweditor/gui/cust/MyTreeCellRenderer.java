package cn.boz.jb.plugin.floweditor.gui.cust;

import cn.boz.jb.plugin.floweditor.gui.MyFile;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class MyTreeCellRenderer implements TreeCellRenderer {

    public static ImageIcon scale(ImageIcon icon, int width, int height) {
        // 获取 Image 对象
        Image image = icon.getImage();

        // 进行缩放，使用 getScaledInstance() 方法
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // 将缩放后的 Image 对象转换回 ImageIcon 对象
        return new ImageIcon(scaledImage);
    }

    private Icon folderIcon = scale(new ImageIcon(getClass().getResource("/folder.png"))
            , 16, 16);
    private Icon fileIcon = scale(new ImageIcon(getClass().getResource("/file-fill.png")),16,16);
    private Color selectedTextColor = Color.WHITE;
    private Color selectedBackgroundColor = new Color(51, 153, 255); // 默认选中颜色

    private JPanel panel;
    private JLabel iconLabel;
    private JLabel textLabel;

    public MyTreeCellRenderer() {
        panel = new JPanel(new BorderLayout());
        iconLabel = new JLabel();
        textLabel = new JLabel();
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        // 设置文本和图标
        if (userObject instanceof MyFile) {
            MyFile myNode = (MyFile) userObject;
            textLabel.setText(myNode.getName());
//            ImageIcon icon = new ImageIcon(fo);
            iconLabel.setIcon(folderIcon);
        } else {
            textLabel.setText(value.toString());
            iconLabel.setIcon(fileIcon);
        }

        // 设置选中状态颜色
        if (selected) {
            panel.setBackground(selectedBackgroundColor);
            panel.setForeground(selectedTextColor);
            textLabel.setForeground(selectedTextColor);
        } else {
            panel.setBackground(tree.getBackground());
            panel.setForeground(tree.getForeground());
            textLabel.setForeground(tree.getForeground());

        }

        return panel;
    }
}
