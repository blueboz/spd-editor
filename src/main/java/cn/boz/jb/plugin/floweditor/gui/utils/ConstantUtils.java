package cn.boz.jb.plugin.floweditor.gui.utils;

import java.awt.Color;

public class ConstantUtils {


    public static int LABEL_SHOW_FULL = 0x0001;
    public static int LABEL_SHOW_ONLY_GATEWAY = 0x0002;
    public static int LABEL_SHOW_NONE = 0x0003;

    private int labelShowMode = LABEL_SHOW_ONLY_GATEWAY;

    public static ConstantUtils INST = new ConstantUtils();

    public Color getShapeActiveBackgroundColor() {
        return ThemeConstants.SHAPE_ACTIVE_BACKGROUND;
    }

    public Color getShapeBackgroundColor() {
        return ThemeConstants.SHAPE_BACKGROUND;
    }

    public static ConstantUtils getInstance() {
        return INST;
    }

    public Color getChartPanelBoardColor() {
        return ThemeConstants.CHARTPANEL_BOARDBACKGROUND;
    }

    public Color getChartPanelHoverBorderColor() {
        return ThemeConstants.CHARTPANEL_HOVERBORDER;
    }

    public Color getChartPanelFlowLineFillColor() {
        return ThemeConstants.CHARTPANEL_FLOWLINE_FILL;
    }

    public Color getChartPanelFlowLineHoverColor() {
        return ThemeConstants.CHARTPANEL_FLOWLINE_HOVER;
    }

    public Color getChartPanelFlowLineActiveColor() {
        return ThemeConstants.CHARTPANEL_FLOWLINE_ACTIVE;
    }

    public Color getChartPanelBackgroundOddColor() {
        return ThemeConstants.CHARTPANEL_BACKGROUND_ODD;
    }

    public Color getChartPanelBackgroundEvenColor() {
        return ThemeConstants.CHARTPANEL_BACKGROUND_EVEN;
    }

    public Color getChartPanelNewFlowLineFillColor() {
        return ThemeConstants.CHART_PANEL_NEW_FLOWLINE_FILL;
    }

    public Color getShapeForegroundColor() {
        return ThemeConstants.SHAPE_FOREGROUND;
    }

    public Color getAlignLineColor() {
        return ThemeConstants.CHARTPANEL_ALIGN_LINE;
    }

    public Color getShapeResizingCtlDotColor() {
        return ThemeConstants.CHARTPANEL_SHAPE_RESIZING_CTLDOT;
    }

    public Color getShapeResizingBorderColor() {
        return ThemeConstants.CHARTPANEL_SHAPE_RESIZING_BORDER;
    }

    public Color getShapeActiveForegroundColor() {
        return ThemeConstants.SHAPE_ACTIVE_FOREGROUND;
    }

    public Color getChartPanelDraggingShapeColor() {
        return ThemeConstants.CHARTPANEL_DRAGGING_SHAPE;
    }

    public Color getChartPanelNewShapeValidColor() {
        return ThemeConstants.CHARTPANEL_NEWSHAPE_VALID;
    }

    public Color getChartPanelNewShapeInvalidColor() {
        return ThemeConstants.CHARTPANEL_NEWSHAPE_INVALID;
    }

    public int getLabelShowMode() {
        return labelShowMode;
    }

    public void setLabelShowMode(int labelShowMode) {
        this.labelShowMode = labelShowMode;
    }

    public Color getBtnColor() {
        return ThemeConstants.BTNBAR_BTN;
    }

    public Color getBtnHoverColor() {
        return ThemeConstants.BTNBAR_BTNHOVER;
    }

    public Color getBtnActiveColor() {
        return ThemeConstants.BTNBAR_BTNACTIVE;
    }

    public Color getBtnBgColor() {
        return ThemeConstants.BTNBAR_BTNBG;
    }

    public Color getBtnBgHoverColor() {
        return ThemeConstants.BTNBAR_BTNBGHOVER;
    }

    public Color getBtnBgActiveColor() {
        return ThemeConstants.BTNBAR_BTNBGACTIVE;
    }

    public Color getBtnBarColor() {
        return ThemeConstants.BTNBAR_BTNBAR;
    }

}
