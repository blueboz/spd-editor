package cn.boz.jb.plugin.floweditor.gui.shape;

import cn.boz.jb.plugin.floweditor.gui.utils.ShapeUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

public class Label extends Shape {

    private Line boundLine;

    private boolean drawBlank=false;
    private String text;

    public Label(double x, double y, double width, double height, String text) {
        super(x, y, width, height);
        this.text = text;
    }

    @Override
    public void drawShape(ChartPanel chartPanel) {
        if(!drawBlank&&StringUtils.isBlank(getText())){
            return ;
        }
        super.drawShape(chartPanel);
    }

    @Override
    public void drawContent(ChartPanel chartPanel) {
        //是否能够在原来的位置进行绘制？考虑游离的标签没有？
        HiPoint cp = boundLine.getCenterPointOfAllLine();
        Size strSize=chartPanel.getStringSize( text);
        Rect rect = new Rect(this.x + cp.x, this.y + cp.y, strSize.getW(), strSize.getH());
        this.setWidth(strSize.getW());
        this.setHeight(strSize.getH());
        HiPoint crosspoint = ShapeUtils.calculateCrossPointWithRect(rect, rect.getCenterPoint(), cp);
        if(crosspoint!=null){
            chartPanel.drawStrokeLine(cp.x,cp.y,crosspoint.x,crosspoint.y);
        }
        chartPanel.markColor();
        chartPanel.setColor(getForegroundColor());
        chartPanel.drawString(rect.getX(), rect.getY(),strSize.getW(),strSize.getH(),strSize.getH(), text);
        Line boundLine = this.getBoundLine();
        chartPanel.drawString(rect.getX(), rect.getY()+20,strSize.getW(),strSize.getH(),strSize.getH(), boundLine.getId());
        chartPanel.resetColor();


    }

    @Override
    public boolean alignRefAble() {
        return false;
    }

    @Override
    public void addXWithOffset(double offset) {
        this.x += offset;
    }

    @Override
    public void addYWithOffset(double offset) {
        this.y += offset;
    }

    @Override
    public Rect toRect() {
        HiPoint cp = boundLine.getCenterPointOfAllLine();
        return new Rect(this.x+cp.x,this.y+cp.y,this.width,this.height);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Line getBoundLine() {
        return boundLine;
    }

    public void setBoundLine(Line boundLine) {
        this.boundLine = boundLine;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
    }

    @Override
    public double getX() {
        //这里组件的x只是一个相对于中间点的坐标
        return super.getX();
    }

    @Override
    public double getY() {
        return super.getY();
    }

    @Override
    public double getWidth() {
        return super.getWidth();
    }

    @Override
    public double getHeight() {
        return super.getHeight();
    }

    @Override
    public boolean attachable() {
        return false;
    }

    @Override
    public void setBoundsTo(Rect rect) {
        //目前是暂不支持不绑定线段的，后面考虑增加绑定线段的
        HiPoint centerPointOfAllLine = this.getBoundLine().getCenterPointOfAllLine();
        this.setX(rect.getX()-centerPointOfAllLine.x );
        this.setY(rect.getY()-centerPointOfAllLine.y);
        this.setWidth(rect.getW());
        this.setHeight(rect.getH());
    }
}
