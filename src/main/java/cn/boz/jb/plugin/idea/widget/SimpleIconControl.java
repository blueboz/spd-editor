package cn.boz.jb.plugin.idea.widget;

import com.intellij.util.IconUtil;

import javax.swing.*;
import java.awt.*;

public class SimpleIconControl extends JComponent {


    private final Icon icon;

    public SimpleIconControl(Icon icon) {
        this.icon = icon;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D currentGraphic = (Graphics2D) g;
        currentGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        currentGraphic.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        currentGraphic.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        currentGraphic.setColor(getForeground());

        int x = (this.getWidth() - icon.getIconWidth()) / 2;

        int y = (this.getHeight() - icon.getIconHeight()) / 2;

        Icon colorize = IconUtil.colorize(icon, Color.WHITE);

        colorize.paintIcon(this, g, x, y);
        g.dispose();
    }
}
