package cn.boz.jb.plugin.algorithm;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;

class AlgorithmCmp extends JComponent {

    private BufferedImage bufferedImage;

    private Timer timer;

    private int i = 0;

    public AlgorithmCmp() {
        try (InputStream inputStream = ChartPanel.class.getClassLoader().getResourceAsStream("background.jpeg");) {
            bufferedImage = ImageIO.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                i += 1;
                if (i > 360) {
                    i = 0;
                }
                repaint();
            }
        });
        timer.start();
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D graphics2D = (Graphics2D) g;
//        graphics2D.transform(AffineTransform.getScaleInstance(i/1.0,i/1.0));
//        graphics2D.transform(AffineTransform.getRotateInstance(i/10.0,this.getWidth()/2,this.getHeight()/2));

        AffineTransform translateInstance = AffineTransform.getTranslateInstance(i, -45);
//        graphics2D.transform(AffineTransform.getRotateInstance(2));
//        graphics2D.transform(AffineTransform.getTranslateInstance(45,45));
//        graphics2D.drawRect(0,0,50,50);
        AffineTransform affineTransform = new AffineTransform(new double[]{Math.cos(i * Math.PI / 180), Math.sin(i * Math.PI / 180),
                -Math.sin(i * Math.PI / 180), Math.cos(i * Math.PI / 180), 0, 0});
        graphics2D.transform(affineTransform);

        graphics2D.drawImage(bufferedImage, 0, 0, null);
        graphics2D.dispose();
    }
}

public class AlgorithmGui extends JFrame {

    private AlgorithmCmp algorithmCmp;

    public AlgorithmGui() throws HeadlessException {

        algorithmCmp = new AlgorithmCmp();
        this.setLayout(new BorderLayout());
        this.add(algorithmCmp, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //注册字体
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                AlgorithmGui algorithmGui = new AlgorithmGui();
                algorithmGui.setVisible(true);
                Rectangle rectangle = ge.getMaximumWindowBounds();
                algorithmGui.setBounds((int) rectangle.getWidth() / 2, 0, rectangle.width / 2, rectangle.height);
                algorithmGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
