package cn.boz.jb.plugin.floweditor.gui;

import cn.boz.jb.plugin.floweditor.gui.widget.FlowEditorComponent;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class FlowEditorGui extends JFrame implements MouseListener {

    private FlowEditorComponent flowEditorComponent;


    public FlowEditorGui() {

        this.setTitle("流程图工具v1.0");
        BufferedImage read = null;
        try {
            read = ImageIO.read(FlowEditorGui.class.getClassLoader().getResourceAsStream("applogo.png"));
            this.setIconImage(read);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setLayout(new BorderLayout());
        flowEditorComponent = new FlowEditorComponent();
        this.add(flowEditorComponent, BorderLayout.CENTER);

    }

    /**
     * 处理菜单的相关细节
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //注册字体
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                FlowEditorGui xServiceGui = new FlowEditorGui();
                xServiceGui.setVisible(true);
                Rectangle rectangle = ge.getMaximumWindowBounds();
                xServiceGui.setBounds((int) rectangle.getWidth() / 2, 0, rectangle.width / 2, rectangle.height);
                xServiceGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}
