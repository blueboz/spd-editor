package cn.boz.jb.plugin.floweditor.gui;

import java.awt.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
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

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        // 判断节点类型并设置图标
        if (userObject instanceof MyFile) {
            setIcon(fileIcon);
        } else {
            setIcon(folderIcon);
        }

        // 如果选中了该节点，则改变字体和背景色
        if (selected) {
            setForeground(selectedTextColor);
            setBackground(selectedBackgroundColor);
        } else {
            setForeground(getTextSelectionColor());
            setBackground(getBackgroundSelectionColor());
        }

        return this;
    }

    public void setSelectedTextColor(Color color) {
        this.selectedTextColor = color;
    }

    public void setSelectedBackgroundColor(Color color) {
        this.selectedBackgroundColor = color;
    }
}

