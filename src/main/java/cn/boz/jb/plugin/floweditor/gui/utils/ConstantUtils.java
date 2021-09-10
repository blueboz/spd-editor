package cn.boz.jb.plugin.floweditor.gui.utils;

import java.awt.Color;

public class ConstantUtils {
    //随机颜色
    public static int COLOR_MODE_RANDOM = 0x0002;
    //内置颜色
    public static int COLOR_MODE_ORIGINAL = 0x0001;
    //黑暗模式
    public static int COLOR_MODE_DARK = 0x0004;
    //亮白颜色
    public static int COLOR_MODE_LIGHT = 0x0001;

    public static int LABEL_SHOW_FULL = 0x0001;
    public static int LABEL_SHOW_ONLY_GATEWAY = 0x0002;
    public static int LABEL_SHOW_NONE = 0x0003;

    private int labelShowMode = LABEL_SHOW_ONLY_GATEWAY;

    private int colorModeCurrent=COLOR_MODE_ORIGINAL;
    private Color shapeActiveBackgroundColor;//new Color(78, 226, 255, 158);
    private Color shapeActiveForegroundColor;//new Color(78, 226, 255, 158);
    private Color shapeBackgroundColor;//new Color(78, 226, 255, 158);
    private Color shapeForegroundColor;//new Color(78, 226, 255, 158);
    private Color chartPanelBoardColor;//= new Color(255, 255, 255, 183);

    private Color chartPanelHoverBorderColor;//= new Color(0, 0, 0, 255);
    private Color chartPanelFlowLineFillColor;//
    private Color chartPanelFlowLineHoverColor;// new Color(165, 165, 165)
    private Color chartPanelFlowLineActiveColor;//new Color(255, 170, 0)

    private Color chartPanelBackgroundOddColor;//= new Color(181, 181, 181, 143);
    private Color chartPanelBackgroundEvenColor;//= new Color(87, 86, 86,143);
    private Color chartPanelNewFlowLineFillColor;//= new Color(87, 86, 86,143);
    private Color chartPanelDraggingShapeColor; //new Color(177, 177, 177, 255)

    //调整大小时候的控制点的颜色
    private Color shapeResizingCtlDotColor;//= new Color(242, 40, 255, 255);
    private Color shapeResizingBorderColor;//= new Color(242, 40, 255, 255);

    //新建图形的合法颜色
    private Color chartPanelNewShapeValidColor;
    //新建图形的非法颜色
    private Color chartPanelNewShapeInvalidColor;

    private Color alignLineColor;//=new Color(242, 40, 255, 139);


    private int colorMode = COLOR_MODE_ORIGINAL;

    public static ConstantUtils INST = new ConstantUtils();

    // ，移动指向重点对象功能
    private ConstantUtils() {
        int colorModeCurrent = getColorModeCurrent();
        if(colorModeCurrent==COLOR_MODE_DARK){
            setColorModeDark();
        }else if(colorModeCurrent==COLOR_MODE_ORIGINAL){
            setColorModeLight();
        }else if(colorModeCurrent==COLOR_MODE_RANDOM){
            setColorfulMode();
        }
    }


    public int getColorMode() {
        return colorMode;
    }

    /**
     * @param colorMode
     */
    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
        if (COLOR_MODE_LIGHT == this.colorMode) {

        } else if (COLOR_MODE_DARK == this.colorMode) {

        } else if (COLOR_MODE_RANDOM == this.colorMode) {

        }
    }

    /**
     * 亮色模式下的控件颜色
     *
     * @param
     */
    public void setColorModeDark() {
        setColorModeCurrent(COLOR_MODE_DARK);

        shapeActiveBackgroundColor = new Color(78, 226, 255, 181);
        shapeActiveForegroundColor= new Color(255, 255, 255);;
        shapeBackgroundColor = new Color(255, 255, 255, 65);
        shapeForegroundColor = new Color(234, 234, 234);

        chartPanelBoardColor = new Color(6, 6, 6, 134);
        chartPanelHoverBorderColor = new Color(255, 255, 255, 255);
        chartPanelFlowLineActiveColor = new Color(151, 237, 251, 255);
        chartPanelFlowLineHoverColor = new Color(145, 229, 242, 255);
        chartPanelFlowLineFillColor = new Color(30, 153, 178, 255);
        chartPanelNewFlowLineFillColor = new Color(145, 229, 242, 255);
        chartPanelBackgroundOddColor = new Color(181, 181, 181, 143);
        chartPanelBackgroundEvenColor = new Color(87, 86, 86, 143);
        alignLineColor = new Color(13, 190, 254);
        shapeResizingBorderColor = new Color(145, 229, 242, 255);
        shapeResizingCtlDotColor = new Color(145, 229, 242, 255);
        chartPanelDraggingShapeColor=new Color(177, 177, 177, 255);
        chartPanelNewShapeInvalidColor= Color.RED;
        chartPanelNewShapeValidColor=Color.GREEN;
    }

    /**
     * 亮色模式下的控件颜色
     *
     * @param
     */
    public void setColorModeLight() {
        setColorModeCurrent(COLOR_MODE_LIGHT);
        shapeActiveBackgroundColor = new Color(78, 226, 255, 158);
        shapeActiveForegroundColor= new Color(0, 0, 0);;
        shapeBackgroundColor = new Color(255, 253, 253, 139);
        shapeForegroundColor = new Color(0, 0, 0);

        chartPanelBoardColor = new Color(255, 255, 255, 134);
        chartPanelHoverBorderColor = new Color(0, 0, 0, 255);
        chartPanelFlowLineActiveColor = new Color(0, 148, 179);
        chartPanelFlowLineHoverColor = new Color(165, 165, 165);
        chartPanelFlowLineFillColor = new Color(0, 0, 0);
        chartPanelBackgroundOddColor = new Color(255, 255, 255, 125);
        chartPanelBackgroundEvenColor = new Color(212, 212, 212, 112);
        chartPanelDraggingShapeColor=new Color(106, 106, 106, 255);

        chartPanelNewShapeInvalidColor=new Color(219, 19, 19);
        chartPanelNewShapeValidColor=new Color(0, 203, 13);
    }

    /**
     * 亮色模式下的控件颜色
     *
     * @param
     */
    public void setColorfulMode() {
        //保证返回的颜色都是随机的
        this.setColorModeLight();
        this.setColorModeCurrent(COLOR_MODE_RANDOM);
    }

    public Color getShapeActiveBackgroundColor() {
        return shapeActiveBackgroundColor;
    }

    public void setShapeActiveBackgroundColor(Color shapeActiveBackgroundColor) {
        this.shapeActiveBackgroundColor = shapeActiveBackgroundColor;
    }

    public Color getShapeBackgroundColor() {
        return shapeBackgroundColor;
    }

    public void setShapeBackgroundColor(Color shapeBackgroundColor) {
        this.shapeBackgroundColor = shapeBackgroundColor;
    }

    public static ConstantUtils getInstance() {
        return INST;
    }

    public Color getChartPanelBoardColor() {
        return chartPanelBoardColor;
    }

    public void setChartPanelBoardColor(Color chartPanelBoardColor) {
        this.chartPanelBoardColor = chartPanelBoardColor;
    }

    public Color getChartPanelHoverBorderColor() {
        return chartPanelHoverBorderColor;
    }

    public void setChartPanelHoverBorderColor(Color chartPanelHoverBorderColor) {
        this.chartPanelHoverBorderColor = chartPanelHoverBorderColor;
    }

    public Color getChartPanelFlowLineFillColor() {
        return chartPanelFlowLineFillColor;
    }

    public void setChartPanelFlowLineFillColor(Color chartPanelFlowLineFillColor) {
        this.chartPanelFlowLineFillColor = chartPanelFlowLineFillColor;
    }

    public Color getChartPanelFlowLineHoverColor() {
        return chartPanelFlowLineHoverColor;
    }

    public void setChartPanelFlowLineHoverColor(Color chartPanelFlowLineHoverColor) {
        this.chartPanelFlowLineHoverColor = chartPanelFlowLineHoverColor;
    }

    public Color getChartPanelFlowLineActiveColor() {
        return chartPanelFlowLineActiveColor;
    }

    public void setChartPanelFlowLineActiveColor(Color chartPanelFlowLineActiveColor) {
        this.chartPanelFlowLineActiveColor = chartPanelFlowLineActiveColor;
    }

    public Color getChartPanelBackgroundOddColor() {
        return chartPanelBackgroundOddColor;
    }

    public void setChartPanelBackgroundOddColor(Color chartPanelBackgroundOddColor) {
        this.chartPanelBackgroundOddColor = chartPanelBackgroundOddColor;
    }

    public Color getChartPanelBackgroundEvenColor() {
        return chartPanelBackgroundEvenColor;
    }

    public void setChartPanelBackgroundEvenColor(Color chartPanelBackgroundEvenColor) {
        this.chartPanelBackgroundEvenColor = chartPanelBackgroundEvenColor;
    }

    public Color getChartPanelNewFlowLineFillColor() {
        return chartPanelNewFlowLineFillColor;
    }

    public void setChartPanelNewFlowLineFillColor(Color chartPanelNewFlowLineFillColor) {
        this.chartPanelNewFlowLineFillColor = chartPanelNewFlowLineFillColor;
    }

    public Color getShapeForegroundColor() {
        return shapeForegroundColor;
    }

    public void setShapeForegroundColor(Color shapeForegroundColor) {
        this.shapeForegroundColor = shapeForegroundColor;
    }

    public Color getAlignLineColor() {
        return alignLineColor;
    }

    public void setAlignLineColor(Color alignLineColor) {
        this.alignLineColor = alignLineColor;
    }

    public Color getShapeResizingCtlDotColor() {
        return shapeResizingCtlDotColor;
    }

    public void setShapeResizingCtlDotColor(Color shapeResizingCtlDotColor) {
        this.shapeResizingCtlDotColor = shapeResizingCtlDotColor;
    }

    public Color getShapeResizingBorderColor() {
        return shapeResizingBorderColor;
    }

    public void setShapeResizingBorderColor(Color shapeResizingBorderColor) {
        this.shapeResizingBorderColor = shapeResizingBorderColor;
    }

    public Color getShapeActiveForegroundColor() {
        return shapeActiveForegroundColor;
    }

    public void setShapeActiveForegroundColor(Color shapeActiveForegroundColor) {
        this.shapeActiveForegroundColor = shapeActiveForegroundColor;
    }

    public Color getChartPanelDraggingShapeColor() {
        return chartPanelDraggingShapeColor;
    }

    public void setChartPanelDraggingShapeColor(Color chartPanelDraggingShapeColor) {
        this.chartPanelDraggingShapeColor = chartPanelDraggingShapeColor;
    }

    public int getColorModeCurrent() {
        return colorModeCurrent;
    }

    public void setColorModeCurrent(int colorModeCurrent) {
        this.colorModeCurrent = colorModeCurrent;
    }

    public Color getChartPanelNewShapeValidColor() {
        return chartPanelNewShapeValidColor;
    }

    public void setChartPanelNewShapeValidColor(Color chartPanelNewShapeValidColor) {
        this.chartPanelNewShapeValidColor = chartPanelNewShapeValidColor;
    }

    public Color getChartPanelNewShapeInvalidColor() {
        return chartPanelNewShapeInvalidColor;
    }

    public void setChartPanelNewShapeInvalidColor(Color chartPanelNewShapeInvalidColor) {
        this.chartPanelNewShapeInvalidColor = chartPanelNewShapeInvalidColor;
    }

    public int getLabelShowMode() {
        return labelShowMode;
    }

    public void setLabelShowMode(int labelShowMode) {
        this.labelShowMode = labelShowMode;
    }
}
