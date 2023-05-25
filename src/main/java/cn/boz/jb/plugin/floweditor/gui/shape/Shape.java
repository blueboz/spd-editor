package cn.boz.jb.plugin.floweditor.gui.shape;

import cn.boz.jb.plugin.floweditor.gui.control.Alignable;
import cn.boz.jb.plugin.floweditor.gui.control.Attachable;
import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.control.Resizable;
import cn.boz.jb.plugin.floweditor.gui.hist.BaseState;
import cn.boz.jb.plugin.floweditor.gui.hist.Restorable;
import cn.boz.jb.plugin.floweditor.gui.hist.ShapeState;
import cn.boz.jb.plugin.floweditor.gui.listener.ClickListener;
import cn.boz.jb.plugin.floweditor.gui.process.Gateway;
import cn.boz.jb.plugin.floweditor.gui.utils.ConstantUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.ShapePos;
import cn.boz.jb.plugin.floweditor.gui.utils.ShapeUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 图形组件
 */
public class Shape implements Restorable, Resizable, Alignable, Attachable, Cloneable, PropertyObject {
    protected String id;
    protected String name;
    //鼠标当前是否悬浮于图形上面
    private boolean cursorInside = false;

    public static int DOT_WIDTH = 4;
    public static int DOT_RANGE = 6;

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    private boolean hover = false;
    private boolean draging = false;
    private boolean resizing = false;

    //作为以下线段的头
    public List<Line> headOfLine = new ArrayList<>();
    //作为以下线段的尾
    public List<Line> tailOfLine = new ArrayList<>();
    //你可以设置颜色，不设置将采用默认的颜色


    //边框属性
    protected String borderStyle = "solid";
    protected int borderWidth = 0;
    protected Color borderColor = new Color(255, 0, 0);

    //鼠标悬浮的状态设置
    protected String hoverBorderStyle = "dashed";
    protected int hoverBoarderWidth = 1;
//    protected Color hoverBorderColor ;//= new Color(0, 0, 0, 255);

    //缩放的时候的属性
    protected String resizingBorderStyle = "dashed";
    protected int resizingBorderWidth = 1;

    //缩放的时候，控制点的颜色
    private List<ClickListener> clickListeners = new ArrayList<>();
    //鼠标击中的状态
    protected boolean mousePressing;
    //控件得到焦点的状态
    private boolean focusing;

    public Shape() {

    }


    public Shape(double x, double y) {
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 30;

    }

    public Shape(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    /**
     * 计算指定点与当前图形中心点之间的连线与本图形的四个边的相交点
     *
     * @param targetCenter
     * @return
     */
    public HiPoint calculateCrossPoint(HiPoint targetCenter) {
        return calculateCrossPoint(getCenterPoint(), targetCenter);
    }

    public HiPoint calculateCrossPoint(HiPoint lineStartPoint, HiPoint lineEndPoint) {
        return ShapeUtils.calculateCrossPointWithRect(toRect(), lineStartPoint, lineEndPoint);
    }

    /**
     * 点是否在图形上面
     *
     * @param point
     * @return
     */
    public boolean inside(HiPoint point) {
        //除此之外，还需要判断是是否在九个控制点中
        Rect rect = toRect();
        if (point.x - rect.getX() > 0 && point.x - rect.getX() < rect.getW() && point.y - rect.getY() > 0 && point.y - rect.getY() < rect.getH()) {
            return true;
        } else {
            return false;
        }

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
     * 获取图形的中心点
     *
     * @return
     */
    public HiPoint getCenterPoint() {
        Rect rect = this.toRect();
        return new HiPoint(rect.getX() + rect.getW() / 2, rect.getY() + rect.getH() / 2);
    }

    /**
     * 两根垂直线段,以某一条线段进行移动，移动距离为offset，offset可能为正也可能为负
     * 当左右两根的左被往左移动，或者被右移动，没有触碰到右边的线的时候，返回的是左+offset,右，
     * 当触碰并且超过右边的线时候，此时左+offset>右，返回的是右边,左边+offset
     * 如果移动的是没有任何线段，则无需处理，直接返回y引用的线段信息就好啦!
     *
     * @param type
     * @param offset
     * @param refLeft
     * @param refRight
     * @return
     */
    public static Change calcMoveLineWithOffsetAndType(MoveType type, double offset, double refLeft, double refRight) {
        //处理x的移动
        //δx=end.x-start.x
        //newx-oldx=δx=end.x-start.x
        //newx=δx+oldx=end.x-start.x+old.x
        double changeLeft = 0;
        double changeRight = 0;
        if (MoveType.LEFT.equals(type)) {
            double finalLeft = refLeft + offset;
            if (finalLeft < refRight) {
                changeLeft = finalLeft;
                changeRight = refRight;
            } else {
                changeLeft = refRight;
                changeRight = finalLeft;
            }
        } else if (MoveType.RIGHT.equals(type)) {
            double finalRight = refRight + offset;
            if (finalRight < refLeft) {
                changeLeft = finalRight;
                changeRight = refLeft;
            } else {
                changeRight = finalRight;
                changeLeft = refLeft;
            }
        } else {
            Change change = new Change();
            change.left = refLeft;
            change.right = refRight;
            return change;
        }
        Change change = new Change();
        change.left = changeLeft;
        change.right = changeRight;
        return change;
    }

    /**
     * 计算resize的时候的真正坐标 此坐标不能出现负宽度与高度
     *
     * @param resizingPos          resize的实际方位，用于区分是在哪个区域点击的
     * @param resizingCurrentPoint 重置
     * @param resizingStartPoint
     * @return
     */
    public static Rect calcResizing(ShapePos resizingPos, HiPoint resizingCurrentPoint, HiPoint resizingStartPoint, Rect rect) {
        double offsetx = resizingCurrentPoint.x - resizingStartPoint.x;
        double offsety = resizingCurrentPoint.y - resizingStartPoint.y;
        MoveType moveType = calcMovePosOfX(resizingPos);
        Change calcx = calcMoveLineWithOffsetAndType(moveType, offsetx, rect.getX(), rect.getX() + rect.getW());
        moveType = calcMovePosOfY(resizingPos);
        Change calcy = calcMoveLineWithOffsetAndType(moveType, offsety, rect.getY(), rect.getY() + rect.getH());
        if (resizingPos != ShapePos.MIDDLE_CENTER && resizingPos != ShapePos.NOT_INSIDE) {
            return new Rect(calcx.left, calcy.left, calcx.right - calcx.left, calcy.right - calcy.left);
        }
        return null;
    }

    /**
     * 计算图形中垂直于y轴的那条边被移动了
     *
     * @param resizingPos
     * @return
     */
    public static MoveType calcMovePosOfY(ShapePos resizingPos) {
        if (resizingPos == ShapePos.TOP_LEFT || resizingPos == ShapePos.TOP_CENTER || resizingPos == ShapePos.TOP_RIGHT) {
            return MoveType.LEFT;
        } else if (resizingPos == ShapePos.BOTTOM_LEFT || resizingPos == ShapePos.BOTTOM_CENTER || resizingPos == ShapePos.BOTTOM_RIGHT) {
            return MoveType.RIGHT;
        }
        return MoveType.NONE;
    }

    /**
     * 计算图形中垂直于x轴线到底那条被拖动了
     *
     * @param resizingPos
     * @return
     */
    public static MoveType calcMovePosOfX(ShapePos resizingPos) {
        if (resizingPos == ShapePos.BOTTOM_LEFT || resizingPos == ShapePos.MIDDLE_LEFT || resizingPos == ShapePos.TOP_LEFT) {
            return MoveType.LEFT;
        } else if (resizingPos == ShapePos.BOTTOM_RIGHT || resizingPos == ShapePos.MIDDLE_RIGHT || resizingPos == ShapePos.TOP_RIGHT) {
            return MoveType.RIGHT;
        }
        return MoveType.NONE;
    }

    /**
     * @param shapePos
     * @param rect
     * @return
     */
    public static Rect getResizeRect(ShapePos shapePos, Rect rect) {
        double x = 0;
        double y = 0;
        double w = Shape.DOT_WIDTH;
        double h = Shape.DOT_WIDTH;
        switch (shapePos) {
            case TOP_LEFT:
                x = rect.getX();
                y = rect.getY();
                break;
            case TOP_CENTER:
                x = rect.getX() + rect.getW() / 2;
                y = rect.getY();
                break;
            case TOP_RIGHT:
                x = rect.getX() + rect.getW();
                y = rect.getY();
                break;
            case MIDDLE_LEFT:
                x = rect.getX();
                y = rect.getY() + rect.getH() / 2;
                break;
            case MIDDLE_CENTER:
                x = rect.getX() + rect.getW() / 2;
                y = rect.getY() + rect.getH() / 2;
                break;
            case MIDDLE_RIGHT:
                x = rect.getX() + rect.getW();
                y = rect.getY() + rect.getH() / 2;
                break;
            case BOTTOM_LEFT:
                x = rect.getX();
                y = rect.getY() + rect.getH();
                break;
            case BOTTOM_CENTER:
                x = rect.getX() + rect.getW() / 2;
                y = rect.getY() + rect.getH();
                break;
            case BOTTOM_RIGHT:
                x = rect.getX() + rect.getW();
                y = rect.getY() + rect.getH();
                break;
            case NOT_INSIDE:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + shapePos);
        }
        if (shapePos != ShapePos.NOT_INSIDE) {
            x -= Shape.DOT_WIDTH / 2.0d;
            y -= Shape.DOT_WIDTH / 2.0d;
            return new Rect(x, y, w, h);
        } else {
            return null;
        }
    }


    /**
     * 图形应该如何绘制
     *
     * @param chartPanel
     */
    public void drawContent(ChartPanel chartPanel) {
        //根据不同的状态，修改背景透视的颜色
        Rect rect = this.toRect();
        chartPanel.fillRoundRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), 5, 5);

    }

    /**
     * 描边
     *
     * @param chartPanel
     * @param rect
     * @param bw
     * @param bstyle
     * @param bColor
     */
    private void drawBorder(ChartPanel chartPanel, Rect rect, int bw, String bstyle, Color bColor) {
        if (bw > 0) {
            chartPanel.markColor();
            chartPanel.markStroke();
            chartPanel.setColor(bColor);
            chartPanel.setStroke(ShapeUtils.getStoke(bw, bstyle));
            chartPanel.drawRoundBorder(rect.getX(), rect.getY(), rect.getW(), rect.getH(), 5, 5);
            chartPanel.resetStroke();
            chartPanel.resetColor();
        }
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
    private void drawInnerBorder(ChartPanel chartPanel, Shape shape, int bw, String bstyle, Color bColor) {
        if (bw > 0) {
            chartPanel.markColor();
            chartPanel.markStroke();
            chartPanel.setColor(bColor);
            chartPanel.setStroke(ShapeUtils.getStoke(bw, bstyle));
            chartPanel.drawRect(shape.x, shape.y, shape.width - 1, shape.height - 1);
            chartPanel.resetStroke();
            chartPanel.resetColor();
        }
    }

    /**
     * 将当前图形以rect形式表现
     *
     * @return
     */
    public Rect toRect() {
        return new Rect(this.x, this.y, this.width, this.height);
    }

    /**
     * 绘制图形的函数
     *
     * @param
     * @param chartPanel
     */
    public void drawShape(ChartPanel chartPanel) {
        chartPanel.markColor();
        //根据不同的状态，修改背景透视的颜色
        chartPanel.setColor(getBackgroundColor());
        this.drawContent(chartPanel);
        chartPanel.resetColor();
        if (this.hover || this.resizing) {
            //画8个小方块点
            //由于resize是一个动态的过程，所以，绘制九个点以及此虚线边框也是需要使用实时
            Rect shapeOfResizeing = this.toRect();
            if (this.resizing) {
                //绘制调整大小的该图形
                chartPanel.markColor();
                chartPanel.setColor(ConstantUtils.getInstance().getShapeResizingCtlDotColor());
                if (chartPanel.resizePressObj != null) {
                    shapeOfResizeing = Shape.calcResizing(chartPanel.resizePos, chartPanel.resizeCurrentPoint, chartPanel.resizeStartPoint, shapeOfResizeing);
                }
                //左上9个小点
                Rect tl = Shape.getResizeRect(ShapePos.TOP_LEFT, shapeOfResizeing);
                chartPanel.fillRect(tl.getX(), tl.getY(), tl.getW(), tl.getH());
                Rect tc = Shape.getResizeRect(ShapePos.TOP_CENTER, shapeOfResizeing);
                chartPanel.fillRect(tc.getX(), tc.getY(), tc.getW(), tc.getH());
                Rect tr = Shape.getResizeRect(ShapePos.TOP_RIGHT, shapeOfResizeing);
                chartPanel.fillRect(tr.getX(), tr.getY(), tr.getW(), tr.getH());

                Rect ml = Shape.getResizeRect(ShapePos.MIDDLE_LEFT, shapeOfResizeing);
                chartPanel.fillRect(ml.getX(), ml.getY(), ml.getW(), ml.getH());
                Rect mc = Shape.getResizeRect(ShapePos.MIDDLE_CENTER, shapeOfResizeing);
                chartPanel.fillRect(mc.getX(), mc.getY(), mc.getW(), mc.getH());
                Rect mr = Shape.getResizeRect(ShapePos.MIDDLE_RIGHT, shapeOfResizeing);
                chartPanel.fillRect(mr.getX(), mr.getY(), mr.getW(), mr.getH());

                Rect bl = Shape.getResizeRect(ShapePos.BOTTOM_LEFT, shapeOfResizeing);
                chartPanel.fillRect(bl.getX(), bl.getY(), bl.getW(), bl.getH());
                Rect bc = Shape.getResizeRect(ShapePos.BOTTOM_CENTER, shapeOfResizeing);
                chartPanel.fillRect(bc.getX(), bc.getY(), bc.getW(), bc.getH());
                Rect br = Shape.getResizeRect(ShapePos.BOTTOM_RIGHT, shapeOfResizeing);
                chartPanel.fillRect(br.getX(), br.getY(), br.getW(), br.getH());
                chartPanel.resetColor();
                if (!this.focusing) {
                    drawBorder(chartPanel, shapeOfResizeing, resizingBorderWidth, resizingBorderStyle, ConstantUtils.getInstance().getShapeResizingBorderColor());
                }
            } else {
                if (!this.focusing) {
                    drawBorder(chartPanel, shapeOfResizeing, hoverBoarderWidth, hoverBorderStyle, ConstantUtils.getInstance().getChartPanelHoverBorderColor());
                }
            }
        }
        this.drawAlign(chartPanel);
    }


    /**
     * 绘制对齐线
     *
     * @param chartPanel
     */
    private void drawAlign(ChartPanel chartPanel) {
        chartPanel.markColor();
        chartPanel.setColor(ConstantUtils.getInstance().getAlignLineColor());
        int vertical = align >> 8;
        int horizontal = align & 0xff;
        Rect rect = toRect();
        double left = rect.getX() - 10;
        double right = rect.getX() + rect.getW() + 10;
        if ((horizontal & Shape.REL_POS_LEFT_OUT) == Shape.REL_POS_LEFT_OUT) {
            double y = rect.getY();
            chartPanel.drawLine(left, y, right, y);
        }
        if ((horizontal & Shape.REL_POS_LEFT_IN) == Shape.REL_POS_LEFT_IN) {
            double y = rect.getY();
            chartPanel.drawLine(left, y, right, y);
        }
        if ((horizontal & Shape.REL_POS_CENTER) == Shape.REL_POS_CENTER) {
            double y = rect.getY() + rect.getH() / 2;
            chartPanel.drawLine(left, y, right, y);
        }
        if ((horizontal & Shape.REL_POS_RIGHT_IN) == Shape.REL_POS_RIGHT_IN) {
            double y = rect.getY() + rect.getH();
            chartPanel.drawLine(left, y, right, y, 0, -1, 0, -1);
        }
        if ((horizontal & Shape.REL_POS_RIGHT_OUT) == Shape.REL_POS_RIGHT_OUT) {
            double y = rect.getY() + rect.getH();
            chartPanel.drawLine(left, y, right, y, 0, -1, 0, -1);
        }

        double top = rect.getY() - 10;
        double bottom = rect.getY() + rect.getH() + 10;

        if ((vertical & Shape.REL_POS_LEFT_OUT) == Shape.REL_POS_LEFT_OUT) {
            double x = rect.getX();
            chartPanel.drawLine(x, top, x, bottom);
        }
        if ((vertical & Shape.REL_POS_LEFT_IN) == Shape.REL_POS_LEFT_IN) {
            double x = rect.getX();
            chartPanel.drawLine(x, top, x, bottom);
        }
        if ((vertical & Shape.REL_POS_CENTER) == Shape.REL_POS_CENTER) {
            double x = rect.getX() + rect.getW() / 2;
            chartPanel.drawLine(x, top, x, bottom);
        }
        if ((vertical & Shape.REL_POS_RIGHT_IN) == Shape.REL_POS_RIGHT_IN) {
            double x = rect.getX() + rect.getW();
            chartPanel.drawLine(x, top, x, bottom, -1, 0, -1, 0);
        }
        if ((vertical & Shape.REL_POS_RIGHT_OUT) == Shape.REL_POS_RIGHT_OUT) {
            double x = rect.getX() + rect.getW();
            chartPanel.drawLine(x, top, x, bottom, -1, 0, -1, 0);
        }
        chartPanel.resetColor();
    }


    @Override
    public String toString() {
        return "Shape{" +
                "cursorInside=" + cursorInside +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", hover=" + hover +
                ", draging=" + draging +
                ", resizing=" + resizing +
                ", focusing=" + focusing +
                '}';
    }

    /**
     * 添加点击事件处理
     *
     * @param listener
     */
    public void addClickListener(ClickListener listener) {
        clickListeners.add(listener);
    }

    public boolean onClick(MouseEvent e) {
        return false;
    }


    public Boolean mousePressed(MouseEvent e) {
        mousePressing = true;
        return true;
    }

    public boolean mouseReleased(MouseEvent e) {
        mousePressing = false;
        return true;
    }

    /**
     * 控件得到焦点的事件
     *
     * @param e
     * @return
     */
    public boolean focus(MouseEvent e) {
        setResizing(true);
        this.draging = true;
        focusing = true;
        //用于控制控件聚焦状态,主要是用于控件
        return true;
    }

    public boolean blur(MouseEvent e) {
        setResizing(false);
        this.draging = false;
        this.focusing = false;
        return true;
    }


    /**
     * 判断两个值是否约等于，allowdiff
     *
     * @param v1        值1
     * @param v2        值2
     * @param allowdiff 允许的误差
     * @return
     */
    public static boolean aboutTo(double v1, double v2, double allowdiff) {
        if (absdiff(v1, v2) <= allowdiff) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个值是否约等于，allowdiff
     *
     * @param v1 值1
     * @param v2 值2
     * @return
     */
    public static double absdiff(double v1, double v2) {
        return Math.abs(v1 - v2);
    }

    public static final int REL_POS_NAIL = 0x0000;
    public static final int REL_POS_LEFT_OUT = 0x0001;
    public static final int REL_POS_LEFT_IN = 0x0002;
    public static final int REL_POS_CENTER = 0x0004;
    public static final int REL_POS_RIGHT_IN = 0x0008;
    public static final int REL_POS_RIGHT_OUT = 0x0010;

    public static double alignHorizontal(int pos, Rect rel, Rect move) {
        if (REL_POS_LEFT_OUT == pos) {
            return rel.getY();
        } else if (REL_POS_LEFT_IN == pos) {
            return rel.getY();
        } else if (REL_POS_CENTER == pos) {
            return rel.getY() + rel.getH() / 2;
        } else if (REL_POS_RIGHT_IN == pos) {
            return rel.getY() + rel.getH();
        } else if (REL_POS_RIGHT_OUT == pos) {
            return rel.getY() + rel.getH();
        }
        return move.getX();
    }

    public static double alignVertical(int pos, Rect rel, Rect move) {
        if (REL_POS_LEFT_OUT == pos) {
            return rel.getX();
        } else if (REL_POS_LEFT_IN == pos) {
            return rel.getX();
        } else if (REL_POS_CENTER == pos) {
            return rel.getX() + rel.getW() / 2;
        } else if (REL_POS_RIGHT_IN == pos) {
            return rel.getX() + rel.getW();
        } else if (REL_POS_RIGHT_OUT == pos) {
            return rel.getX() + rel.getW();
        }
        return move.getX();
    }

    public static double targetPosOfX(int pos, Rect rel, Rect move) {
        if (REL_POS_LEFT_OUT == pos) {
            return rel.getX() - move.getW();
        } else if (REL_POS_LEFT_IN == pos) {
            return rel.getX();
        } else if (REL_POS_CENTER == pos) {
            return rel.getX() + rel.getW() / 2 - move.getW() / 2;
        } else if (REL_POS_RIGHT_IN == pos) {
            return rel.getX() + rel.getW() - move.getW();
        } else if (REL_POS_RIGHT_OUT == pos) {
            return rel.getX() + rel.getW();
        }
        return move.getX();
    }

    /**
     * 最终坐标
     *
     * @param pos  位置
     * @param rel  相对对象
     * @param move 移动位置
     * @return
     */
    public static double targetPosOfY(int pos, Rect rel, Rect move) {
        if (REL_POS_LEFT_OUT == pos) {
            return rel.getY() - move.getH();
        } else if (REL_POS_LEFT_IN == pos) {
            return rel.getY();
        } else if (REL_POS_CENTER == pos) {
            return rel.getY() + rel.getH() / 2 - move.getH() / 2;
        } else if (REL_POS_RIGHT_IN == pos) {
            return rel.getY() + rel.getH() - move.getH();
        } else if (REL_POS_RIGHT_OUT == pos) {
            return rel.getY() + rel.getH();
        }
        return move.getY();
    }

    /**
     * y固定，与y轴垂直的线段
     *
     * @param rel           被参照的图形
     * @param moveRect      移动中的图形
     * @param alignDistance 允许的最小触碰距离
     * @return
     */
    public static int relPosOfY(Rect rel, Rect moveRect, int alignDistance) {
        double relvl = rel.getY();
        double relvr = rel.getY() + rel.getH();
        double movevl = moveRect.getY();
        double movevr = moveRect.getY() + moveRect.getH();
        int i = relPosOfRange(alignDistance, relvl, relvr, movevl, movevr);
        if (i != Shape.REL_POS_NAIL) {
            //非中间对齐

            int i1 = leastPos(i, relvl, relvr, movevl, movevr);
            return i1;
        }
        return i;

    }

    /**
     * x固定的线段， 与x轴垂直的
     *
     * @param rel           被相对的对象
     * @param moveRect      移动中的图形
     * @param alignDistance 允许的最小触碰距离
     * @return
     */
    public static int relPosOfX(Rect rel, Rect moveRect, int alignDistance) {
        double relhl = rel.getX();
        double relhr = rel.getX() + rel.getW();
        double movehl = moveRect.getX();
        double movehr = moveRect.getX() + moveRect.getW();
        int i = relPosOfRange(alignDistance, relhl, relhr, movehl, movehr);
        if (i != Shape.REL_POS_NAIL) {
            return leastPos(i, relhl, relhr, movehl, movehr);
        }
        return i;
    }

    /**
     * x固定的线段， 与x轴垂直的
     *
     * @param rel      被相对的对象
     * @param moveRect 移动中的图形
     * @return
     */
    public static double leastDistanceVertical(Rect rel, Rect moveRect) {
        double relhl = rel.getX();
        double relhr = rel.getX() + rel.getW();
        double movehl = moveRect.getX();
        double movehr = moveRect.getX() + moveRect.getW();
        return leastDistance(relhl, relhr, movehl, movehr);
    }

    /**
     * y固定，与y轴垂直的线段
     *
     * @param rel      被参照的图形
     * @param moveRect 移动中的图形
     * @return
     */
    public static double leastDistanceHorizontal(Rect rel, Rect moveRect) {
        double relvl = rel.getY();
        double relvr = rel.getY() + rel.getH();
        double movevl = moveRect.getY();
        double movevr = moveRect.getY() + moveRect.getH();
        return leastDistance(relvl, relvr, movevl, movevr);
    }


    /**
     * 计算两个线组
     *
     * @param alignDistance 允许的误差范围
     * @param relLeft       被参照对象的左边界
     * @param relRight      被参照对象的右边界
     * @param moveLeft      移动对象的左边界
     * @param moveRight     移动对象的右边界
     * @return
     */
    private static int relPosOfRange(int alignDistance, double relLeft, double relRight, double moveLeft, double moveRight) {
        int result = 0;
        if (aboutTo(relLeft, moveRight, alignDistance)) {
            //左外
            result |= REL_POS_LEFT_OUT;
        }
        if (aboutTo(relLeft, moveLeft, alignDistance)) {
            //左内
            result |= REL_POS_LEFT_IN;
        }
        if (aboutTo((relLeft + relRight) / 2, (moveLeft + moveRight) / 2, alignDistance)) {
            //中间
            result |= REL_POS_CENTER;
        }
        if (aboutTo(relRight, moveRight, alignDistance)) {
            //右外
            result |= REL_POS_RIGHT_IN;
        }
        if (aboutTo(relRight, moveLeft, alignDistance)) {
            //右内
            result |= REL_POS_RIGHT_OUT;
        }
        return result;
    }

    /**
     * 计算两个线组
     *
     * @param relLeft   被参照对象的左边界
     * @param relRight  被参照对象的右边界
     * @param moveLeft  移动对象的左边界
     * @param moveRight 移动对象的右边界
     * @return
     */
    private static double leastDistance(double relLeft, double relRight, double moveLeft, double moveRight) {
        double leastDiff = Double.MAX_VALUE;
        double li = absdiff(relLeft, moveRight); //左外
        if (li < leastDiff) {
            leastDiff = li;
        }
        double lo = absdiff(relLeft, moveLeft); //左内
        if (lo < leastDiff) {
            leastDiff = lo;
        }
        double md = absdiff((relLeft + relRight) / 2, (moveLeft + moveRight) / 2); //中间
        if (md < leastDiff) {
            leastDiff = md;
        }
        double ro = absdiff(relRight, moveLeft);//右外
        if (ro < leastDiff) {
            leastDiff = ro;
        }
        double ri = absdiff(relRight, moveRight);//右内
        if (ri < leastDiff) {
            leastDiff = ri;
        }
        return leastDiff;
    }

    /**
     * 只返回两个最小的路程
     *
     * @param relLeft   被参照对象的左边界
     * @param relRight  被参照对象的右边界
     * @param moveLeft  移动对象的左边界
     * @param moveRight 移动对象的右边界
     * @return
     */
    private static int leastPos(int i, double relLeft, double relRight, double moveLeft, double moveRight) {
        //非中间对齐
        double least = 999;
        int result = 0;
        //计算里面最小距离者
        if ((i & Shape.REL_POS_CENTER) != Shape.REL_POS_NAIL) {
            double md = absdiff((relLeft + relRight) / 2, (moveLeft + moveRight) / 2); //中间
            if (md < least) {
                result |= REL_POS_CENTER;
            }
        }
        if ((i & Shape.REL_POS_LEFT_IN) != Shape.REL_POS_NAIL) {
            double li = absdiff(relLeft, moveRight); //左外
            if (li < least) {
                result |= REL_POS_LEFT_IN;
            }
        }
        if ((i & Shape.REL_POS_LEFT_OUT) != Shape.REL_POS_NAIL) {
            double lo = absdiff(relLeft, moveLeft); //左内
            if (lo < least) {
                result |= REL_POS_LEFT_OUT;
            }
        }
        if ((i & Shape.REL_POS_RIGHT_IN) != Shape.REL_POS_NAIL) {
            double ri = absdiff(relRight, moveRight);//右内
            if (ri < least) {
                result |= REL_POS_RIGHT_IN;
            }
        }
        if ((i & Shape.REL_POS_RIGHT_OUT) != Shape.REL_POS_NAIL) {
            double ro = absdiff(relRight, moveLeft);//右外
            if (ro < least) {
                result |= REL_POS_RIGHT_OUT;
            }
        }
        return result;
    }

    @Override
    public BaseState serialize() {
        ShapeState shapeState = new ShapeState();
        shapeState.x = this.x;
        shapeState.y = this.y;
        shapeState.w = this.width;
        shapeState.h = this.height;
        shapeState.operated = this;
        return shapeState;
    }

    @Override
    public void restore(BaseState state) {
        ShapeState shapeState = (ShapeState) state;
        this.x = shapeState.x;
        this.y = shapeState.y;
        this.width = shapeState.w;
        this.height = shapeState.h;
    }

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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getLeft() {
        return this.x;
    }

    public double getTop() {
        return this.y;
    }

    public double getRight() {
        return this.x + this.width;
    }

    public double getBottom() {
        return this.y + this.height;
    }

    public void addXWithOffset(double offset) {
        this.x += offset;
        if (x <= 0) {
            x = 0;
        }
    }

    public void addYWithOffset(double offset) {
        this.y += offset;
        if (y <= 0) {
            y = 0;
        }
    }

    public boolean isHover() {
        return hover;
    }

    public void setHover(boolean hover) {
        this.hover = hover;
    }

    public boolean isDraging() {
        return draging;
    }

    public void setDraging(boolean draging) {
        this.draging = draging;
    }

    public boolean isResizing() {
        return resizing;
    }

    public void setResizing(boolean resizing) {
        if (resizable()) {
            this.resizing = resizing;
        }
    }

    public boolean isFocusing() {
        return focusing;
    }

    public void setFocusing(boolean focusing) {
        this.focusing = focusing;
    }

    /**
     * 获取控件的填充颜色
     *
     * @return
     */
    public Color getForegroundColor() {
        if (this.draging || this.resizing) {
            return ConstantUtils.getInstance().getShapeActiveForegroundColor();
        } else {
            return ConstantUtils.getInstance().getShapeForegroundColor();
        }
    }


    /**
     * 获取控件的填充颜色
     *
     * @return
     */
    public Color getBackgroundColor() {
        if (this.draging || this.resizing) {
            return ConstantUtils.getInstance().getShapeActiveBackgroundColor();
        } else {
            return ConstantUtils.getInstance().getShapeBackgroundColor();
        }
    }

    public boolean isMousePressing() {
        return mousePressing;
    }

    public void setMousePressing(boolean mousePressing) {
        this.mousePressing = mousePressing;
    }

    public void drawDragging(ChartPanel chartPanel, double draggingOffsetX, double draggingOffsetY) {
        Rect rect = toRect();
        chartPanel.drawRectInnerBorder(rect.getX() + draggingOffsetX < 0 ? 0 : rect.getX() + draggingOffsetX, rect.getY() + draggingOffsetY < 0 ? 0 : rect.getY() + draggingOffsetY, rect.getW(), rect.getH());

    }

    //0x0000 0000
    //前四位存储垂直对齐，后四位存储水平对齐
    private int align = 0x00000000;

    @Override
    public boolean resizable() {
        return false;
    }

    @Override
    public void setAlign(int relpos) {
        if ((relpos & REL_HORIZONTAL) == REL_HORIZONTAL) {
            //水平对齐
            align |= relpos ^ REL_HORIZONTAL;
        } else if ((relpos & REL_VERTICAL) == REL_VERTICAL) {
            //垂直对齐
            align |= (relpos ^ REL_VERTICAL) << 8;
        }
    }

    @Override
    public void resetAlign() {
        align &= 0;
    }

    @Override
    public boolean alignRefAble() {
        return true;
    }

    @Override
    public boolean alignSelf() {
        return false;
    }

    public int getAlign() {
        return align;
    }


    @Override
    public boolean attachable() {
        return true;
    }

    /**
     * 设置图形的坐标,一般是x对应x,y对应y,w对应w,h对应高度
     *
     * @param rect
     */
    public void setBoundsTo(Rect rect) {
        this.setX(rect.getX());
        this.setY(rect.getY());
        this.setWidth(rect.getW());
        this.setHeight(rect.getH());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Shape shape = new Shape(this.x, this.y, this.width, this.height);

        shape.setName(this.name);
        return shape;
    }

    /**
     * 供外部使用的绘制棱形
     *
     * @param chartPanel
     * @param rect
     */
    public void drawNewShape(ChartPanel chartPanel, Rect rect) {
        chartPanel.drawRect(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    public String indicatorFont(){
        return  IcoMoonUtils.getCircle();
    }

    /**
     * 只是绘制指示器有用，有需要请自己重写
     * @param chartPanel
     */
    public void drawIndicator(ChartPanel chartPanel, Point point){
        chartPanel.markFont();
        chartPanel.setColor(new Color(255,255,255,100));
        Shape.this.drawContent(chartPanel);

        chartPanel.resetFont();
    }



    /**
     * 提供图形的初始化操作
     *
     * @param hiPoint
     */
    public void init(HiPoint hiPoint) {
        this.setX(hiPoint.x);
        this.setY(hiPoint.y);
        this.setWidth(105);
        this.setHeight(55);
    }

    /**
     * 提供图形初始化使用，一般在新建图形的时候会执行图形的初始化功能
     *
     * @param rect
     */
    public void init(Rect rect) {
        this.setX(rect.getX());
        this.setY(rect.getY());
        this.setWidth(rect.getW());
        this.setHeight(rect.getW());
    }

    public String getIdPrefix() {
        return "shape";
    }

    /**
     * 控件本身是否可见取决于控件本身
     *
     * @return
     */
    public boolean visiable() {
        if (this instanceof Label) {
            int labelShowMode = ConstantUtils.getInstance().getLabelShowMode();
            if (labelShowMode == ConstantUtils.LABEL_SHOW_FULL) {
                return true;
            } else if (labelShowMode == ConstantUtils.LABEL_SHOW_ONLY_GATEWAY) {
                Label lb = (Label) this;
                Line boundLine = lb.getBoundLine();
                Shape startShape = boundLine.getStartShape();
                if (startShape instanceof Gateway) {
                    return true;
                }
                return false;
            } else if (labelShowMode == ConstantUtils.LABEL_SHOW_NONE) {
                return false;
            }
            return false;
        } else {
            return true;
        }
    }


}

class Change {
    public double left;
    public double right;
}
