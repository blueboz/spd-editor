package cn.boz.jb.plugin.floweditor.gui.utils;

import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;

import java.awt.BasicStroke;

import static cn.boz.jb.plugin.floweditor.gui.utils.ShapePos.*;

public class ShapeUtils {

    /**
     * 给定线段，计算与给定图形的相交点坐标
     * @param rect 给定矩形
     * @param lineStartPoint 线段起始点坐标
     * @param lineEndPoint 线段终止点坐标
     * @return
     */
    public static HiPoint calculateCrossPointWithRect(Rect rect, HiPoint lineStartPoint, HiPoint lineEndPoint) {
        //Ax+bx+c=0
        //a=(y0-y1)
        //b=(x0-x1)
        //c=-(Ax+By)
        double a = lineStartPoint.y - lineEndPoint.y;
        double b = lineEndPoint.x - lineStartPoint.x;
        double c = -(a * lineStartPoint.x + b * lineStartPoint.y);
        double x=rect.getX();
        double y=rect.getY();
        double width=rect.getW();
        double height=rect.getH();
        //区域范围
        //target center
        if (b != 0.0f) {
            //左边线
            // x=x1     y[y1,y1+h]
            //ax+by+c=0
            //
            double crossy = -(c + a * x) / b;
            if (crossy >= y && crossy < y + height) {
                if (lineStartPoint.x > lineEndPoint.x) {
                    return new HiPoint(x, crossy);
                }
            }
            //在右边的
            //右边线
            // x=x1+w   y[y1,y1+h]
            crossy = -(c + a * (x + width)) / b;
            if (crossy > y && crossy <= y + height) {
                if (lineStartPoint.x <= lineEndPoint.x) {
                    return new HiPoint(x + width, crossy);
                }
            }
        }
        if (a != 0.0f) {
            //上边线
            // y=y1     x[x1,x1+w]
            double crossx = -(b * y + c) / a;
            if (crossx > x && crossx <= x + width) {
                if (lineStartPoint.y > lineEndPoint.y) {
                    return new HiPoint(crossx, y);
                }
            }
            crossx = -(b * (y + height) + c) / a;
            if (crossx >= x && crossx < x + width) {
                if (lineStartPoint.y < lineEndPoint.y) {
                    return new HiPoint(crossx, y + height);
                }
            }
        }
        //下边线
        return  new HiPoint(x, y + height);
    }
    /**
     * 判断是否在小点区域范围内
     *
     * @param point
     * @return
     */
    public static ShapePos insideDot(HiPoint point, Rect rect) {
        //x轴正确
        //改变形状
        if (insideDot(point, new HiPoint(rect.getX(), rect.getY()))) {
            //左上
            return TOP_LEFT;
        } else if (insideDot(point, new HiPoint(rect.getX(), rect.getY() + rect.getH() / 2))) {
            //左中
            return MIDDLE_LEFT;
        } else if (insideDot(point, new HiPoint(rect.getX(), rect.getY() + rect.getH()))) {
            //左下
            return BOTTOM_LEFT;
        } else if (insideDot(point, new HiPoint(rect.getX() + rect.getW() / 2, rect.getY()))) {
            //中上
            return TOP_CENTER;
        } else if (insideDot(point, new HiPoint(rect.getX() + rect.getW() / 2, rect.getY() + rect.getH() / 2))) {
            //中中
            return MIDDLE_CENTER;
        } else if (insideDot(point, new HiPoint(rect.getX() + rect.getW() / 2, rect.getY() + rect.getH()))) {
            //中下
            return BOTTOM_CENTER;
        } else if (insideDot(point, new HiPoint(rect.getX() + rect.getW(), rect.getY()))) {
            //右上
            return TOP_RIGHT;
        } else if (insideDot(point, new HiPoint(rect.getX() + rect.getW(), rect.getY() + rect.getH() / 2))) {
            //右中
            return MIDDLE_RIGHT;
        } else if (insideDot(point, new HiPoint(rect.getX() + rect.getW(), rect.getY() + rect.getH()))) {
            //右下
            return BOTTOM_RIGHT;
        }
        return NOT_INSIDE;
    }

    private static boolean insideDot(HiPoint point, HiPoint dot) {
        if (point.x - (dot.x - Shape.DOT_RANGE / 2) > 0 && point.x - (dot.x + Shape.DOT_RANGE / 2) < 0) {
            if (point.y - (dot.y - Shape.DOT_RANGE / 2) > 0 && point.y - (dot.y + 2) < Shape.DOT_RANGE / 2) {
                //标识在左上角
                return true;
            }
        }
        return false;
    }

    public static BasicStroke getStoke(int strokeWidth, String type) {
        if ("solid".equals(type)) {
            return new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1f,0f}, 0f);
        } else if ("dashed".equals(type)) {
            return new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2f, 0f, 2f}, 0f);
        } else if ("dotted".equals(type)) {
            return new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1f, 0f, 1f}, 0f);
        }
        return null;
    }

}
