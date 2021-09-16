package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.listener.ToggleListener;
import cn.boz.jb.plugin.floweditor.gui.utils.ConstantUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class Button extends JComponent implements MouseListener, MouseMotionListener {
    private boolean hover = false;
    private boolean toggleAble = false;
    private boolean toggle = false;
    private boolean press = false;
    private String title;
    private String id;
    private String group;
    List<ToggleListener> listeners = new ArrayList<>();
    private String tempTitle = null;
    private Integer offset = 0;
    private boolean animation;

    Font font = FontUtils.FA;

    public Button(String title, Boolean toggleAble, String id, String group, Font font) {
        this.group = group;
        this.title = title;
        this.toggleAble = toggleAble;
        this.id = id;
        this.setPreferredSize(new Dimension(40, 40));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.animation = true;
        this.font = font;
    }


    public Button(String title, Boolean toggleAble, String id, String group) {
        this.group = group;
        this.title = title;
        this.toggleAble = toggleAble;
        this.id = id;
        this.setPreferredSize(new Dimension(40, 40));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.animation = true;
    }

    public Button(String title, Boolean toggleAble, String id) {
        this.title = title;
        this.toggleAble = toggleAble;
        this.id = id;
        this.setPreferredSize(new Dimension(40, 40));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.animation = true;
    }

    public Button(String title, Boolean toggleAble, String id, Boolean animation) {
        this.title = title;
        this.toggleAble = toggleAble;
        this.id = id;
        this.setPreferredSize(new Dimension(40, 40));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.animation = animation;
    }

    public void addMyButtonToggleListener(ToggleListener listener) {
        listeners.add(listener);
    }

    public String getId() {
        return id;
    }

    public void setToggle(boolean toggle) {
        fireToggleListener(this.toggle, toggle);
        this.toggle = toggle;
        repaint();
    }

    public boolean isToggle() {
        return toggle;
    }



    private Color getForegroundColor() {
        if (hover || (toggle && toggleAble)) {
            if ((toggle && toggleAble) || press) {
                return ConstantUtils.getInstance().getBtnActiveColor();
            } else {
                return ConstantUtils.getInstance().getBtnHoverColor();
            }
        } else {
            return ConstantUtils.getInstance().getBtnColor();
        }
    }

    private Color getBackgroundColor() {
        if (hover || (toggle && toggleAble)) {
            if ((toggle && toggleAble) || press) {
                return ConstantUtils.getInstance().getBtnBgActiveColor();
            } else {
                return ConstantUtils.getInstance().getBtnBgHoverColor();
            }
        } else {
            return null;
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        Color bgc = getBackgroundColor();
        if(bgc!=null){
            Color color = g2d.getColor();
            g2d.setColor(bgc);
//            g2d.fillRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
            g2d.fillRoundRect(4,4,this.getWidth()-8,this.getHeight()-8,5,5);
            g2d.setColor(color);
        }
        int fontsize = this.getWidth();
        if (this.getWidth() > this.getHeight()) {
            fontsize = this.getHeight();
        }
        fontsize -= 16;
        Font font = this.font.deriveFont(Font.PLAIN, fontsize);
        Font bkfont = g2d.getFont();

        FontMetrics metrics = g.getFontMetrics(font);
        g2d.setFont(font);
        String centerWords = title;


        int strHeight = metrics.getHeight();
        int top = (this.getHeight() - strHeight) / 2 + metrics.getAscent();
        Color bkc = g2d.getColor();

        g2d.setColor(getForegroundColor());
        if (tempTitle != null) {

            Dimension preferredSize = this.getPreferredSize();
            int tmpstrWidth = metrics.stringWidth(tempTitle);
            int lefttemp = (this.getWidth() - tmpstrWidth) / 2; //左边位置

            g2d.drawString(tempTitle, lefttemp, top - Math.round(preferredSize.getHeight() * offset / 100));
            int strWidth = metrics.stringWidth(centerWords);
            int left = (this.getWidth() - strWidth) / 2; //左边位置

            g2d.drawString(centerWords, left, top + preferredSize.height - Math.round(preferredSize.getHeight() * offset / 100));
        } else {
            int strWidth = metrics.stringWidth(centerWords);
            int left = (this.getWidth() - strWidth) / 2; //左边位置
            g2d.drawString(centerWords, left, top);
        }
        g2d.setFont(bkfont);
        g2d.setColor(bkc);
        g2d.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (press == true) {
            return;
        }
        if (toggleAble) {
            this.fireToggleListener(toggle, !toggle);
            toggle = !toggle;
            //并且给同组的设置toggle
            Container parent = this.getParent();
            Component[] components = parent.getComponents();
            for (Component component : components) {
                if (!(component instanceof Button)) {
                    continue;
                }
                Button comp = (Button) component;
                if (this.group != null && this.group.equals(comp.getGroup())) {
                    if (!this.getId().equals(comp.getId())) {
                        comp.setToggle(false);
                    }
                }
            }
        }
        this.press = true;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        this.press = false;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hover = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getDefaultCursor());
        hover = false;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        repaint();

    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 触发开关事件
     *
     * @param oldStatus
     * @param newStatus
     */
    private void fireToggleListener(Boolean oldStatus, Boolean newStatus) {
        if (!toggleAble) {
            return;
        }
        if (oldStatus == true && newStatus == false) {
            for (ToggleListener listener : this.listeners) {
                listener.toggleOff(this);
            }
        } else if (oldStatus == false && newStatus == true) {
            for (ToggleListener listener : this.listeners) {
                listener.toggleOn(this);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    Timer timer;

    public void setTitle(String title) {
        if (timer != null) {
            return;
        }
        if (!animation) {
            this.title = title;
            return;
        }

        synchronized (Button.class) {
            if (timer != null) {
                return;
            }
            tempTitle = this.title;
            offset = 0;
            timer = new Timer(1000 / 60, e -> {
                if (offset >= 100) {
                    offset = 0;
                    tempTitle = null;
                    timer.stop();
                    timer = null;
                } else {
                    offset += 10;
                    repaint();
                }
            });
            timer.start();
            this.title = title;
        }
    }

    public boolean isAnimation() {
        return animation;
    }

    public void setAnimation(boolean animation) {
        this.animation = animation;
    }
}
