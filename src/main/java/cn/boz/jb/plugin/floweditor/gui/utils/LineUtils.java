package cn.boz.jb.plugin.floweditor.gui.utils;

import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;

public class LineUtils {

    /**
     * 给定线段的起点与终点，然后，给定某个点到起点的距离，求对应某个点的坐标
     *
     * @param left   起始点
     * @param right  终止点
     * @param offset 偏移起始点的偏移量
     * @return
     */
    public static HiPoint calcCrossPointOfSpecificDistance(HiPoint left, HiPoint right, double offset) {
        //(x2-x1)²+(y2-y1)²=t²
        //(x1-x)²-(y1-y)²=distance²
        //(x2-x)²-(y2-y)²=(t-distance)²
        //求解x ,y,由此来看是比较复杂的，并且难以理解
        //使用几何的解析方法
        //δx=x2-x1
        //δy=y2-y1
        //d/(δx²+δy²)^½*δx=δx'
        //d/(δx²+δy²)^½*δy=δy'
        double deltax = right.x - left.x;
        double deltay = right.y - left.y;
        double offsetx = offset / Math.sqrt(deltax * deltax + deltay * deltay) * deltax;
        double offsety = offset / Math.sqrt(deltax * deltax + deltay * deltay) * deltay;
        return new HiPoint(left.x + offsetx, left.y + offsety);
    }

    /**
     * 计算两个线段的夹角以p1->p2为指向
     *
     * @param l1p1
     * @param l1p2
     * @param l2p1
     * @param l2p2
     */
    public static double calcCrossAngleOfTwoLine(HiPoint l1p1, HiPoint l1p2, HiPoint l2p1, HiPoint l2p2) {
        double l1x = l1p2.x - l1p1.x;
        double l1y = l1p2.y - l1p1.y;
        double l2x = l2p2.x - l2p1.x;
        double l2y = l2p2.y - l2p1.y;
        double ab = l1x * l2x + l1y * l2y;
        double absa = Math.sqrt(l1x * l1x + l1y * l1y);
        double absb = Math.sqrt(l2x * l2x + l2y * l2y);
        double acos = Math.acos(ab / (absa * absb));
        return acos;
    }

    /**
     * 返回的是一个轴的向量,正值表示正方向
     *
     * @param l1p1
     * @param l1p2
     * @param l2p1
     * @param l2p2
     * @return a1a2 x a3a4
     * (x1,y1) x (x2,y2)
     * =x1*y2-y1*x2 z
     */
    public static double calcCrossDirectionOfTwoLine(HiPoint l1p1, HiPoint l1p2, HiPoint l2p1, HiPoint l2p2) {
        double l1x = l1p2.x - l1p1.x;
        double l1y = l1p2.y - l1p1.y;
        double l2x = l2p2.x - l2p1.x;
        double l2y = l2p2.y - l2p1.y;
        return l1x * l2y - l1y * l2x;
    }


    /**
     * 计算线段与椭圆的交点坐标
     *
     * @param lineStartPoint        线段起点
     * @param lineEndPoint          线段终点
     * @param ovelCenter            椭圆中心点
     * @param horizontalWidthOfOvel 椭圆水平宽度
     * @param verticalWidthOfOvel   椭圆垂直高度
     * @return
     */
    public static HiPoint calcCrossPointWithOvel(HiPoint lineStartPoint, HiPoint lineEndPoint, HiPoint ovelCenter, double horizontalWidthOfOvel, double verticalWidthOfOvel) {
        double[] sincos = LineUtils.calcCrossAngleWithXAxis(lineStartPoint, lineEndPoint);
        if (sincos[1] == 0 || sincos[0] == 0) {
            //任意轴垂直均可使用此公式
            return new HiPoint(ovelCenter.x + horizontalWidthOfOvel / 2 * sincos[1], ovelCenter.y + verticalWidthOfOvel / 2 * sincos[0]);
        } else {
            //椭圆与线段的交点坐标求解公式
            double a = horizontalWidthOfOvel / 2;
            double b = verticalWidthOfOvel / 2;
            double dx = a * b / Math.sqrt(b * b + a * a * (sincos[0] * sincos[0]) / (sincos[1] * sincos[1]));
            double dy = a * b * Math.abs(sincos[0] / sincos[1]) / Math.sqrt(b * b + a * a * (sincos[0] * sincos[0] / (sincos[1] * sincos[1])));
            //偏移量与圆中心指向外部方向正余弦值保持一致以及
            dx = dx * (sincos[1] > 0 ? 1 : -1);
            dy = dy * (sincos[0] > 0 ? 1 : -1);
            return new HiPoint(ovelCenter.x + dx, ovelCenter.y + dy);
        }
    }

    /**
     * 计算线段与x轴的夹角sin(θ) 与 cos(θ) 第一个返回值是sin
     *
     * @param startPoint 起始点
     * @param endPoint   结束点
     */
    public static double[] calcCrossAngleWithXAxis(HiPoint startPoint, HiPoint endPoint) {
        double x = endPoint.x - startPoint.x;
        double y = endPoint.y - startPoint.y;
        //在第一象限x>0,y>0 cos(θ)>0
        //在第二象限x<0,y>0 cos(θ)<0
        //在第三象限x<0,y<0 cos(θ)<0  问题
        //在第四象限x>0,y<0 cos(θ)>0  问题
        double r = Math.sqrt(x * x + y * y);
        double sin = y / r;
        double cos = x / r;
        return new double[]{sin, cos};
    }


    public static double transferToDeg(double pieDeg) {
        return pieDeg * 180 / Math.PI;
    }

    /**
     * 计算两个线段的交点
     *
     * @param l1p1 线段一
     * @param l1p2 线段二
     * @param l2p1 线段三
     * @param l2p2 线段四
     * @return
     */
    public static HiPoint calcCrossPointOfTwoLine(HiPoint l1p1, HiPoint l1p2, HiPoint l2p1, HiPoint l2p2) {
        double[] line1abc = LineUtils.calcABAndCValue(l1p1, l1p2);
        double[] line2abc = LineUtils.calcABAndCValue(l2p1, l2p2);
        double a1 = line1abc[0];
        double b1 = line1abc[1];
        double c1 = line1abc[2];

        double a2 = line2abc[0];
        double b2 = line2abc[1];
        double c2 = line2abc[2];
        double x = (b2 * c1 - b1 * c2) / (a2 * b1 - b2 * a1);
        if (b1 != 0) {
            double y = -(a1 * x + c1) / b1;
            return new HiPoint(x, y);
        } else {
            if (b2 != 0) {
                double y = -(a2 * x + c2) / b2;
                return new HiPoint(x, y);
            } else {
                System.out.println(String.format(
                        "a1:%f a2:%f a3:%f b1:%f b2:%f b3:%f startPoint(%f,%f) endPoint(%f,%f)"
                        , a1, b1, c1, a2, b2, c2, l1p1.x, l1p1.y, l2p1.x, l2p2.y));
                throw new RuntimeException("why b1 and b2 ==0");
            }
        }
    }

    /**
     * 计算给定线段的a b 值
     * 根据点斜式进行计算
     *
     * @param p1 起始点
     * @param p2 终止点
     * @return
     */
    public static double[] calcAAndBValue(HiPoint p1, HiPoint p2) {
        double a = p1.y - p2.y;
        double b = p2.x - p1.x;
        return new double[]{a, b};
    }


    /**
     * 计算指定线段的a b c值
     * 根据点斜式进行计算
     * Ax+By+c=0;
     *
     * @param p1 起始点
     * @param p2 终止点
     * @return
     */
    public static double[] calcABAndCValue(HiPoint p1, HiPoint p2) {
        double a = p1.y - p2.y;
        double b = p2.x - p1.x;
        double c = -(a * p1.x + b * p1.y);
        return new double[]{a, b, c};
    }

    /**
     * 两点之间的
     *
     * @param p1 点一
     * @param p2 点二
     * @return
     */
    public static double distance(HiPoint p1, HiPoint p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * 给定一个图形的前后左右边线，计算图形中到底哪根线被移动了
     *
     * @param beforeLeft  移动前左边线
     * @param beforeRight 移动前右边线
     * @param afterLeft   移动后左边线
     * @param afterRight  移动后右边线
     * @return
     */
    public static Double calcOfMoveingLine(double beforeLeft, double beforeRight, double afterLeft, double afterRight) {
        if (beforeLeft == afterLeft && beforeRight != afterRight) {
            return afterRight;
        } else if (beforeRight == afterRight && beforeLeft != afterLeft) {
            return afterLeft;
        } else if (beforeLeft == afterRight && beforeRight != afterLeft) {
            return afterLeft;
        } else if (beforeRight == afterLeft && afterRight != beforeLeft) {
            return afterRight;
        }
        return null;
    }


}
