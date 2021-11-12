package cn.boz.jb.plugin.idea.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class MyLayoutManager implements LayoutManager {
    @Override
    public void addLayoutComponent(String name, Component comp) {
        System.out.println("setting bound");
        comp.setBounds(0, 0, 40, 40);
    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Component[] components = parent.getComponents();
        int allheight = 0;
        for (Component component : components) {
            Dimension preferredSize = component.getPreferredSize();
            allheight += preferredSize.getHeight();
        }
        allheight += thick * components.length;
        System.out.println("mi" + allheight);

        return new Dimension(715, allheight);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(715, 200);
    }

    private int initialOffsetY = 0;
    int thick = 8;

    @Override
    public void layoutContainer(Container parent) {
        System.out.println("layout" + initialOffsetY);
        int width = parent.getWidth();
        int height = parent.getHeight();
        Component[] components = parent.getComponents();
        int padding = 10;
        int accy = 0;
        accy += initialOffsetY;
        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            Dimension preferredSize = component.getPreferredSize();
            Dimension minimumSize = component.getSize();
            accy += thick / 2;
            if (i == components.length - 1) {
                //最后一个元素
                int heightremain = height - accy;
                if (preferredSize.height == -1) {
                    component.setBounds(padding / 2, accy, width - padding, heightremain - thick / 2);
                } else {
                    component.setBounds(padding / 2, accy, width - padding, preferredSize.height > 200 ? 200 : preferredSize.height);
                }
            } else {
                component.setBounds(padding / 2, accy, width - padding, preferredSize.height > 200 ? 200 : preferredSize.height);
            }

            accy += preferredSize.height;
            accy += thick / 2;
        }
        System.out.println(accy + "-->" + height);
        parent.setSize(width, accy);

    }

    public void addOffset(double preciseWheelRotation) {
        this.initialOffsetY += preciseWheelRotation;
    }
}
