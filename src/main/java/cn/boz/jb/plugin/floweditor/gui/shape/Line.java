package cn.boz.jb.plugin.floweditor.gui.shape;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.hist.BaseState;
import cn.boz.jb.plugin.floweditor.gui.hist.LineState;
import cn.boz.jb.plugin.floweditor.gui.hist.Restorable;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.utils.ConstantUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.LineUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 线段
 */
public class Line implements Restorable, Comparable, PropertyObject, Cloneable {
    protected String id;
    protected String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String sourceRef;
    private String targetRef;
    //鼠标当前是否悬浮于图形上面
    public boolean mousePressing = false;
    private boolean cursorInside = false;
    public static int smallDotR = 2;
    public static int bigDotR = 3;
    private Shape startShape;
    private Shape endShape;
    private int hoverMinDistance = 4;
    private LinkedList<HiPoint> points = new LinkedList<>();
    //被选中
    private boolean selected = false;
    //是否鼠标炫富
    private boolean hover = false;
    public static double TRIANGLE_DISTANCE = 10;
    //此属性供外部使用
    private boolean focusing;

    //该线段的标签
    private Label label;

    /**
     * 获取起始点
     *
     * @return
     */
    public HiPoint getStartPoint() {
        HiPoint startCenter = this.startShape.getCenterPoint();
        HiPoint endCenter = this.endShape.getCenterPoint();
        LinkedList<HiPoint> result = new LinkedList<>();
        result.add(startCenter);
        result.addAll(points);
        result.add(endCenter);
        HiPoint firstNext = result.get(1);
        return this.startShape.calculateCrossPoint(firstNext);
    }

    /**
     * 获取结束点
     *
     * @return
     */
    public HiPoint getEndPoint() {
        HiPoint startCenter = this.startShape.getCenterPoint();
        HiPoint endCenter = this.endShape.getCenterPoint();
        LinkedList<HiPoint> result = new LinkedList<>();
        result.add(startCenter);
        result.addAll(points);
        result.add(endCenter);
        HiPoint lastPrev = result.get(result.size() - 2);
        return this.endShape.calculateCrossPoint(lastPrev);
    }

    /**
     * 获取所有的节点附带开始节点与结束节点
     *
     * @return
     */
    public List<HiPoint> getAllPointWithStartEnd() {
        LinkedList<HiPoint> result = new LinkedList<>();
        HiPoint startPoint = getStartPoint();
        HiPoint endPoint = getEndPoint();
        result.add(startPoint);
        result.addAll(points);
        result.add(endPoint);
        return result;
    }


    public boolean above(HiPoint point) {
        //判断点是否在线段上
        //开始点
        List<HiPoint> allPointWithStartEnd = getAllPointWithStartEnd();
        for (int i = 0; i < allPointWithStartEnd.size() - 1; i++) {
            if (aboveExt(point, allPointWithStartEnd.get(i), allPointWithStartEnd.get(i + 1))) {
                return true;
            }
        }
        return false;
    }

    public boolean above(HiPoint mouseMovePoint, HiPoint target) {
        double distance = Math.sqrt(Math.pow(mouseMovePoint.x - target.x, 2) + Math.pow(mouseMovePoint.y - target.y, 2));
        if (distance <= Line.smallDotR + 3) {
            return true;
        }
        return false;

    }

    /**
     * 判断点与点是否在两点之连线之中间点
     *
     * @param point      当前悬浮点
     * @param startPoint 起始点
     * @param endPoint   结束点
     * @return
     */
    public boolean aboveExt(HiPoint point, HiPoint startPoint, HiPoint endPoint) {
        double sx = startPoint.x;
        double sy = startPoint.y;

        double ex = endPoint.x;
        double ey = endPoint.y;


        double a = ey - sy;
        double b = sx - ex;
        double c = -(a * sx + b * sy);
        //ax+by+c=0 就是线段的方程
        double v = Math.abs(a * point.x + b * point.y + c) / Math.sqrt(a * a + b * b);

        if (v <= hoverMinDistance) {
            //其次判断大区域
            //bx-ay+c=0
            //[(sx+ex)/2,(sy+ey)/2]
            //[p,q]
            //c=aq-bp
            //distance=sqrt((x1-x2)^2+(y1-y2)^2)+2*hoverMinDistance
            double p = (sx + ex) / 2;
            double q = (sy + ey) / 2;
//            System.out.println(String.format("sp:(%d,%d) ep:(%d,%d) cp:(%.2f,%.2f)", startPoint.x,startPoint.y,endPoint.x,endPoint.y,p,q));
            double c2 = a * q - b * p;
            double a2 = b;
            double b2 = -a;
            double v2 = Math.abs(a2 * point.x + b2 * point.y + c2) / Math.sqrt(a2 * a2 + b2 * b2);
            double distance = (Math.sqrt(Math.pow(ex - sx, 2) + Math.pow(ey - sy, 2)) + 0 * hoverMinDistance) / 2;
            if (v2 <= distance) {
                return true;
            }
        }
        return false;
    }


    /**
     * 计算与交点距离指定距离的点
     *
     * @param crossPoint 交点坐标
     * @return
     */
    public HiPoint calculateCrossDistancePoint(HiPoint crossPoint, float distance) {
        List<HiPoint> allpoints = getAllPointWithStartEnd();
        HiPoint centerPoint = endShape.getCenterPoint();
        double[] abv = LineUtils.calcAAndBValue(centerPoint, crossPoint);
        double a = abv[0];
        double b = abv[1];
        //Ax+By+C=0
        //Aδx+Bδy=0
        //δx²+δy²=D²
        //求解δx与δy
        //左边作者上边
        HiPoint point = allpoints.get(allpoints.size() - 1);

        HiPoint[] points = calculateDistancePoint(endShape.getCenterPoint(), point, crossPoint, distance);
        if (a == 0) {
            //δx=0 δy=±D
            //取left of right 决定于centerPoint.y与crosspoint.y
            //if cy>xy->-
            //if cy<xy
            if (crossPoint.x > centerPoint.x) {
                //crossPoint在centerPoint右边，加
                return points[0];
            } else {
                //左边-
                return points[1];
            }
        } else if (b == 0) {
            //δy=0 δx=±D
            if (crossPoint.y > centerPoint.y) {
                //crossPoint在centerPoint 下方，加
                return points[0];
            } else {
                //crossPoint在centerPoint 上方，减
                return points[1];
            }
        } else {//|sqrt(D*A/(A²+B²)^0.5)|=δx    δy=-Bδx/A
            //究竟应该取正或者负
            if (crossPoint.x > centerPoint.x) {
                //加
                double deltaX = Math.sqrt(Math.pow(distance * b, 2) / (a * a + b * b));
                double deltaY = -a * deltaX / b;
                return new HiPoint((crossPoint.x + deltaX), (crossPoint.y + deltaY));
            } else {
                double deltaX = -Math.sqrt(Math.pow(distance * b, 2) / (a * a + b * b));
                double deltaY = -a * deltaX / b;
                return new HiPoint(crossPoint.x + deltaX, crossPoint.y + deltaY);
            }
        }
    }

    /**
     * relative point
     *
     * @param linePointStart start position of the line
     * @param linePointEnd   end position of the line
     * @param relativePoint  relative should on the line
     * @param distance       the distance of the relativePoint and the calculate point
     * @return
     */
    public static HiPoint[] calculateDistancePoint(HiPoint linePointStart, HiPoint linePointEnd, HiPoint relativePoint, float distance) {
        double a = linePointStart.y - linePointEnd.y;
        double b = linePointEnd.x - linePointStart.x;
        return calculateDistancePoint(relativePoint, distance, a, b);
    }

    /**
     * a b 分别是ax+by+c=0中的ab
     *
     * @param relativePoint
     * @param distance
     * @param a
     * @param b
     * @return
     */
    public static HiPoint[] calculateDistancePoint(HiPoint relativePoint, float distance, double a, double b) {
        if (a == 0) {
            //δx=0 δy=±D
            //取left of right 决定于centerPoint.y与crosspoint.y
            //if cy>xy->-
            //if cy<xy
            //crossPoint在centerPoint右边，加
            return new HiPoint[]{
                    new HiPoint(relativePoint.x + distance, relativePoint.y),
                    new HiPoint(relativePoint.x - distance, relativePoint.y)};
        } else if (b == 0) {
            //δy=0 δx=±D
            return new HiPoint[]{
                    new HiPoint(relativePoint.x, relativePoint.y + distance),
                    new HiPoint(relativePoint.x, relativePoint.y - distance)};
        } else {//|sqrt(D*A/(A²+B²)^0.5)|=δx    δy=-Bδx/A
            //究竟应该取正或者负
            //加
            double deltaX = Math.sqrt(Math.pow(distance * b, 2) / (a * a + b * b));
            double deltaY = -a * deltaX / b;
            return new HiPoint[]{
                    new HiPoint(relativePoint.x + deltaX, relativePoint.y + deltaY),
                    new HiPoint(relativePoint.x - deltaX, relativePoint.y - deltaY)
            };
        }
    }

    /**
     * 是否在断点上悬浮，并且返回悬浮的点坐标
     *
     * @param hoverPoint
     * @return
     */
    public PointLink hoveringPoint(HiPoint hoverPoint) {
        List<HiPoint> allpoints = getAllPointWithStartEnd();
        //悬浮于
        //checking is hovering centerpoint
        HiPoint startCenter = startShape.getCenterPoint();
        HiPoint endCenter = endShape.getCenterPoint();

        for (int i = 1; i < allpoints.size() - 1; i++) {
            HiPoint left = allpoints.get(i - 1);
            HiPoint right = allpoints.get(i + 1);
            HiPoint current = allpoints.get(i);
            if (above(hoverPoint, current)) {
                PointLink pointLink = new PointLink();
                pointLink.setPrev(left);
                pointLink.setVirtual(new HiPoint(current.x, current.y));
                pointLink.setNext(right);
                pointLink.setVal(current);
                pointLink.setReal(true);
                pointLink.setHead(startCenter);
                pointLink.setTail(endCenter);
                if (i == 1) {
                    pointLink.setPrev(pointLink.getHead());
                    pointLink.setPrevIsHead(true);
                }
                if (i == allpoints.size() - 2) {
                    pointLink.setNext(pointLink.getTail());
                    pointLink.setNextIsTail(true);
                }
                return pointLink;
            }
        }


        for (int i = 0; i < allpoints.size() - 1; i++) {
            HiPoint lp = allpoints.get(i);
            HiPoint rp = allpoints.get(i + 1);
            HiPoint centerPoint = getCenterPoint(lp, rp);
            if (!above(hoverPoint, centerPoint)) {
                continue;
            }
            //增加 误差范围
            PointLink pointLink = new PointLink();
            pointLink.setPrev(lp);
            pointLink.setVirtual(getCenterPoint(lp, rp));
            pointLink.setNext(rp);
            pointLink.setVal(getCenterPoint(lp, rp));
            pointLink.setReal(false);
            pointLink.setHead(startCenter);
            pointLink.setTail(endCenter);
            if (i == 0) {
                pointLink.setPrev(pointLink.getHead());
                pointLink.setPrevIsHead(true);
            }
            if (i == allpoints.size() - 2) {
                pointLink.setNext(pointLink.getTail());
                pointLink.setNextIsTail(true);
            }
            return pointLink;
        }


        return null;
    }


    public static HiPoint getCenterPoint(HiPoint left, HiPoint right) {
        return new HiPoint((left.x + right.x) / 2, (left.y + right.y) / 2);
    }

    /**
     * 计算两点之间的距离
     *
     * @param a a 点
     * @param b b 点
     * @return
     */
    public static double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * 鼠标点击的是时候触发的事件
     *
     * @param e
     */
    public boolean onClick(MouseEvent e) {
        return true;
    }

    /**
     * 更新鼠标悬浮状态
     *
     * @param inside
     * @param e
     * @return
     */
    public boolean updateHoverStatus(Boolean inside, MouseEvent e) {
        if (cursorInside != inside) {
            this.cursorInside = inside;
            if (inside == true) {
                mouseEnter(e);
                return true;
            } else {
                mouseLeave(e);
                return true;
            }
        } else if (inside == true) {
            mouseMoved(e);
        }

        return false;
    }

    /**
     * 鼠标在控件上移动所触发的事件
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * 鼠标进入事件
     */
    public void mouseEnter(MouseEvent e) {
        this.hover = true;

    }

    /**
     * 鼠标离开事件
     */
    public void mouseLeave(MouseEvent e) {
        this.hover = false;


    }

    /**
     * 鼠标击中，但是未弹起
     *
     * @param e
     * @return
     */
    public Boolean mousePressed(MouseEvent e) {
        mousePressing = true;
        return true;
    }

    public Boolean mouseReleased(MouseEvent e) {
        return true;
    }

    /**
     * @param e
     */
    public boolean blur(MouseEvent e) {
        this.selected = false;
        return true;
    }

    /**
     * 控件得到焦点的时候触发的事件
     *
     * @param e
     * @return
     */
    public boolean focus(MouseEvent e) {

        this.selected = true;
        this.focusing = true;
        return true;
    }

    /**
     * 临时序列化，供状态恢复
     *
     * @return
     */
    @Override
    public BaseState serialize() {
        LinkedList<HiPoint> points = this.points;
        LineState lineTempory = new LineState();
        lineTempory.startShape = this.startShape;
        lineTempory.endShape = this.endShape;
        lineTempory.label = this.label;
        for (HiPoint point : points) {
            lineTempory.points.add(new HiPoint(point.x, point.y));
        }
        lineTempory.operated = this;
        return lineTempory;
    }

    /**
     * 按照给定指令进行状态的恢复
     *
     * @param state
     */
    @Override
    public void restore(BaseState state) {
        LineState lineTempory = (LineState) state;
        this.startShape = lineTempory.startShape;
        this.endShape = lineTempory.endShape;
        this.points = lineTempory.points;
        this.label = lineTempory.label;
        ;
    }

    @Override
    public int compareTo(Object o) {
        return this.hashCode() - o.hashCode();
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public Shape getStartShape() {
        return startShape;
    }

    public void setStartShape(Shape startShape) {
        this.startShape = startShape;
    }

    public Shape getEndShape() {
        return endShape;
    }

    public void setEndShape(Shape endShape) {
        this.endShape = endShape;


    }

    public LinkedList<HiPoint> getPoints() {
        return points;
    }

    public void setPoints(LinkedList<HiPoint> points) {
        this.points = points;
    }

    public boolean isCursorInside() {
        return cursorInside;
    }

    public void setCursorInside(boolean cursorInside) {
        this.cursorInside = cursorInside;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isHover() {
        return hover;
    }

    public void setHover(boolean hover) {
        this.hover = hover;
    }

    public boolean isFocusing() {
        return focusing;
    }

    public void setFocusing(boolean focusing) {
        this.focusing = focusing;
    }

    public int getHoverMinDistance() {
        return hoverMinDistance;
    }

    public void setHoverMinDistance(int hoverMinDistance) {
        this.hoverMinDistance = hoverMinDistance;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }


    /**
     * 获取所有线段长度汇总后的中间点实际坐标
     *
     * @return
     */
    public HiPoint getCenterPointOfAllLine() {
        List<HiPoint> allpoints = getAllPointWithStartEnd();
        double totaldistance = 0;
        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < allpoints.size() - 1; i++) {
            HiPoint pp = allpoints.get(i);
            HiPoint np = allpoints.get(i + 1);
            double distance = LineUtils.distance(pp, np);
            distances.add(distance);
            totaldistance += distance;
        }
        double mp = totaldistance / 2;
        double accumulate = 0;
        int matchidx = distances.size() - 1;
        double offset = -1;
        for (int i = 0; i < distances.size(); i++) {
            Double clen = distances.get(i);
            if (mp > accumulate && mp <= accumulate + clen) {
                matchidx = i;
                offset = mp - accumulate;
                break;
            }
            accumulate += clen;
        }
        HiPoint cpLeft = allpoints.get(matchidx);
        HiPoint cpRight = allpoints.get(matchidx + 1);
        return LineUtils.calcCrossPointOfSpecificDistance(cpLeft, cpRight, offset);
    }

    /**
     * 给所有的点一定偏移量
     *
     * @param draggingOffsetX x偏移量
     * @param draggingOffsetY y轴偏移量
     */
    public void moveAllPointsWithOffset(double draggingOffsetX, double draggingOffsetY) {
        for (HiPoint point : getPoints()) {
            point.x += draggingOffsetX;
            point.y += draggingOffsetY;
        }
    }

    public String getIdPrefix() {
        return "line";
    }


    /**
     * 绘制线段的核心
     * 绘制标签的代码请参考如下
     *
     * @param chartPanel
     * @see cn.boz.jb.plugin.floweditor.gui.shape.Label#drawContent(cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel)
     */
    public void drawLine(ChartPanel chartPanel) {
        HiPoint endPoint = this.getEndPoint();
        chartPanel.setColor(ConstantUtils.getInstance().getChartPanelFlowLineFillColor());
        if (this.isHover()) {
            chartPanel.setColor(ConstantUtils.getInstance().getChartPanelFlowLineHoverColor());
        }
        chartPanel.markStroke();
        if (this.isSelected()) {
            chartPanel.setColor(ConstantUtils.getInstance().getChartPanelFlowLineActiveColor());
            //绘制虚线，以及中间点？
            float[] dash = {2.0f, 0f, 2.0f};
            BasicStroke basicStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
            chartPanel.setStroke(basicStroke);
        }
        //绘制线段
        List<HiPoint> allpoints = this.getAllPointWithStartEnd();
        double xs[] = new double[allpoints.size()];
        double ys[] = new double[allpoints.size()];
        for (int i = 0; i < allpoints.size(); i++) {
            xs[i] = allpoints.get(i).x;
            ys[i] = allpoints.get(i).y;
        }
        chartPanel.markColor();
        chartPanel.drawPolyline(xs, ys, allpoints.size());
        chartPanel.resetColor();
        chartPanel.resetStroke();

        //绘制控制点
        if (this.isSelected()) {
            List<HiPoint> points = this.getAllPointWithStartEnd();

            for (int i = 0; i < points.size() - 1; i++) {
                HiPoint lp = points.get(i);
                HiPoint rp = points.get(i + 1);
                //绘制中点
                HiPoint cp = new HiPoint((lp.x + rp.x) / 2, (lp.y + rp.y) / 2);
                chartPanel.fillOval(cp.x - Line.smallDotR, cp.y - Line.smallDotR, 2 * Line.smallDotR + 1, 2 * Line.smallDotR + 1);

            }
            for (HiPoint point : this.getPoints()) {
                chartPanel.fillOval(point.x - Line.bigDotR, point.y - Line.bigDotR, 2 * Line.bigDotR + 1, 2 * Line.bigDotR + 1);
            }
        }


        //计算与画箭头
        HiPoint crosspoint = this.calculateCrossDistancePoint(endPoint, (float) Line.TRIANGLE_DISTANCE);
        //wang
        HiPoint crosspointMinus = this.calculateCrossDistancePoint(endPoint, (float) Line.TRIANGLE_DISTANCE - 2);
        double[] abvs = LineUtils.calcAAndBValue(this.getEndShape().getCenterPoint(), this.getEndPoint());
        HiPoint[] triangleLeftRight = Line.calculateDistancePoint(crosspoint, (float) (Line.TRIANGLE_DISTANCE / 3), -abvs[1], abvs[0]);
        double xPoints[] = new double[]{endPoint.x, triangleLeftRight[0].x, crosspointMinus.x, triangleLeftRight[1].x};
        double yPoints[] = new double[]{endPoint.y, triangleLeftRight[0].y, crosspointMinus.y, triangleLeftRight[1].y};
        chartPanel.fillPolyline(xPoints, yPoints, 4);
    }


    /**
     * 初始化的必要操作
     *
     * @param startShape
     * @param endShape
     */
    public void init(Shape startShape, Shape endShape) {
        this.setStartShape(startShape);
        this.setEndShape(endShape);
        String name = "to " + endShape.getName();
        this.setName(name);
        Label label = new Label(0, 0, 0, 0, this.getName());
        this.setLabel(label);
        label.setBoundLine(this);
    }

    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        return PropertyObject.super.getPropertyEditors(propertyEditor);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Label cloneLabel = (Label) this.getLabel().clone();
        Line cloneLine = new Line();
        cloneLabel.setBoundLine(cloneLine);
        cloneLine.setName(this.getName());
        cloneLine.setLabel(cloneLabel);

        LinkedList<HiPoint> points = this.points;
        for (HiPoint point : points) {
            cloneLine.points.add((HiPoint) point.clone());
        }
        //startShape与endShape未知
        return cloneLine;
    }

    public void addXWithOffset(double diffx) {
        for (HiPoint point : this.points) {
            point.x += diffx;
            if (point.x <= 0) {
                point.x = 0;
            }
        }
    }

    public void addYWithOffset(double diffy) {
        for (HiPoint point : this.points) {
            point.y += diffy;
            if (point.y <= 0) {
                point.y = 0;
            }
        }
    }
}
