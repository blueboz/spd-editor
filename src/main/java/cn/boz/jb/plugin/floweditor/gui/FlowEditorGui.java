package cn.boz.jb.plugin.floweditor.gui;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.EndEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.StartEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Circle;
import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.Prismatic;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.utils.ConstantUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.Button;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
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

    FlowEditorComponent flowEditorComponent;
    public FlowEditorGui() {

        this.setTitle("流程图工具v1.0");
        BufferedImage read = null;
        try {
            read = ImageIO.read(FlowEditorGui.class.getClassLoader().getResourceAsStream("flow2.png"));
            this.setIconImage(read);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setLayout(new BorderLayout());
        flowEditorComponent = new FlowEditorComponent();
        this.add(flowEditorComponent, BorderLayout.CENTER);
//        this.pack();

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
                xServiceGui.setBounds(rectangle.x + rectangle.width / 4, rectangle.y + rectangle.height / 4, rectangle.width / 2, rectangle.height / 2);
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
