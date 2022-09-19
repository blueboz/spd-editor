package cn.boz.jb.plugin.idea.layoutmanager;

import java.awt.*;

public class MyLayoutManager implements LayoutManager {


    @Override
    public void addLayoutComponent(String name, Component comp) {
//        System.out.println("setting bound");
//        comp.setBounds(0, 0, 40, 40);
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
//        System.out.println("mi" + allheight);

        return new Dimension(715, allheight);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(715, 10);
    }

    private int initialOffsetY = 0;
    int thick = 8;

    @Override
    public void layoutContainer(Container parent) {
//        System.out.println("layout" + initialOffsetY);
        int width = parent.getWidth();
        Component[] components = parent.getComponents();
        int padding = 10;
        int accy = 0;
        accy += initialOffsetY;
        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            Dimension preferredSize = component.getPreferredSize();
            accy += thick / 2;
            if (i == components.length - 1) {
                //最后一个元素
                if (preferredSize.height == -1) {
                    component.setBounds(padding / 2, accy, width - padding, 40);
                    accy+=40;
                } else {
                    component.setBounds(padding / 2, accy, width - padding, preferredSize.height);
                    accy += preferredSize.height;
                    accy += thick / 2;
                }
            } else {
                component.setBounds(padding / 2, accy, width - padding, preferredSize.height );
                accy += preferredSize.height;
                accy += thick / 2;
            }

        }
//        System.out.println(accy + "-->" + height);
        parent.setSize(width, accy);
        parent.setPreferredSize(new Dimension(width,accy));

    }

    public void addOffset(double preciseWheelRotation) {
        this.initialOffsetY += preciseWheelRotation;
    }
}
