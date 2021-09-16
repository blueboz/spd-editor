package cn.boz.jb.plugin.floweditor.gui.utils;

import cn.boz.jb.plugin.floweditor.gui.widget.FlowEditorComponent;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontUtils {


    static {

        String[] fontNames = new String[]{"fontawesome-webfont.ttf", "PingFang Bold.ttf", "PingFang ExtraLight.ttf", "PingFang Heavy.ttf", "PingFang Light.ttf", "PingFang Medium.ttf",
                "PingFang Regular.ttf"};
        try {
            FontUtils.FA = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[0]));
            FA_25 = FontUtils.FA.deriveFont(Font.PLAIN, 25);
            PF_BOLD = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[1]));
            PF_LIGHT = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[2]));
            PF_HEAVY = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[3]));
            PF_EXTRA_LIGHT = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[4]));
            PF_MEDIUM = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[5]));
            PF = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[6]));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String PING_FANG_SC_BOLD_NAME = "PingFang SC Bold";
    public static final String PING_FANG_SC_EXTRA_LIGHT_NAME = "PingFang SC ExtraLight";
    public static final String PING_FANG_SC_HEAVY_NAME = "PingFang SC Heavy";
    public static final String PING_FANG_THIN_NAME = "平方 细体";
    public static final String PING_FANG_SC_MEDIUM_NAME = "PingFang SC Medium";
    public static final String PING_FANG_SC_REGULAR_NAME = "PingFang SC Regular";
    public static final String FONT_AWESOME_NAME = "FontAwesome";

    public static Font FA;
    public static Font FA_25;
    public static Font PF_BOLD;
    public static Font PF_LIGHT;
    public static Font PF_HEAVY;
    public static Font PF_EXTRA_LIGHT;
    public static Font PF_MEDIUM;
    public static Font PF;
    public static Font getFa(){
        return FA.deriveFont(Font.PLAIN,16);
    }

    public static String search() {
        return "\uf002";
    }

    public static String starempty() {
        return "\uf006";
    }

    public static String starfull() {
        return "\uf005";
    }

    public static String starhalf() {
        return "\uf123";
    }

    public static String arrowleft() {
        return "\uf177";
    }

    public static String sun() {
        return "\uf185";
    }

    public static String moon() {
        return "\uf186";
    }

    public static String paint() {
        return "\uf1fc";
    }

    public static String expand() {
        return "\uf065";
    }

    public static String stop() {
        return "\uf04d";
    }

    public static String arrows() {
        return "\uf047";
    }

    public static String play() {
        return "\uf04b";
    }

    public static String circleo() {
        return "\uf10c";
    }

    public static String hand() {
        return "\uf256";
    }

    public static String save() {
        return "\uf0c7";
    }

    public static String circle() {
        return "\uf111";
    }

    public static String photo() {
        return "\uf03e";
    }

    public static String load() {
        return "\uf019";
    }

    public static String rotateRight() {
        return "\uf01e";
    }

    public static String erase() {
        return "\uf12d";
    }

    public static String rotateLeft() {
        return "\uf0e2";
    }

    public static String trash() {
        return "\uf014";
    }

    public static String crosshairs() {
        return "\uf05b";
    }

    public static String windowclose() {
        return "\uf2d3";
    }

    public static String cube() {
        return "\uf1b2";
    }

    public static String cog() {
        return "\uf013";
    }

    public static String undo() {
        return "\uf0e2";
    }

    public static String user() {
        return "\uf007";
    }

    public static String plus() {
        return "\uf067";
    }

    public static String close() {
        return "\uf00d";
    }

}
