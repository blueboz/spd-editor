package cn.boz.jb.plugin.floweditor.gui.shape;

import cn.boz.jb.plugin.floweditor.gui.utils.LineUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

/**
 * 棱形
 */
public class Prismatic extends Shape {
    public static int POINT_EAST = 0;
    public static int POINT_WEST = 1;
    public static int POINT_NORTH = 2;
    public static int POINT_SOUTH = 3;

    public Prismatic() {

    }

    public Prismatic(double x, double y) {
        super(x, y);
    }

    public Prismatic(double x, double y, double width, double height) {
        super(x, y, width, height);
    }


    @Override
    public void drawContent(ChartPanel chartPanel) {
        HiPoint east = getPointOfPos(POINT_EAST);
        HiPoint south = getPointOfPos(POINT_SOUTH);
        HiPoint north = getPointOfPos(POINT_NORTH);
        HiPoint west = getPointOfPos(POINT_WEST);
        chartPanel.fillPolylineClose(east, south, west, north);
    }

    @Override
    public HiPoint calculateCrossPoint(HiPoint lineStartPoint, HiPoint lineEndPoint) {
        //计算与棱形的焦点
        //与四条边的分别交点
        //
        double dy = lineEndPoint.y - lineStartPoint.y;
        double dx = lineEndPoint.x - lineStartPoint.x;
        if (dy > 0 && dx > 0) {
            //第一象限SE    东南
            HiPoint sP = getPointOfPos(POINT_EAST);
            HiPoint eP = getPointOfPos(POINT_SOUTH);
            return LineUtils.calcCrossPointOfTwoLine(sP, eP, lineStartPoint, lineEndPoint);
        } else if (dx < 0 && dy > 0) {
            //第二象限SW    西南
            HiPoint sP = getPointOfPos(POINT_WEST);
            HiPoint eP = getPointOfPos(POINT_SOUTH);
            return LineUtils.calcCrossPointOfTwoLine(sP, eP, lineStartPoint, lineEndPoint);
        } else if (dx < 0 && dy < 0) {
            //第三象限NW    西北
            HiPoint sP = getPointOfPos(POINT_WEST);
            HiPoint eP = getPointOfPos(POINT_NORTH);
            return LineUtils.calcCrossPointOfTwoLine(sP, eP, lineStartPoint, lineEndPoint);
        } else if (dx > 0 && dy < 0) {
            //第四象限NE    东北
            HiPoint sP = getPointOfPos(POINT_EAST);
            HiPoint eP = getPointOfPos(POINT_NORTH);
            return LineUtils.calcCrossPointOfTwoLine(sP, eP, lineStartPoint, lineEndPoint);
        }
//        LineUtils.calcCrossAngleOfTwoLine()
        return super.calculateCrossPoint(lineStartPoint, lineEndPoint);
    }

    /**
     * 获取当前图形指定位置的点
     *
     * @param position POINT_EAST/POINT_SOUNTH/POINT_WEST/POINT_NORTH 当前类提供的枚举值
     * @return
     */
    private HiPoint getPointOfPos(int position) {
        Rect rect = new Rect(this.x, this.y, this.width, this.height);
        return getPointOfPosOfRect(position, rect);
    }

    /**
     * 获取图形指定位置的点
     *
     * @param position POINT_EAST/POINT_SOUNTH/POINT_WEST/POINT_NORTH 当前类提供的枚举值
     * @param rect
     * @return
     */
    private static HiPoint getPointOfPosOfRect(int position, Rect rect) {
        if (POINT_EAST == position) {
            return new HiPoint(rect.getX() + rect.getW(), rect.getY() + rect.getH() / 2);
        } else if (POINT_SOUTH == position) {
            return new HiPoint(rect.getX() + rect.getW() / 2, rect.getY() + rect.getH());
        } else if (POINT_WEST == position) {
            return new HiPoint(rect.getX(), rect.getY() + rect.getH() / 2);
        } else if (POINT_NORTH == position) {
            return new HiPoint(rect.getX() + rect.getW() / 2, rect.getY());
        }
        return null;
    }

    @Override
    public void drawNewShape(ChartPanel chartPanel, Rect rect) {
        HiPoint east = getPointOfPosOfRect(POINT_EAST, rect);
        HiPoint south = getPointOfPosOfRect(POINT_SOUTH, rect);
        HiPoint north = getPointOfPosOfRect(POINT_NORTH, rect);
        HiPoint west = getPointOfPosOfRect(POINT_WEST, rect);
        chartPanel.drawPolylineClose(east, south, west, north);
    }
}
