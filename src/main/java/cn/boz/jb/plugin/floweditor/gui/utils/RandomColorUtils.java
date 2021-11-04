package cn.boz.jb.plugin.floweditor.gui.utils;

import java.awt.Color;
import java.util.Random;

public class RandomColorUtils {

    private static final RandomColorUtils INST = new RandomColorUtils();
    private Color[] clist;
    private int cidx;
    private RandomColorUtils() {
        clist = new Color[]{
                new Color(0x0D6EFD),
                new Color(0x6610f2),
                new Color(0x6f42c1),
                new Color(0xd63384),
                new Color(0xdc3545),
                new Color(0xfd7e14),
                new Color(0xffc107),
                new Color(0x198754),
                new Color(0x20c997),
                new Color(0x0dcaf0),
        };

        cidx=0;
    }

    public static RandomColorUtils getInstance() {
        return INST;
    }

    /**
     * 获取随机的颜色
     * @return
     */
    public Color getRandomColor(){
        Random random = new Random();
        Color color;
        do{
            color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }while(isDark(color.getRed(),color.getGreen(),color.getBlue()));
        return color;
    }

    public static boolean isDark(Color cc) {
        int r = cc.getRed();
        int b = cc.getBlue();
        int g = cc.getGreen();
        return !(r * 0.299 + g * 0.578 + b * 0.114 >= 200);

    }

    public static boolean isDark(int r,int g,int b) {
        return !(r * 0.299 + g * 0.578 + b * 0.114 >= 200);

    }
}
