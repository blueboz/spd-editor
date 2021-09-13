package cn.boz.jb.plugin.floweditor.gui.utils;


import java.awt.Font;
import java.awt.FontFormatException;
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
        return FONT.deriveFont(Font.PLAIN,20);
    }

    public static Font getFont25() {
        return FONT.deriveFont(Font.PLAIN,25);
    }

    public static Font getFont16() {
        return FONT.deriveFont(Font.PLAIN,16);
    }


    public static String getPlay() {
        return "\ue90f";
    }

    public static String getStop() {
        return "\ue90e";
    }

    public static String getCallActivity() {
        return "\ue900";
    }

    public static String getEndEvent() {
        return "\ue901";
    }

    public static String getExclusiveGateway() {
        return "\ue902";
    }

    public static String getForeachGateway() {
        return "\ue903";
    }

    public static String getParallelGateway() {
        return "\ue904";
    }

    public static String getSequenceFlow() {
        return "\ue905";
    }

    public static String getServiceTask() {
        return "\ue906";
    }

    public static String getStartEvent() {
        return "\ue907";
    }

    public static String getUserTask() {
        return "\ue908";
    }

    public static String getCircle() {
        return "\ue909";
    }
    public static String getUser() {
        return "\ue90a";
    }
    public static String getClose() {
        return "\ue90b";
    }
    public static String getPlus() {
        return "\ue90c";
    }
    public static String getGear() {
        return "\ue90d";
    }

}
