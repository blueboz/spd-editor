package cn.boz.jb.plugin.floweditor.gui.utils;


import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class IcoMoonUtils {

    private static Font FONT;


    static {
        try {
            FONT = Font.createFont(Font.PLAIN,
                    IcoMoonUtils.class.getClassLoader().getResourceAsStream("icomoon/icomoon.ttf"));

        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Font getFont() {
        return FONT;
    }

    public static Font getFont20() {
        return FONT.deriveFont(Font.PLAIN, 20);
    }

    public static Font getFont25() {
        return FONT.deriveFont(Font.PLAIN, 25);
    }


    public static Font getFont36() {
        return FONT.deriveFont(Font.PLAIN, 36);
    }

    public static Font getFont48() {
        return FONT.deriveFont(Font.PLAIN, 48);
    }
    public static Font getFont72() {
        return FONT.deriveFont(Font.PLAIN, 72);
    }
    public static Font getFont(int size) {
        return FONT.deriveFont(Font.PLAIN, size);

    }


    public static Cursor initCursor(String text) {
        int size = 32;
        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        Font font16 = IcoMoonUtils.getFont(size);
        FontMetrics fontMetrics = graphics.getFontMetrics(font16);
        double offsety = Math.round(size / 2.0 - fontMetrics.getDescent() + fontMetrics.getAscent() / 2.0);
        graphics.setFont(font16);
        boolean dark = RandomColorUtils.isDark(ThemeConstants.SHAPE_MODE_COLOR);
        if(dark){
            graphics.setColor(ThemeConstants.SHAPE_MODE_COLOR.brighter());
        }else{
            graphics.setColor(ThemeConstants.SHAPE_MODE_COLOR.darker());
        }

        graphics.fillRect(0, 0, size, size);
        graphics.setColor(ThemeConstants.SHAPE_MODE_COLOR);
        graphics.drawString(text, 0, (int) offsety);
        return Toolkit.getDefaultToolkit().createCustomCursor(bufferedImage, new Point(16, 16), "cursor");
    }


    public static Font getFont16() {
        return FONT.deriveFont(Font.PLAIN, 16);
    }

    public static String getEquation() {
        return "\ue900";
    }

    public static String getAlign() {
        return "\ue901";
    }

    public static String getErase() {
        return "\ue902";
    }

    public static String getEText() {
        return "\ue903";
    }

    public static String getFText() {
        return "\ue904";
    }

    public static String getHText() {
        return "\ue905";
    }

    public static String getImage() {
        return "\ue906";
    }

    public static String getRedo() {
        return "\ue907";
    }

    public static String getScale() {
        return "\ue908";
    }

    public static String getTrash() {
        return "\ue909";
    }

    public static String getUndo() {
        return "\ue90a";
    }

    public static String getMove() {
        return "\ue90b";
    }

    public static String getCallActivity() {
        return "\ue90c";
    }

    public static String getEndEvent() {
        return "\ue90d";
    }

    public static String getExclusiveGateway() {
        return "\ue90e";
    }

    public static String getForeachGateway() {
        return "\ue90f";
    }

    public static String getParallelGateway() {
        return "\ue910";
    }

    public static String getSequenceFlow() {
        return "\ue911";
    }

    public static String getServiceTask() {
        return "\ue912";
    }

    public static String getStartEvent() {
        return "\ue913";
    }

    public static String getUserTask() {
        return "\ue914";
    }

    public static String getCircle() {
        return "\ue915";
    }

    public static String getUser() {
        return "\ue916";
    }

    public static String getClose() {
        return "\ue917";
    }

    public static String getPlus() {
        return "\ue918";
    }

    public static String getGear() {
        return "\ue919";
    }

    public static String getStop() {
        return "\ue91a";
    }

    public static String getPlay() {
        return "\ue91b";
    }

    public static String getSQL() {
        return "\ue91c";
    }

    public static String getSave() {
        return "\ue91e";
    }

    /**
     * 自动化保存
     * @return
     */
    public static String getAutomation() {
        return "\ue91f";
    }

    public static String getManual() {
        return "\ue924";
    }

    /**
     * 从磁盘刷新
     * @return
     */
    public static String getReload() {
        return "\ue921";
    }
}
