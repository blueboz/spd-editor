package cn.boz.jb.plugin.floweditor.gui.shape;

import cn.boz.jb.plugin.floweditor.gui.utils.LineUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.ShapeUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

import java.awt.*;

/**
 * 圆形
 */
public class Circle extends Shape {

    public Circle() {

    }

    public Circle(double x, double y) {
        super(x, y);
    }

    public Circle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void drawContent(ChartPanel chartPanel) {
        chartPanel.fillOval(this.x, this.y, this.width, this.height);
        drawInnerBorder(chartPanel, this, borderWidth, borderStyle, borderColor);
    }


    /**
     * 本题目不能使用参数方程求解
     * 椭圆方程为
     * (x/a)²+(y/b)²=1
     * 线段以圆中心为起点(x1,y1)，到另外一点(x2,y2)与x轴相夹角
     * sin(θ)=dy/(dy²+dx²)^½
     * cos(θ)=dx/(dy²+dx²)^½
     * 其中dy=y2-y1     dx=x2-x1
     * 由于椭圆与线段的夹角已经求出了
     * 而且椭圆是的某一段(x,y)有y/x=tan(θ)=sin(θ)/cos(θ)
     * 将y=x*tan(θ)代入椭圆方程得
     * <p>
     * (x*b)²+(y*a)²=(a*b)²
     * (x*b)²+(x*a)²*tan²(θ)=(a*b)²
     * x²*(b²+a²*tan²(θ))=(a*b)²
     * x²=(a*b)²/(b²+a²*tan²(θ))
     * x=(a*b)/{(b²+a²*tan²(θ))}^½
     * y=x*tan(θ)代入上式
     * 可以得
     * y=(a*b*tan(θ)/{(b²+a²*tan²(θ))}^½
     * 求得的x,y是相对于原点的椭圆而言，所以得到的x,y仅仅是增量，需要叠加上图形原点坐标方可以得到真实的坐标
     */
    @Override
    public HiPoint calculateCrossPoint(HiPoint lineStartPoint, HiPoint lineEndPoint) {
        return LineUtils.calcCrossPointWithOvel(lineStartPoint, lineEndPoint, getCenterPoint(), width, height);
    }

    /**
     * 描边
     *
     * @param chartPanel
     * @param shape
     * @param bw
     * @param bstyle
     * @param bColor
     */
    protected void drawInnerBorder(ChartPanel chartPanel, Shape shape, int bw, String bstyle, Color bColor) {
        if (bw > 0) {
            chartPanel.markColor();
            chartPanel.markStroke();
            chartPanel.setColor(bColor);
            chartPanel.setStroke(ShapeUtils.getStoke(bw, bstyle));
            chartPanel.drawOval(shape.x, shape.y, shape.width - 1, shape.height - 1);
            chartPanel.resetStroke();
            chartPanel.resetColor();
        }
    }

    @Override
    public void drawNewShape(ChartPanel chartPanel, Rect rect) {
        chartPanel.drawOval(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    @Override
    public void init(HiPoint hiPoint) {
        this.setX(hiPoint.x);
        this.setY(hiPoint.y);
        this.setWidth(35);
        this.setHeight(35);
    }

    @Override
    public String getIdPrefix() {
        return "circle";
    }
}
