package cn.boz.jb.plugin.floweditor.gui.utils;

import cn.boz.jb.plugin.floweditor.gui.widget.FlowEditorComponent;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontUtils {


    static {
        String[] fontNames = new String[]{"fontawesome-webfont.ttf"};
        try {
            FontUtils.FA = Font.createFont(Font.PLAIN, FlowEditorComponent.class.getClassLoader().getResourceAsStream(fontNames[0]));
            FA_25 = FontUtils.FA.deriveFont(Font.PLAIN, 25);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Font FA;
    public static Font FA_25;

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
