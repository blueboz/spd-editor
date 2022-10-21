package cn.boz.jb.plugin.floweditor.gui.utils;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FunctionIconUtils {

    private static Font FONT;

    static {
        try {
            FONT = Font.createFont(Font.PLAIN,
                    FunctionIconUtils.class.getClassLoader().getResourceAsStream("font/function/function.ttf"));

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


        Font font16 = FunctionIconUtils.getFont(size);
        FontMetrics fontMetrics = graphics.getFontMetrics(font16);
        double offsety = Math.round(size / 2.0 - fontMetrics.getDescent() + fontMetrics.getAscent() / 2.0);
        graphics.setFont(font16);
        boolean dark = RandomColorUtils.isDark(ThemeConstants.SHAPE_MODE_COLOR);
        if (dark) {
            graphics.setColor(ThemeConstants.SHAPE_MODE_COLOR.brighter());
        } else {
            graphics.setColor(ThemeConstants.SHAPE_MODE_COLOR.darker());
        }

        graphics.fillRect(0, 0, size, size);
        graphics.setColor(ThemeConstants.SHAPE_MODE_COLOR);
        graphics.drawString(text, 0, (int) offsety);
        return Toolkit.getDefaultToolkit().createCustomCursor(bufferedImage, new Point(16, 16), "cursor");
    }

    public static final String ICO_LOADING = "\ue900";
    public static final String ICO_MENU = "\ue901";
    public static final String ICO_ACTION = "\ue902";
    public static final String ICO_CODE = "\ue903";
    public static final String ICO_DATABASE = "\ue904";
    public static final String ICO_GEAR = "\ue905";
    public static final String ICO_JAVASCRIPT = "\ue906";
    public static final String ICO_BATCH = "\ue907";

}
