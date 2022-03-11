package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.control.Alignable;
import cn.boz.jb.plugin.floweditor.gui.control.FlowSqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.events.ShapeSelectedEvent;
import cn.boz.jb.plugin.floweditor.gui.file.TemplateLoaderImpl;
import cn.boz.jb.plugin.floweditor.gui.hist.BaseGroupState;
import cn.boz.jb.plugin.floweditor.gui.hist.BaseState;
import cn.boz.jb.plugin.floweditor.gui.hist.LineState;
import cn.boz.jb.plugin.floweditor.gui.hist.ShapeState;
import cn.boz.jb.plugin.floweditor.gui.hist.StateChange;
import cn.boz.jb.plugin.floweditor.gui.listener.ShapeSelectedListener;
import cn.boz.jb.plugin.floweditor.gui.process.definition.ProcessDefinition;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.MoveType;
import cn.boz.jb.plugin.floweditor.gui.shape.PointLink;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.shape.Size;
import cn.boz.jb.plugin.floweditor.gui.utils.ConstantUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.LineUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.NumberUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.ShapePos;
import cn.boz.jb.plugin.floweditor.gui.utils.ShapeUtils;
import cn.boz.jb.plugin.idea.action.FindInSpdEditor;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.listener.ChartChangeListener;
import cn.boz.jb.plugin.idea.listener.ProcessSaveListener;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.PopupHandler;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 流程画板面板
 */
public class ChartPanel extends JComponent implements DataProvider, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener, PropertyObject, FocusListener, UserDataHolder {

    public static final DataKey<ChartPanel> CURRENT_CHART_PANEL = DataKey.create("CURRENT_CHART_PANEL");


    private List<ShapeSelectedListener> shapeSelectedListeners = new ArrayList<>();
    private List<ProcessSaveListener> processSaveListener = new ArrayList<>();
    private ConstantUtils constantUtils = ConstantUtils.getInstance();

    private PropertyObject selectedObject = null;
    private static boolean debug = false;
    private Graphics2D currentGraphic = null;
    private Stroke currentStroke = null;
    LinkedList<Shape> shapes = new LinkedList<>();
    LinkedList<Line> lines = new LinkedList<>();
    private LinkedList<StateChange> stateHistory = new LinkedList<>();
    private LinkedList<StateChange> undoStack = new LinkedList<>();
    private List<Shape> copyList = new ArrayList<>();
    private List<Line> copyLines = new ArrayList<>();
    private HiPoint copyMouseStartPoint = null;
    private List<ChartChangeListener> chartChangeListenerList = new ArrayList<>();

    public static int MODE_DEFAULT = 0;
    public static int MODE_LINE = 1;
    public static int MODE_NEW_SHAPE = 2;
    public static int MODE_MOVE = 4;

    private boolean whitespacePressing = false;
    private int markMode = -1;

    private LinkedList<Color> markColors = new LinkedList<Color>();
    private LinkedList<Font> markFonts = new LinkedList<Font>();

    private Cursor markCursor = null;
    /**
     * 当前流程ID
     */
    private String id;
    /**
     * 当前流程名称
     */
    private String name;

    private Size initBoardSize = new Size(600, 800);
    private Size boardSize = new Size(600, 800);

    //图像原点
    private HiPoint initialOriginalPoint = new HiPoint(10, 10);

    private HiPoint originalPoint = new HiPoint(initialOriginalPoint.x, initialOriginalPoint.y);
    //图形放大率
    private double scale = 1;
    private boolean ctrlPressing = false;
    //shift按钮按下的状态
    private boolean shiftPressing = false;

    //对齐
    private int alignDistance = 5;
    private double alignX = 0;
    private Shape alignXhitObj = null;
    private double alignY = 0;
    private Shape alignYhitObj = null;
    private Shape dragPressObj = null;
    private Point dragpressPoint = null;
    public Shape resizePressObj = null;
    //以哪条边进行拖动的
    public ShapePos resizePos = ShapePos.NOT_INSIDE;
    public HiPoint resizeStartPoint = new HiPoint(0, 0);
    public HiPoint resizeCurrentPoint = new HiPoint(0, 0);

    private Shape lineStartObj = null;
    private HiPoint lineCursorTracker = null;

    double draggingOffsetX;
    double draggingOffsetY;
    int mode = MODE_DEFAULT;
    //新图形使用的类
    private Class<? extends Shape> newShapeClass;

    //新流程线段使用的类
    private Class<? extends Line> newLineClass;

    /**
     * 拖动线段的时候，被拖动的线段
     */
    private Line dragLine = null;
    //被拖动的线段上的点
    private BaseState dragLineStartState = null;
    private PointLink dragLinePoint = null;
    //被拖动的开始点击点
    private HiPoint dragLineStartPoint = null;
    //被拖动的当前点
    private HiPoint dragLineCurrentPoint = null;

    //新建矩形/或者圆形的起点
    private HiPoint newShapeStartPoint = null;
    private HiPoint newShapeDragCurrentPoint = null;

    //画板移动的起始与结束点
    private Point boardMoveStartPoint = null;
    private Point boardMoveCurrentPoint = null;
    private HiPoint boardMoveStartOriginPoint = null;


    //画板颜色

    //触发drag事件就会置位true
    private boolean dragFlag = false;
    //图形拖动的控制标志位
    private boolean dragging = false;
    //用于记录当前鼠标位置
    private Point mouseCurrentPoint = null;

    private Project project;

    private VirtualFile virtualFile;
    private SpdEditor editor;


    public ChartPanel(Project project, VirtualFile virtualFile, SpdEditor editor) {
        this.project = project;
        this.virtualFile = virtualFile;
        this.editor = editor;
        setBackground(Color.gray);
        //初始化的图形仅仅供测试
        addMouseMotionListener(this);
        addMouseListener(this);
        setFocusable(true);
        grabFocus();
        addKeyListener(this);
        addMouseWheelListener(this);

        addFocusListener(this);
        setInheritsPopupMenu(true);
        ActionManager instance = ActionManager.getInstance();
        ActionGroup ag = (ActionGroup) instance.getAction("SqlDiffAction");
//        ActionPopupMenu sqlDiffAction = instance.createActionPopupMenu("diff", (ActionGroup) instance.getAction("SqlDiffAction"));
        //这样
//        sqlDiffAction.setTargetComponent(this.getSpdEditor().getChartPanel());
//        chartPanel.setComponentPopupMenu(sqlDiffAction.getComponent());
//        this.getSpdEditor().getChartPanel().setComponentPopupMenu(sqlDiffAction.getComponent());
//        PopupHandler.installPopupMenu(myList, "VcsSelectionHistoryDialog.Popup", ActionPlaces.UPDATE_POPUP);
        PopupHandler.installPopupHandler(this, ag, ActionPlaces.UPDATE_POPUP);
        //注册Ctrl+F给对应的Action
        new FindInSpdEditor(this).registerCustomShortcutSet(this,null);
    }

    /**
     * @return
     * @
     */
    public int getMode() {
        return mode;
    }


    /**
     * 设置新图形的模式
     *
     * @param clz
     */
    public void setModeOfNewLine(Class<? extends Line> clz) {
        //由于目前
        this.setMode(MODE_LINE);
        this.newLineClass = clz;
        this.newShapeClass = null;
    }

    /**
     * 设置新图形的模式
     *
     * @param clz
     */
    public void setModeOfNewShape(Class<? extends Shape> clz) {
        //由于目前
        this.setMode(MODE_NEW_SHAPE);
        this.newShapeClass = clz;
        this.newLineClass = null;

    }


    /**
     * 带默认模式
     *
     * @param clz
     * @param cursor
     */
    public void setModeOfNewShape(Class<? extends Shape> clz, Cursor cursor) {
        //由于目前
        this.setMode(MODE_NEW_SHAPE);
        this.newShapeClass = clz;
        this.newLineClass = null;

        this.setCursor(cursor);
    }


    public void setMode(int mode) {
        this.mode = mode;
        //重置状态
        this.dragPressObj = null;
        this.resizePressObj = null;
        this.lineStartObj = null;
        this.lineCursorTracker = null;
        for (Shape shape : shapes) {
            shape.blur(null);
        }
        for (Line line : lines) {
            line.blur(null);
        }
        Cursor cursorByMode = getCursorByMode(this.mode);
        this.setCursor(cursorByMode);
        repaint();
    }

    private Cursor getCursorByMode(int mode) {
        if (MODE_LINE == this.mode) {
            return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        } else if (MODE_NEW_SHAPE == this.mode) {
            return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        } else if (MODE_DEFAULT == this.mode) {
            return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        } else if (MODE_MOVE == this.mode) {
            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
        return Cursor.getDefaultCursor();
    }


    /**
     * 翻译点坐标
     * 将真实坐标转化为绘图坐标
     *
     * @param point 点
     */
    public HiPoint translatePoint(HiPoint point) {
        return translatePoint(point.x, point.y);
    }

    /**
     * 从绘图坐标转化到真实坐标
     * graphicToReal
     *
     * @param x
     * @param y
     * @return
     */
    public HiPoint retranslatePoint(double x, double y) {
        return new HiPoint(x / scale - originalPoint.x, y / scale - originalPoint.y);
    }


    /**
     * 从图形坐标转化到真实的坐标
     *
     * @param point
     * @return
     */
    public HiPoint retranslatePoint(Point point) {
        return retranslatePoint(point.x, point.y);
    }

    /**
     * 翻译点坐标
     * 主要是从真实坐标转化为绘图坐标(带缩放与偏移的坐标)
     *
     * @param x 坐标
     * @param y 坐标
     */
    public HiPoint translatePoint(double x, double y) {
        return new HiPoint(Math.round((x + originalPoint.x) * scale), Math.round((y + originalPoint.y) * scale));
    }

    /**
     * 只转换x坐标
     *
     * @param i
     * @return
     */
    public double translateX(double i) {
        double fakex = i + originalPoint.x;
        return fakex * scale;
    }

    public double translateY(double i) {
        double fakey = i + originalPoint.y;
        return fakey * scale;
    }


    /**
     * 翻译高度为实际坐标
     *
     * @param size
     * @return
     */
    public Size translateSize(Size size) {
        return translateSize(size.getW(), size.getH());
    }

    /**
     * 将实际大小转化为绘图大小
     *
     * @param w
     * @param h
     * @return
     */
    public Size translateSize(double w, double h) {
        return new Size(w * scale, h * scale);
    }

    public double translateSize(double size) {
        return size * scale;
    }

    /**
     * 图形尺寸转化为实际尺寸
     *
     * @param w
     * @param h
     * @return
     */
    public Size retranslateSize(double w, double h) {
        return new Size(w / scale, h / scale);
    }

    /**
     * 图形尺寸转化为实际尺寸
     *
     * @param offset
     * @return
     */
    public double retranslate(double offset) {
        return offset / scale;
    }


    /**
     * 懒人版绘制直线
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawStrokeLine(double x1, double y1, double x2, double y2) {
        markStroke();
        Graphics2D g2d = (Graphics2D) this.currentGraphic;
        HiPoint topleft = translatePoint(x1, y1);
        HiPoint bottomRight = translatePoint(x2, y2);
        float[] dash = {1.0f, 0.0f, 1.0f};
        BasicStroke basicStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
        g2d.setStroke(basicStroke);
        g2d.drawLine(doubleToInt(topleft.x), doubleToInt(topleft.y), doubleToInt(bottomRight.x), doubleToInt(bottomRight.y));
        resetStroke();
    }


    /**
     * 懒人版绘制直线
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(double x1, double y1, double x2, double y2) {
        Graphics2D g2d = (Graphics2D) this.currentGraphic;
        HiPoint topleft = translatePoint(x1, y1);
        HiPoint bottomRight = translatePoint(x2, y2);
        g2d.drawLine(doubleToInt(topleft.x), doubleToInt(topleft.y), doubleToInt(bottomRight.x), doubleToInt(bottomRight.y));
    }

    /**
     * 懒人版绘制直线
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(double x1, double y1, double x2, double y2, int ox, int oy, int ox2, int oy2) {
        Graphics2D g2d = (Graphics2D) this.currentGraphic;
        HiPoint topleft = translatePoint(x1, y1);
        HiPoint bottomRight = translatePoint(x2, y2);
        g2d.drawLine(doubleToInt(topleft.x) + ox, doubleToInt(topleft.y) + oy, doubleToInt(bottomRight.x) + ox2, doubleToInt(bottomRight.y) + oy2);
    }

    /**
     * 四舍五入般转换
     *
     * @param val
     * @return
     */
    public int doubleToInt(double val) {
        return (int) Math.round(val);
    }


    /**
     * 填充图形
     */
    public void fillRect(double x, double y, double w, double h) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = this.currentGraphic;
        g2d.fillRect(doubleToInt(point.x), doubleToInt(point.y), doubleToInt(size.getW()), doubleToInt(size.getH()));
    }

    /**
     * 填充图形
     */
    public void fillRoundRect(double x, double y, double w, double h, int aw, int ah) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = this.currentGraphic;
        Size asize = translateSize(aw, ah);

        g2d.fillRoundRect(doubleToInt(point.x), doubleToInt(point.y), doubleToInt(size.getW()), doubleToInt(size.getH()), doubleToInt(asize.getW()), doubleToInt(asize.getH()));
    }

    /**
     * 填充图形
     */
    public void drawRect(double x, double y, double w, double h) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = (Graphics2D) currentGraphic;
        g2d.drawRect(doubleToInt(point.x), doubleToInt(point.y), doubleToInt(size.getW()), doubleToInt(size.getH()));
    }

    /**
     * 因为对于图形来说，fillRect是含有头含有尾部的，
     * 但是对于drawRect是含头超出一个尾部的，
     * 为了绘制刚好在图框的边沿,那么就只能过左上移动1即使-1,-1
     * 而右边下边本来刚好的被移动了，就得补了
     * 填充图形
     */
    public void drawRectBorder(double x, double y, double w, double h) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = (Graphics2D) currentGraphic;
        g2d.drawRect(doubleToInt(point.x) - 1, doubleToInt(point.y) - 1, doubleToInt(size.getW()) + 1, doubleToInt(size.getH()) + 1);
    }

    /**
     * 描边
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param ar
     * @param ah
     */
    public void drawRoundBorder(double x, double y, double w, double h, double ar, double ah) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = currentGraphic;
        Size asize = translateSize(ar, ah);
        g2d.drawRoundRect(doubleToInt(point.x) - 1, doubleToInt(point.y) - 1, doubleToInt(size.getW()) + 1, doubleToInt(size.getH()) + 1, doubleToInt(asize.getW()), doubleToInt(asize.getH()));
    }

    /**
     * 与fillREct 同等
     */
    public void drawRectInnerBorder(double x, double y, double w, double h) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = currentGraphic;
        g2d.drawRect(doubleToInt(point.x), doubleToInt(point.y), doubleToInt(size.getW()) - 1, doubleToInt(size.getH()) - 1);
    }


    /**
     * 填充图形
     */
    public void fillOval(double x, double y, double w, double h) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = currentGraphic;
        g2d.fillOval(doubleToInt(point.x), doubleToInt(point.y), doubleToInt(size.getW()), doubleToInt(size.getH()));
    }

    /**
     * 填充图形
     */
    public void drawOval(double x, double y, double w, double h) {
        HiPoint point = translatePoint(x, y);
        Size size = translateSize(w, h);
        Graphics2D g2d = currentGraphic;
        g2d.drawOval(doubleToInt(point.x), doubleToInt(point.y), doubleToInt(size.getW()), doubleToInt(size.getH()));
    }


    public void drawPolyline(double xPoints[], double yPoints[],
                             int nPoints) {

        int xpps[] = new int[xPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            xpps[i] = (int) Math.round(translateX(xPoints[i]));
        }
        int ypps[] = new int[xPoints.length];
        for (int i = 0; i < yPoints.length; i++) {
            ypps[i] = (int) Math.round(translateY(yPoints[i]));
        }
        Graphics2D g2d = currentGraphic;
        g2d.drawPolyline(xpps, ypps, nPoints);

    }

    /**
     * 绘制多边形，以闭合形式，非填充
     *
     * @param points
     */
    public void fillPolylineClose(HiPoint... points) {
        double[][] xyPoints = translatePointToCompatibleArray(points);
        fillPolyline(xyPoints[0], xyPoints[1], points.length + 1);
    }

    /**
     * 绘制多边形，以闭合形式，非填充
     *
     * @param points
     */
    public void drawPolylineClose(HiPoint... points) {
        double[][] xyPoints = translatePointToCompatibleArray(points);
        drawPolyline(xyPoints[0], xyPoints[1], points.length + 1);
    }

    /**
     * 绘制多边形，以闭合形式，非填充
     *
     * @param points
     */
    public void drawPolylineClose(List<HiPoint> points) {
        double[][] xyPoints = translatePointToCompatibleArray(points.toArray(new HiPoint[]{}));
        fillPolyline(xyPoints[0], xyPoints[1], points.size() + 1);
    }

    public double[][] translatePointToCompatibleArray(HiPoint... points) {
        double xPoints[] = new double[points.length + 1];
        double yPoints[] = new double[points.length + 1];
        for (int i = 0; i < points.length; i++) {
            xPoints[i] = points[i].x;
            yPoints[i] = points[i].y;
        }
        xPoints[points.length] = points[0].x;
        yPoints[points.length] = points[0].y;
        return new double[][]{xPoints, yPoints};
    }


    /**
     * 绘制多边形，以闭合，填充形式
     *
     * @param points
     */
    public void fillPolylineClose(List<HiPoint> points) {
        double xPoints[] = new double[points.size() + 1];
        double yPoints[] = new double[points.size() + 1];
        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = points.get(i).x;
            yPoints[i] = points.get(i).y;
        }
        xPoints[points.size()] = points.get(0).x;
        yPoints[points.size()] = points.get(0).y;
        fillPolyline(xPoints, yPoints, points.size() + 1);
    }

    public void fillPolyline(double xPoints[], double yPoints[],
                             int nPoints) {

        int xpps[] = new int[xPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            xpps[i] = (int) Math.round(translateX(xPoints[i]));
        }
        int ypps[] = new int[xPoints.length];
        for (int i = 0; i < yPoints.length; i++) {
            ypps[i] = (int) Math.round(translateY(yPoints[i]));
        }
        Graphics2D g2d = (Graphics2D) currentGraphic;
        g2d.fillPolygon(xpps, ypps, nPoints);

    }

    private void setAntiAlias() {
        currentGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        currentGraphic.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
    }

    public void paintForExport(Graphics g) {
        this.currentGraphic = (Graphics2D) g;
        ;
        //设置图形去锯齿
        // activate anti aliasing for text rendering (if you want it to look nice)
        setAntiAlias();
//        drawBackboard();
        //绘制画板
        drawTotalBoard();
        //瞬时的虚线
        drawTempFlowLine();
        //绘制图形
        drawShapes();
        //绘制流程线段,
        drawFlowLines();
        //对齐参考线y
        drawAlignLine();
        //绘制拖动的图形
        drawDragShape();
        //绘制新建图形
        drawNewShape();
        //绘制调试信息
        drawDebug();
        //绘制耗损性能情况
//        drawOccupation();
        g.dispose();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        this.currentGraphic = (Graphics2D) g;
        //设置图形去锯齿
        // activate anti aliasing for text rendering (if you want it to look nice)
        setAntiAlias();
//        drawBackboard();
        //绘制画板
        drawBoard();
        //瞬时的虚线
        drawTempFlowLine();
        //绘制图形
        drawShapes();
        //绘制流程线段,
        drawFlowLines();
        //对齐参考线y
        drawAlignLine();
        //绘制拖动的图形
        drawDragShape();
        //绘制新建图形
        drawNewShape();
        //绘制调试信息
        drawDebug();
        //绘制耗损性能情况
//        drawOccupation();

        g.dispose();
    }


    private void drawOccupation() {
        Runtime runtime = Runtime.getRuntime();
        long maxM = runtime.maxMemory();
        long total = runtime.totalMemory();
        int cpu = runtime.availableProcessors();
        long free = runtime.freeMemory();
        this.drawString(0, 0, String.format("max:%d total:%d cpu:%d free:%d", maxM, total, cpu, free));
    }

    /**
     * 绘制一堆图形
     */
    private void drawShapes() {
        for (Shape shape : shapes) {
            if (!shape.visiable()) {
                continue;
            }
            shape.drawShape(this);
        }
    }

    /**
     * 绘制一堆流程线段
     */
    private void drawFlowLines() {
        Graphics2D g2d = this.currentGraphic;
        for (Line line : lines) {
            line.drawLine(this);
        }
    }


    private void drawNewShape() {
        //绘制新建矩形
        Graphics2D g2d = (Graphics2D) this.currentGraphic;

        if (newShapeDragCurrentPoint != null) {
            Color bc = g2d.getColor();
            g2d.setColor(new Color(177, 177, 177, 255));
            Stroke stroke = g2d.getStroke();
            float[] dash = {2f, 0f, 2f};
            BasicStroke basicStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
            g2d.setStroke(basicStroke);
            Rect rect = calcShape(newShapeStartPoint, newShapeDragCurrentPoint);
            if (rect.getW() > 20 && rect.getH() > 20) {
                g2d.setColor(ConstantUtils.getInstance().getChartPanelNewShapeValidColor());
            } else {
                g2d.setColor(ConstantUtils.getInstance().getChartPanelNewShapeInvalidColor());
            }

            if (newShapeClass != null && MODE_NEW_SHAPE == mode) {
                try {
                    Shape shape = newShapeClass.getDeclaredConstructor().newInstance();
                    shape.drawNewShape(this, rect);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            g2d.setStroke(stroke);
            g2d.setColor(bc);
        }
    }

    private void drawDragShape() {
        Graphics2D g2d = this.currentGraphic;
        if (!dragging) {
            return;
        }

        if (dragPressObj != null) {
            markColor();
            g2d.setColor(ConstantUtils.getInstance().getChartPanelDraggingShapeColor());
            for (Shape shape : shapes) {
                if (!shape.visiable()) {
                    continue;
                }
                if (shape.isDraging()) {
                    shape.drawDragging(this, draggingOffsetX, draggingOffsetY);
                }
            }
            resetColor();
        }
    }

    private void drawAlignLine() {
        Graphics2D g2d = this.currentGraphic;
        markColor();
        g2d.setColor(constantUtils.getAlignLineColor());
        if (alignYhitObj != null) {
            drawLine(0, alignY, this.getWidth(), alignY);
        }
        //对齐参考线x
        if (alignXhitObj != null) {
            drawLine(alignX, 0, alignX, this.getHeight());
        }
        resetColor();
    }

    /**
     * 绘制临时的流程虚线
     *
     * @param
     */
    private void drawTempFlowLine() {
        Graphics2D g2d = this.currentGraphic;
        markColor();
        markStroke();
        if (lineStartObj != null && lineCursorTracker != null) {
            HiPoint centerPoint = lineStartObj.getCenterPoint();
            float[] dash = {2f, 0, 2f};

            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 0f));
            g2d.setColor(constantUtils.getChartPanelNewFlowLineFillColor());
            drawLine(centerPoint.x, centerPoint.y, lineCursorTracker.x, lineCursorTracker.y);
        }
        resetColor();
        resetStroke();
    }

    private void drawTotalBoard() {
        Graphics2D g2d = this.currentGraphic;
        markColor();

        g2d.setColor(ConstantUtils.getInstance().getChartPanelBoardColor());
        fillRect(0, 0, boardSize.getW(), boardSize.getH());
        resetColor();
    }

    private void drawBoard() {
        Graphics2D g2d = this.currentGraphic;
        markColor();
        g2d.setColor(ConstantUtils.getInstance().getChartPanelBoardColor());
        fillRect(0, 0, boardSize.getW(), boardSize.getH());
        resetColor();
    }

    private void drawBackboard() {
        Graphics2D g2d = this.currentGraphic;

        int width = this.getWidth();
        int height = this.getHeight();


        markColor();
        int blockwidth = 6;
        //绘制背板
        for (int i = 0; i < Math.ceil(height / blockwidth) + 1; i++) {
            for (int j = 0; j < Math.ceil(width / blockwidth) + 1; j++) {
                Color cc = (i + j) % 2 == 1 ? constantUtils.getChartPanelBackgroundOddColor() : constantUtils.getChartPanelBackgroundEvenColor();
                g2d.setColor(cc);
                int x = j * blockwidth;
                int y = i * blockwidth;
                g2d.fillRect(x, y, blockwidth, blockwidth);

            }
        }
        resetColor();


    }

    /**
     * 导出到文件
     *
     * @param selectedFile 被选中的文件
     * @throws IOException
     */
    public void exportToFile(File selectedFile) throws IOException {
        if (!selectedFile.exists()) {
            selectedFile.createNewFile();
        } else {
            int opt = JOptionPane.showConfirmDialog(this,
                    "文件已经存在，确认要覆盖?", "确认信息",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.NO_OPTION) {
                return;
            }
        }

        double ww = boardSize.getW();
        double hh = boardSize.getH();
        ExportImageDialog exportImageDialog = new ExportImageDialog(1.0, ww, hh);
        exportImageDialog.setVisible(true);
        double initScale = exportImageDialog.getInitScale();
        BufferedImage bufferedImage = new BufferedImage((int) Math.ceil(ww * initScale), (int) Math.ceil(hh * initScale), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        double x = originalPoint.x;
        double y = originalPoint.y;
        originalPoint.x = 0;
        originalPoint.y = 0;
        double scale = this.scale;
        this.scale = initScale;
        paintForExport(graphics);
        this.scale = scale;
        originalPoint.x = x;
        originalPoint.y = y;

        //确认继续操作
        ImageIO.write(bufferedImage, "jpg", selectedFile);
    }

    /**
     * 导出为图片
     */
    public void export() {
        try {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setCurrentDirectory(new File("E:\\RTC_work\\DEV3\\xfunds_201608\\import\\"));

//            jFileChooser.setCurrentDirectory(new File("./"));
            jFileChooser.showDialog(new JLabel(), "请选择");
            File selectedFile = jFileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }

            exportToFile(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawDebug() {
        if (!debug) {
            return;
        }
        Graphics2D g = this.currentGraphic;

        int offsetright = 200;
        Color color = g.getColor();

        int initx = this.getWidth() - offsetright;
        int inity = 0;
        int lineheight = 18;
        int x = initx;
        int y = inity;
        g.setColor(new Color(86, 86, 86, 217));
        g.fillRect(initx, inity, offsetright, lineheight * (shapes.size() * 3 + 1));


        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            g.setColor(shape.getBackgroundColor());
            if (shape.isHover()) {
                g.fillRect(x, y, offsetright, lineheight);
                g.setColor(new Color(102, 102, 102));
            }
            g.drawString(String.format("x:%.2f y:%.2f w:%.2f h:%.2f", shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()), x, y + 12);
            y += lineheight;
            g.setColor(shape.getBackgroundColor());
            if (shape.isHover()) {
                g.fillRect(x, y, offsetright, lineheight);
                g.setColor(new Color(102, 102, 102));
            }
            g.drawString(String.format("drg:%s res:%s hov:%s foc:%s", shape.isDraging() ? 1 : 0, shape.isResizing() ? 1 : 0, shape.isHover() ? 1 : 0, shape.isFocusing() ? 1 : 0), x, y + 12);
            y += lineheight;
            g.setColor(shape.getBackgroundColor());

            if (shape.isHover()) {
                g.fillRect(x, y, offsetright, lineheight);
                g.setColor(new Color(102, 102, 102));
            }
            g.drawString(String.format("align:%s", NumberUtils.binaryFormat(shape.getAlign(), 16)), x, y + 12);
            y += lineheight;
        }
        g.setColor(new Color(241, 168, 89));
        g.drawString(String.format("ctl:%s ws:%s sc:%.2f org:(%.2f,%.2f)", ctrlPressing ? 1 : 0, whitespacePressing ? 1 : 0, scale, originalPoint.x, originalPoint.y), x, y + 12);
        y += lineheight;
        g.setColor(color);

//        drawString(20,20,100,20,20);

    }

    /**
     * 只需要关心在那个位置放置就好了，无需关心字符串是否超长
     * 但是需要注意字符串超长导致的越界问题
     *
     * @param x
     * @param y
     * @param text
     */
    public void drawString(double x, double y, String text) {
        FontMetrics fontMetrics = this.currentGraphic.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int width = fontMetrics.stringWidth(text);
        int lineheight = ascent + descent;
        drawString(x, y, width, lineheight, lineheight, text);
    }

    public void setFontExt(Font font) {
        this.currentGraphic.setFont(font);
    }


    /**
     * 只需要关心在那个位置放置就好了，无需关心字符串是否超长
     * 但是需要注意字符串超长导致的越界问题
     *
     * @param x
     * @param y
     * @param text
     */
    public void drawStringHint(double x, double y, String text) {
        FontMetrics fontMetrics = this.currentGraphic.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int lineheight = ascent + descent;
        this.drawLine(x, y, x, y + lineheight);
    }

    /**
     * 在给定矩形区域 绘制字符串
     *
     * @param x          x坐标
     * @param y          y坐标
     * @param w          宽度
     * @param h          高度
     * @param lineheight 行高
     * @param text       文本内容
     */
    public void drawString(double x, double y, double w, double h, double lineheight, String text) {
        if (text == null) {
            text = "NULL";
        }
        Font font = this.currentGraphic.getFont();
        HiPoint hiPoint = translatePoint(x, y);
        Font fontScale = font.deriveFont(Font.PLAIN, (int) Math.round(translateSize(font.getSize())));
        this.currentGraphic.setFont(fontScale);
        FontMetrics fontMetrics = this.currentGraphic.getFontMetrics(font);
        double offsety = Math.round(lineheight / 2.0 - fontMetrics.getDescent() + fontMetrics.getAscent() / 2.0);

        this.currentGraphic.drawString(text, (int) Math.round(hiPoint.x), (int) Math.round(hiPoint.y + translateSize(offsety)));
        this.currentGraphic.setFont(font);

    }


    /**
     * 以边框的形式进行绘制
     *
     * @param x
     * @param y
     * @param text
     */
    public void draw(int x, int y, String text) {
        Graphics2D g2 = this.currentGraphic;

        // remember original settings
        Color originalColor = g2.getColor();
        Stroke originalStroke = g2.getStroke();
        RenderingHints originalHints = g2.getRenderingHints();
        Color outlineColor = Color.black;
        Color fillColor = Color.white;
        BasicStroke outlineStroke = new BasicStroke(1.0f);

        // create a glyph vector from your text
        Font font = this.currentGraphic.getFont();
        GlyphVector glyphVector = font.createGlyphVector(g2.getFontRenderContext(), text);
        // get the shape object
        java.awt.Shape textShape = glyphVector.getOutline();


        g2.translate(x, y);
//
        g2.setColor(outlineColor);
        g2.setStroke(outlineStroke);
        g2.draw(textShape); // draw outline

        g2.setColor(fillColor);
        g2.fill(textShape); // fill the shape

        // reset to original settings after painting
        g2.setColor(originalColor);
        g2.setStroke(originalStroke);
        g2.setRenderingHints(originalHints);
        g2.translate(-x, -y);
    }


    /**
     * 在给定矩形区域 绘制字符串
     *
     * @param x      x坐标
     * @param y      y坐标
     * @param width  宽度
     * @param height 高度
     * @param text   文本内容
     */
    public void drawString(double x, double y, double width, double height, String text) {
        if (text == null) {
            return;
        }
        int padding = 2;
        Font font = this.currentGraphic.getFont();
        //实际需要绘制到的坐标

        FontMetrics fontMetrics = this.currentGraphic.getFontMetrics(font);
        int strHReal = fontMetrics.getAscent() + fontMetrics.getDescent();
        int length = text.length();
        List<String> strParts = new ArrayList<>();
        String temp = "";
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            int chlen = fontMetrics.charWidth(ch);
            if (chlen > width) {
                //如果连一个字符都超过，这就很过分了
                break;
            }
            String tmps = temp + ch;
            int strlen = fontMetrics.stringWidth(tmps);
            if (strlen > width) {
                strParts.add(temp);
                temp = String.valueOf(ch);
            } else {
                temp += ch;
            }
        }
        if (temp != null && !temp.trim().equals("")) {
            strParts.add(temp);
        }

        int fullHeight = strParts.size() * (strHReal);
        double finalStartX = x;
        double finalStartY = y + height / 2.0 - fullHeight / 2.0;
        for (int i = 0; i < strParts.size(); i++) {
            String s = strParts.get(i);
            int strW = fontMetrics.stringWidth(s);
            double offsetX = (width - strW) / 2.0;
            this.drawString(finalStartX + offsetX, finalStartY, strW, strHReal, strHReal, s);
            finalStartY += (strHReal);
        }

    }

    /**
     * 绘制指定矩形边框的阴影
     *
     * @param
     * @param x
     * @param y
     * @param boardWidth
     * @param boardHeight
     */
    private void drawShadow(int x, int y, double boardWidth, double boardHeight) {

        HiPoint point = translatePoint(x, y);
        Size size = translateSize(boardWidth, boardHeight);
        int shadowWidth = 3;
        double opacity = 1;
        this.markColor();
        int offsetX = 1;
        int offsetY = 1;
        Graphics2D g2d = (Graphics2D) currentGraphic;
        for (int i = 0; i < shadowWidth; i++) {
            opacity -= opacity / shadowWidth;
            this.setColor(new Color(150, 150, 150, (int) (150 * opacity)));
            g2d.fillRoundRect(doubleToInt(Math.round(point.x) - i + offsetX), doubleToInt(Math.round(point.y) - i + offsetY)
                    , doubleToInt(Math.round(size.getW()) + 2 * i + 1), doubleToInt(Math.round(size.getH()) + 2 * i + 1), 10, 10);
        }
        this.resetColor();
    }

    public void setColor(Color color) {
        this.currentGraphic.setColor(color);
    }

    /**
     * 用于根据两个point 计算合适的矩形，例如新增矩形的时候，两个点位置可能不定，但是作为合理的矩形，
     * 应该是左上角的坐标与右下角的左边
     *
     * @param point1
     * @param point2
     * @return
     */
    private Rect calcShape(HiPoint point1, HiPoint point2) {
        double x1 = point1.x;
        double x2 = point2.x;
        double y1 = point1.y;
        double y2 = point2.y;
        double x = x1 > x2 ? x2 : x1;
        double y = y1 > y2 ? y2 : y1;
        double w = x1 > x2 ? x1 - x2 : x2 - x1;
        double h = y1 > y2 ? y1 - y2 : y2 - y1;
        return new Rect(x, y, w, h);
    }


    private void markMode() {
        this.markMode = mode;
    }

    private void resetMode() {
        this.markMode = -1;
        this.mode = markMode;
    }

    public void setIdForLine(Line line) {
        int id = calcIdForLine(line);
        line.setId(line.getIdPrefix() + (id + 1));
    }

    private int calcIdForLine(Line line) {
        Class<? extends Line> aClass = line.getClass();
        int maxidx = 0;
        for (Line l : lines) {
            if (l.getClass() == aClass) {
                String id = l.getId();
                String idPrefix = l.getIdPrefix();
                String substring = id.substring(idPrefix.length());
                int i = Integer.parseInt(substring);
                if (maxidx < i) {
                    maxidx = i;
                }
            }
        }
        return maxidx;
    }

    public void setIdForShape(Shape shape) {
        int i = calcIdForShape(shape);
        shape.setId(shape.getIdPrefix() + (i + 1));
    }

    public int calcIdForShape(Shape shape) {
        Class<? extends Shape> aClass = shape.getClass();
        int maxidx = 0;
        for (Shape s : shapes) {
            if (s.getClass() == aClass) {
                String id = s.getId();
                String idPrefix = s.getIdPrefix();
                String substring = id.substring(idPrefix.length());
                int i = Integer.parseInt(substring);
                if (maxidx < i) {
                    maxidx = i;
                }
            }
        }
        return maxidx;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (e.getClickCount() == 2) {
            if (getSelectedObject() instanceof CallActivity) {
                ActionManager instance = ActionManager.getInstance();
                AnAction goToProcessAction = instance.getAction("goToProcessAction");
                instance.tryToExecute(goToProcessAction, e, this, "", true);
            } else if (getSelectedObject() instanceof ServiceTask) {
                ActionManager instance = ActionManager.getInstance();
                AnAction gotoServiceTaskAction = instance.getAction("gotoServiceTaskAction");
                instance.tryToExecute(gotoServiceTaskAction, e, this, "", true);
            } else if (getSelectedObject() instanceof UserTask) {
                ActionManager instance = ActionManager.getInstance();
                AnAction gotoServiceTaskAction = instance.getAction("gotoRightAction");
                instance.tryToExecute(gotoServiceTaskAction, e, this, "", true);
            }
            return;
        }

//        fireSavedListener();

        if (whitespacePressing) {
            return;
        }
        if (MODE_NEW_SHAPE == mode) {
            //记录新句型
            HiPoint point = retranslatePoint(e.getPoint());

            Shape shape = null;
            try {
                shape = newShapeClass.getDeclaredConstructor().newInstance();
                setIdForShape(shape);
                shape.init(point);
                addShape(shape);
                repaint();
                if (SpdEditorDBState.getInstance().autoSave) {
                    fireSavedListener();
                }

            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException instantiationException) {
                instantiationException.printStackTrace();
            }

        } else if (MODE_DEFAULT == mode) {
            HiPoint hiPoint = retranslatePoint(e.getPoint());
            //逆序
            boolean repaint = false;
            if (aboveShape(hiPoint, shape -> shape.onClick(e))) {
                repaint = true;
            }
            if (aboveLine(hiPoint, line -> line.onClick(e))) {
                repaint = true;
            }
            if (repaint) {
                repaint();
            }

        }
    }

    /**
     * 如果位置在对象上就执行后面的逻辑
     *
     * @param hiPoint
     * @param insidecb
     * @param notinsidecb
     * @return
     */
    private boolean aboveLine(HiPoint hiPoint, Function<Line, Boolean> insidecb, Function<Line, Boolean> notinsidecb) {
        Iterator<Line> lit = lines.descendingIterator();
        boolean result = false;
        boolean hitone = false;
        while (lit.hasNext()) {
            Line line = lit.next();
            if (line.above(hiPoint) && !hitone) {
                hitone = true;
                if (insidecb.apply(line)) {
                    result = true;
                }
            } else if (notinsidecb != null) {
                if (notinsidecb.apply(line)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 如果位置在对象上就执行后面的逻辑
     *
     * @param hiPoint
     * @param function
     * @return
     */
    private boolean aboveLine(HiPoint hiPoint, Function<Line, Boolean> function) {
        return aboveLine(hiPoint, function, null);
    }

    /**
     * 是否在图形上面,在一个图形上与否，不仅取决于是否在上面，而且取决于是否被遮盖
     *
     * @param hiPoint           击中点
     * @param insidecb          鼠标在上面的
     * @param notinsidecallback 处理喊出
     * @return
     */
    private boolean aboveShape(HiPoint hiPoint, Function<Shape, Boolean> insidecb, Function<Shape, Boolean> notinsidecallback) {
        Iterator<Shape> lit = shapes.descendingIterator();
        boolean onehit = false;
        boolean result = false;
        while (lit.hasNext()) {
            Shape shape = lit.next();
            if (!shape.visiable()) {
                continue;
            }
            if (shape.inside(hiPoint) && !onehit) {
                //鼠标在空间上面
                onehit = true;
                if (insidecb.apply(shape)) {
                    result = true;
                }
            } else {
                if (notinsidecallback != null) {
                    if (notinsidecallback.apply(shape)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 是否在图形上面
     *
     * @param hiPoint  击中点
     * @param function 处理喊出
     * @return
     */
    private boolean aboveShape(HiPoint hiPoint, Function<Shape, Boolean> function) {
        return aboveShape(hiPoint, function, null);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.grabFocus();
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (MODE_MOVE == mode || whitespacePressing) {
            this.boardMoveStartPoint = e.getPoint();
            this.boardMoveStartOriginPoint = new HiPoint(this.originalPoint.x, this.originalPoint.y);
        } else if (MODE_LINE == mode) {
            //新建线段
            mousePressedModeLine(e);
        } else if (MODE_NEW_SHAPE == mode) {
            //记录新矩形
            newShapeStartPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
        } else if (MODE_DEFAULT == mode) {

            HiPoint hiPoint = retranslatePoint(e.getPoint());

            //如果是移动状态就不能够重置状态了
            //记住拖动的选中点
            for (Line line : lines) {
                if (line.isSelected()) {
                    PointLink point = line.hoveringPoint(retranslatePoint(e.getPoint().x, e.getPoint().y));
                    if (point != null) {
                        //开始进行拖动
                        dragLine = line;
                        dragLinePoint = point;
                        dragLineStartPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
                        //记录一个历史操作的开始
                        dragLineStartState = line.serialize();
                        return;
                    }
                }
            }

            //图形放大与缩小
            Iterator<Shape> iterator = shapes.descendingIterator();
            while (iterator.hasNext()) {
                Shape it = iterator.next();
                ShapePos shapePos = ShapeUtils.insideDot(retranslatePoint(e.getPoint().x, e.getPoint().y), it.toRect());
                if (shapePos != ShapePos.NOT_INSIDE && shapePos != ShapePos.MIDDLE_CENTER) {
                    //如果是处于放大的阿虎
                    this.resizePressObj = it;
                    this.resizePos = shapePos;
                    this.resizeStartPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
                    return;
                }
            }
            //逆序
            //触发失去焦点
            Shape hitShape = getChoiceShape(hiPoint);

            if (hitShape != null) {
                //已经是被选中了的，无需再次选中也无需重置状态
                this.dragPressObj = hitShape;
                dragpressPoint = e.getPoint();
                this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            if (hitShape == null || !hitShape.isFocusing()) {
                aboveShape(hiPoint, shape -> {
                    boolean repm = shape.mousePressed(e);
                    //使得控件得到焦点
                    boolean repf = shape.focus(e);
                    return repm || repf;
                }, shape -> {
                    //使得控件失去焦点
                    //ctrl没有按下，
                    if (shape.isFocusing() && !ctrlPressing) {
                        return shape.blur(e);
                    }
                    return false;
                });
                aboveLine(hiPoint, line -> {
                    boolean repm = line.mousePressed(e);
                    boolean repf = line.focus(e);
                    return repm || repf;
                }, line -> {
                    if (line.isFocusing() && !ctrlPressing) {
                        PointLink pointLink = line.hoveringPoint(hiPoint);
                        if (pointLink != null) {
                            return false;
                        } else {
                            return line.blur(e);
                        }
                    }
                    return false;
                });
            }


            List<PropertyObject> selectObj = new ArrayList<>();
            selectObj.addAll(lines.stream().filter(Line::isSelected).collect(Collectors.toList()));
            selectObj.addAll(shapes.stream().filter(Shape::isDraging).collect(Collectors.toList()));

            if (selectObj.size() == 1) {
                fireShapeSelected(selectObj.get(0));
            } else if (selectObj.size() > 1) {
                fireShapeSelected(null);
            } else {
                fireShapeSelected(this);
            }

            repaint();
        }
        //取得虚拟对象，画画
    }

    /**
     * 线段模式的时候，鼠标击中的事件处理方法
     *
     * @param e
     */
    private void mousePressedModeLine(MouseEvent e) {
        if (lineStartObj == null) {
            for (Shape shape : shapes) {
                if (shape.attachable() && shape.isHover()) {
                    lineStartObj = shape;
                    shape.setHover(false);
                    repaint();
                    break;
                }
            }
        } else {
            //成为一条线
            Iterator<Shape> it = shapes.descendingIterator();
            Shape endShape = null;
            while (it.hasNext()) {
                Shape shape = it.next();
                if (!shape.visiable()) {
                    continue;
                }
                if (!shape.attachable()) {
                    continue;
                }
                if (shape == lineStartObj) {
                    continue;
                }
                if (shape.inside(retranslatePoint(e.getPoint().x, e.getPoint().y))) {
                    endShape = shape;
                    break;
                }
            }
            if (endShape == null) {
                lineStartObj = null;
                lineCursorTracker = null;
                repaint();
                return;
            }
            try {
                Line line = newLineClass.getConstructor().newInstance();
                //进行关联
                line.setStartShape(lineStartObj);
                line.setEndShape(endShape);
                //保证ID与name 被初始化
                setIdForLine(line);
                line.init(lineStartObj, endShape);
                lineStartObj.headOfLine.add(line);
                endShape.tailOfLine.add(line);
                lineStartObj = null;
                lineCursorTracker = null;
                addLine(line);
                repaint();
                if (SpdEditorDBState.getInstance().autoSave) {
                    fireSavedListener();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    /**
     * 新增线段
     *
     * @param line
     */
    private void addLine(Line line) {
        lines.add(line);
        shapes.add(line.getLabel());
        ArrayList<BaseState> afters = new ArrayList<>();
        afters.add(line.serialize());
        afters.add(line.getLabel().serialize());
        StateChange stateChange = new StateChange(null, new BaseGroupState(afters));
        recordStateChange(stateChange);
        recalcBoard();

    }

    /**
     * 获取被选中的图形
     *
     * @param p
     * @return
     */
    public Shape getChoiceShape(HiPoint p) {
        Iterator<Shape> sdi = shapes.descendingIterator();
        while (sdi.hasNext()) {

            //更加优先级应该是扩大与缩小吧
            //移动
            Shape item = sdi.next();
            if (!item.visiable()) {
                continue;
            }
            if (item.inside(p)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 添加图形,记录操作历史以便进行回滚
     *
     * @param shape
     */
    private void addShape(Shape shape) {
//        Random random = new Random();
//        shape.setBackgroundColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        shapes.add(shape);
        BaseState serialize = shape.serialize();
        recordStateChange(new StateChange(null, serialize));
        recalcBoard();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        dragging = false;
        if (whitespacePressing || ctrlPressing) {
            return;
        }
        alignXhitObj = null;
        alignYhitObj = null;
        shapes.forEach(Shape::resetAlign);
        if (MODE_LINE == mode) {
        } else if (MODE_NEW_SHAPE == mode) {
            mouseReleasedModeShape(e);
        } else if (MODE_DEFAULT == mode) {
            mouseReleasedModeDefault(e);
        } else if (mode == MODE_MOVE) {
            this.boardMoveStartPoint = null;
            this.boardMoveStartOriginPoint = null;
        }
//        fireSavedListener();

    }

    /**
     * 新增图形模式下，鼠标释放的操作
     *
     * @param e
     */
    private void mouseReleasedModeShape(MouseEvent e) {
        newShapeDragCurrentPoint = null;
        Rect rect = calcShape(newShapeStartPoint, retranslatePoint(e.getPoint().x, e.getPoint().y));
        double width = rect.getW();
        double height = rect.getH();
        if (width > 20 && height > 20) {
            try {
                Shape shape = newShapeClass.getDeclaredConstructor().newInstance();
                setIdForShape(shape);
                shape.init(rect);
                addShape(shape);
                if (SpdEditorDBState.getInstance().autoSave) {
                    fireSavedListener();
                }
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException instantiationException) {

                instantiationException.printStackTrace();
            }
        }
        repaint();
    }

    private void mouseReleasedModeDefault(MouseEvent e) {
        lines.descendingIterator().forEachRemaining(it -> {
            if (it.mousePressing) {
                it.mouseReleased(e);
            }
        });
        shapes.descendingIterator().forEachRemaining(it -> {
            if (it.isMousePressing()) {
                it.mouseReleased(e);
            }
        });
        //如果没有拖动，或者没有任何偏移，都需要将未集中的给反选
        if (dragFlag == false) {
            for (Shape shape : shapes) {
                if (!shape.isHover()) {
                    shape.blur(e);
                }
            }
        }
        if (dragFlag) {
            dragFlag = false;
        }
        if (resizePressObj != null) {
            doResize();
            repaint();
        }
        //记录当前resizing对象
        if (dragPressObj != null) {
            doDrag();
            if (SpdEditorDBState.getInstance().autoSave) {
                fireSavedListener();
            }
            repaint();
        }
        if (dragLinePoint != null) {
            //记录历史操作
            if (SpdEditorDBState.getInstance().autoSave) {
                fireSavedListener();
            }
            BaseState dragLineAfterState = dragLine.serialize();
            recordStateChange(new StateChange(dragLineStartState, dragLineAfterState));
            dragLine = null;
            dragLinePoint = null;
            dragLineStartPoint = null;
            dragLineStartState = null;
        }
    }

    private void recordStateChange(StateChange stateChange) {
        undoStack.clear();
        stateHistory.push(stateChange);
        triggerChartChangeListener();
    }


    /***
     * 真正执行图形拖动的功能
     */
    private void doDrag() {
        if (draggingOffsetY == 0 && draggingOffsetX == 0) {
            return;
        }
        //修改draing的属性

        List<BaseState> befores = new ArrayList<>();
        List<BaseState> afters = new ArrayList<>();
        Set<Line> headLines = new TreeSet<>();
        Set<Line> tailLines = new TreeSet<>();
        Set<Shape> dragedShapeUnique = shapes.stream().filter(Shape::isDraging).collect(Collectors.toSet());
        for (Shape shape : dragedShapeUnique) {
            if (!shape.visiable()) {
                continue;
            }
            if (shape instanceof Label) {
                Label label = (Label) shape;
                Line boundLine = label.getBoundLine();
                Shape ss = boundLine.getStartShape();
                Shape es = boundLine.getEndShape();
                if (dragedShapeUnique.contains(ss) && dragedShapeUnique.contains(es)) {
                    continue;
                } else if (dragedShapeUnique.contains(ss)) {
                    //需要单独根据对应shape移动的时候，
                    continue;
                } else if (dragedShapeUnique.contains(es)) {
                    continue;
                    //单独有一个点?
                }
            }

            befores.add(shape.serialize());

            shape.addXWithOffset(draggingOffsetX);
            shape.addYWithOffset(draggingOffsetY);

            afters.add(shape.serialize());
            headLines.addAll(shape.headOfLine);
            tailLines.addAll(shape.tailOfLine);


        }
        //针对所有的线段,进行平移，如果某一个线段startShape与endShape均在移动列表中，则，该线段中所有的点按照规则移动指定距离,delta x，并且记录操作历史
        headLines.retainAll(tailLines);
        //针对哪些有都移动的点，需要记录历史操作
        for (Line headLine : headLines) {
            BaseState serialize = headLine.serialize();
            befores.add(serialize);
            headLine.moveAllPointsWithOffset(draggingOffsetX, draggingOffsetY);
            afters.add(headLine.serialize());
        }

        //FIXME
        //回滚时候，线段中的点没能够同时的进行回滚
        //

        StateChange stateChange = new StateChange(new BaseGroupState(befores), new BaseGroupState(afters));
        recordStateChange(stateChange);
        recalcBoard();


//        dragPressObj = null;
        dragpressPoint = null;
        draggingOffsetX = 0;
        draggingOffsetY = 0;
    }

    /**
     * 指定图形的最大边框，以设置白板的合适宽高
     *
     * @param maxw
     * @param maxh
     */
    private void setBoardSize(double maxw, double maxh) {
        if (maxw > boardSize.getW()) {
            boardSize.setW(maxw + 40);
        } else {
            if (maxw > initBoardSize.getW()) {
                boardSize.setW(maxw + 40);
            } else {
                boardSize.setW(initBoardSize.getW());
            }
        }
        if (maxh > boardSize.getH()) {
            boardSize.setH(maxh + 40);
        } else {
            if (maxh > initBoardSize.getH()) {
                boardSize.setH(maxh + 40);
            } else {
                boardSize.setH(initBoardSize.getH());
            }
        }
    }

    /**
     * 执行调整大小的功能
     */
    private void doResize() {


        List<BaseState> befores = new ArrayList<>();
        List<BaseState> afters = new ArrayList<>();
        for (Shape ss : shapes) {
            if (!ss.visiable()) {
                continue;
            }
            //图形重置大小
            if (ss.isResizing()) {
                //批量处理
                Rect orgRect = ss.toRect();
                Rect rect = Shape.calcResizing(resizePos, resizeCurrentPoint, resizeStartPoint, orgRect);
                if (rect.getW() > 10 && rect.getH() > 10) {
                    befores.add(ss.serialize());
                    ss.setBoundsTo(rect);
                    afters.add(ss.serialize());
                }
            }
        }
        StateChange stateChange = new StateChange(new BaseGroupState(befores), new BaseGroupState(afters));
        recordStateChange(stateChange);
        recalcBoard();
        resizePressObj = null;
    }


    public void redo() {

        StateChange pop;
        synchronized (ChartPanel.class) {
            if (undoStack.size() <= 0) {
                chartChangeListenerList.forEach(l -> l.doChartChange(this));
                return;
            }
            pop = undoStack.pop();
            stateHistory.push(pop);
        }
        if (pop == null) {
            chartChangeListenerList.forEach(l -> l.doChartChange(this));
            return;
        }

        if (pop.before != null && pop.after != null) {
            //移动,再做移动
            if (pop.after instanceof ShapeState) {
                //图形移动
                ShapeState sbe = (ShapeState) pop.after;
                sbe.operated.restore(sbe);
            } else if (pop.after instanceof BaseGroupState) {
                BaseGroupState sbe = (BaseGroupState) pop.after;
                List<BaseState> operateds = sbe.operateds;
                for (BaseState baseState : operateds) {
                    baseState.operated.restore(baseState);
                }
            } else if (pop.after instanceof LineState) {
                LineState after = (LineState) pop.after;
                after.operated.restore(after);
            }

        } else if (pop.before == null && pop.after != null) {
            //新增
            if (pop.after instanceof ShapeState) {
                ShapeState sbe = (ShapeState) pop.after;
                shapes.add((Shape) sbe.operated);
            } else if (pop.after instanceof LineState) {
                LineState ls = (LineState) pop.after;
                lines.add((Line) ls.operated);
            } else if (pop.after instanceof BaseGroupState) {
                BaseGroupState after = (BaseGroupState) pop.after;
                List<BaseState> operateds = after.operateds;
                for (BaseState baseState : operateds) {
                    if (baseState instanceof ShapeState) {
                        ShapeState sbe = (ShapeState) baseState;
                        shapes.add((Shape) sbe.operated);
                    } else if (baseState instanceof LineState) {
                        LineState ls = (LineState) baseState;
                        lines.add((Line) ls.operated);
                    }
                }

            }

        } else if (pop.before != null && pop.after == null) {
            //删除
            if (pop.before instanceof BaseGroupState) {
                BaseGroupState sge = (BaseGroupState) pop.before;
                List<BaseState> operateds = sge.operateds;
                for (BaseState baseState : operateds) {
                    if (baseState instanceof ShapeState) {
                        ShapeState sbe = (ShapeState) baseState;
                        shapes.remove((Shape) sbe.operated);
                    } else if (baseState instanceof LineState) {
                        LineState ls = (LineState) baseState;
                        lines.remove((Line) ls.operated);
                    }
                }
            }
        }
        recalcBoard();
        repaint();
        if (SpdEditorDBState.getInstance().autoSave) {
            fireSavedListener();
        }
        chartChangeListenerList.forEach(l -> l.doChartChange(this));

    }

    /**
     * 获取历史记录条目的数量
     *
     * @return
     */
    public int getHistorySize() {
        return stateHistory.size();
    }

    /**
     * 获取最后一个元素的hash
     *
     * @return
     */
    public int getTopHistoryHash() {
        if (stateHistory.size() == 0) {
            return 0;
        }
        return stateHistory.getFirst().hashCode();
    }

    public int getCurrentHistoryHash() {
        return currentHistoryHash;
    }

    /**
     * 触发图形改变监听器
     */
    private void triggerChartChangeListener() {
        chartChangeListenerList.forEach(l -> l.doChartChange(this));
    }

    /**
     * 回撤
     */
    public void undo() {

        StateChange pop;
        synchronized (ChartPanel.class) {
            if (stateHistory.size() <= 0) {
                chartChangeListenerList.forEach(l -> l.doChartChange(this));
                return;
            }
            pop = stateHistory.pop();
            undoStack.push(pop);
        }
        if (pop == null) {

            chartChangeListenerList.forEach(l -> l.doChartChange(this));
            return;
        }
        if (pop.before != null && pop.after != null) {
            //移动
            if (pop.before instanceof ShapeState) {
                //图形移动
                ShapeState sbe = (ShapeState) pop.before;
                sbe.operated.restore(sbe);
            } else if (pop.before instanceof BaseGroupState) {
                BaseGroupState sbe = (BaseGroupState) pop.before;
                List<BaseState> operateds = sbe.operateds;
                for (BaseState baseState : operateds) {
                    baseState.operated.restore(baseState);
                }
            } else if (pop.before instanceof LineState) {
                LineState before = (LineState) pop.before;
                before.operated.restore(before);
            }

        } else if (pop.before == null && pop.after != null) {
            //新增
            if (pop.after instanceof ShapeState) {
                ShapeState sbe = (ShapeState) pop.after;
                shapes.remove(sbe.operated);
            } else if (pop.after instanceof LineState) {
                LineState ls = (LineState) pop.after;
                lines.remove(ls.operated);
            } else if (pop.after instanceof BaseGroupState) {
                BaseGroupState after = (BaseGroupState) pop.after;
                List<BaseState> operateds = after.operateds;
                for (BaseState baseState : operateds) {
                    if (baseState instanceof ShapeState) {
                        ShapeState sbe = (ShapeState) baseState;
                        shapes.remove(sbe.operated);
                    } else if (baseState instanceof LineState) {
                        LineState ls = (LineState) baseState;
                        lines.remove(ls.operated);
                    }
                }

            }

        } else if (pop.before != null && pop.after == null) {
            //删除
            if (pop.before instanceof BaseGroupState) {
                BaseGroupState sge = (BaseGroupState) pop.before;
                List<BaseState> operateds = sge.operateds;
                for (BaseState baseState : operateds) {
                    if (baseState instanceof ShapeState) {
                        ShapeState sbe = (ShapeState) baseState;
                        shapes.add((Shape) sbe.operated);
                    } else if (baseState instanceof LineState) {
                        LineState ls = (LineState) baseState;
                        lines.add((Line) ls.operated);
                    }
                }
            }
        }
        recalcBoard();

        repaint();
        if (SpdEditorDBState.getInstance().autoSave) {
            fireSavedListener();
        }
        chartChangeListenerList.forEach(l -> l.doChartChange(this));

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    /**
     * 鼠标对线段进行拖动的时候，所执行的函数
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        this.dragging = true;
        dragFlag = true;

        if (ctrlPressing) {
            return;
        }
        //中间对齐
        if (mode == MODE_MOVE || whitespacePressing) {
            if (boardMoveStartPoint != null) {
                boardMove(e);
                repaint();
                return;
            }
        }
        if (MODE_NEW_SHAPE == mode) {
            newShapeDragCurrentPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
            repaint();
            return;
        }
        if (MODE_DEFAULT == mode) {
            //被调整大小的对象
            if (resizePressObj != null) {
                resizingShape(e);
                repaint();
                return;
            }
            //被拖动线段的点不是空的情况下，需要
            if (dragLineStartPoint != null) {
                //不宜有过多的复杂逻辑
                dragingLine(e);
                repaint();
                return;

            }
            //对象拖动逻辑主要控制拖动的时候与其他元素的对齐功能
            if (dragPressObj != null) {
                dragingAlign(e);
                repaint();
                return;
            }
        }
    }

    /**
     * 画板移动的时候，调用的函数
     * 在hand模式下，进入画板移动模式
     * 根据boardMoveCurrentPoint -boardMoveStartPoint 即end-start得出δmove
     * 要知道，此时δmove是屏幕上移动的距离，故而，实际距离是δmove/scale=δx
     * end-start=δx
     * δx/scale=δrx
     * boardnew-boarold=δrx得
     * boardnew=boardold+δrx
     *
     * @param e
     */
    private void boardMove(MouseEvent e) {
        //
        boardMoveCurrentPoint = e.getPoint();
        double movedX = boardMoveCurrentPoint.x - boardMoveStartPoint.x;
        double movedY = boardMoveCurrentPoint.y - boardMoveStartPoint.y;
        Size delta = retranslateSize(movedX, movedY);
        originalPoint.x = boardMoveStartOriginPoint.x + delta.getW();
        originalPoint.y = boardMoveStartOriginPoint.y + delta.getH();
    }

    /**
     * 图形调整大小时候的回调函数
     *
     * @param e
     */
    private void resizingShape(MouseEvent e) {
        HiPoint hiPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
        Rect moveBefore = resizePressObj.toRect();
        Rect moveAfter = Shape.calcResizing(resizePos, hiPoint, resizeStartPoint, moveBefore);
//                是否与任何的一个图形的某一条边发生重合
        //将resizeCurrentPoint进行充值
        //与哪个图形的边发生了重合?
        //需要计算resize的当前点应该设置为多少
        //计算应该被对齐的线的坐标
        Double xmovingLine = LineUtils.calcOfMoveingLine(moveBefore.getX(), moveBefore.getX() + moveBefore.getW(), moveAfter.getX(), moveAfter.getX() + moveAfter.getW());
        Double ymovingLine = LineUtils.calcOfMoveingLine(moveBefore.getY(), moveBefore.getY() + moveBefore.getH(), moveAfter.getY(), moveAfter.getY() + moveAfter.getH());
        alignXhitObj = null;
        alignYhitObj = null;
        for (Shape shape : shapes) {

            if (shape == resizePressObj) {
                continue;
            }

            {
                MoveType moveType = Shape.calcMovePosOfX(resizePos);
                Double fposx = calcAlign(xmovingLine, shape.getLeft(), shape.getRight());
                if (fposx != null) {
                    //标识移动已经命中了边线
                    alignXhitObj = shape;
                    alignX = fposx;
                    double v = ChartPanel.calcFinalPosition(moveType, fposx, moveBefore.getX(), moveBefore.getX() + moveBefore.getW(), resizeStartPoint.x);
                    hiPoint.x = v;
                }
            }
            {
                MoveType moveType = Shape.calcMovePosOfY(resizePos);
                Double fposy = calcAlign(ymovingLine, shape.getTop(), shape.getBottom());
                if (fposy != null) {
                    //标识移动已经命中了边线
                    alignYhitObj = shape;
                    alignY = fposy;
                    double v = ChartPanel.calcFinalPosition(moveType, fposy, moveBefore.getY(), moveBefore.getY() + moveBefore.getH(), resizeStartPoint.y);
                    hiPoint.y = v;
                }
            }
        }
        resizeCurrentPoint = hiPoint;
    }


    /**
     * 控制线段被拖动的时候的操作
     *
     * @param e
     */
    private void dragingLine(MouseEvent e) {
        dragLineCurrentPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
        //先拖动嘛
        //是否需要将点加入
        //暂时不判断此条件
        HiPoint val = dragLinePoint.getVal();
        boolean needtoRemove = false;
        //是否要删除
        double v = LineUtils.calcCrossAngleOfTwoLine(dragLinePoint.getPrev(), dragLinePoint.getVal(), dragLinePoint.getPrev(), dragLinePoint.getNext());


        //
//        double crossDirection = LineUtils.calcCrossDirectionOfTwoLine(dragLinePoint.getPrev(), dragLinePoint.getVal(), dragLinePoint.getPrev(), dragLinePoint.getNext());
//        System.out.println("cross direction"+crossDirection);

        //
        double deg = LineUtils.transferToDeg(v);
        //超过区域也不用管

        double offsetX = (dragLineCurrentPoint.x - dragLineStartPoint.x);
        double offsetY = (dragLineCurrentPoint.y - dragLineStartPoint.y);
        //控制offset
        double finalX = dragLinePoint.getVirtual().x + offsetX;
        double finalY = dragLinePoint.getVirtual().y + offsetY;
        //与x轴交点

        //两个边线的夹角
        if (deg <= 5) {
            double vreverse = LineUtils.calcCrossAngleOfTwoLine(dragLinePoint.getNext(), dragLinePoint.getVal(), dragLinePoint.getNext(), dragLinePoint.getPrev());
            double degreverse = LineUtils.transferToDeg(vreverse);
            if (degreverse <= 10) {
                needtoRemove = true;
            }
        }
        HiPoint prev = dragLinePoint.getPrev();
        if (dragLinePoint.isPrevIsHead()) {
            prev = dragLinePoint.getHead();
        }
        //计算与横轴，纵轴的夹角
        double vHorizontal = LineUtils.calcCrossAngleOfTwoLine(prev, new HiPoint(finalX, finalY), new HiPoint(0, 0), new HiPoint(1000, 0));
        double vVertical = LineUtils.calcCrossAngleOfTwoLine(prev, new HiPoint(finalX, finalY), new HiPoint(0, 0), new HiPoint(0, 1000));
        double degHorizoncal = LineUtils.transferToDeg(vHorizontal);
        double degVertical = LineUtils.transferToDeg(vVertical);
        double degAllow = 2.5;
        if (degHorizoncal < degAllow || 180 - degHorizoncal < degAllow) {
            finalY = prev.y;
        } else if (degVertical < degAllow || 180 - degVertical < degAllow) {
            finalX = prev.x;
        }

        HiPoint next = dragLinePoint.getNext();
        if (dragLinePoint.isNextIsTail()) {
            next = dragLinePoint.getTail();
        }
        //计算与横轴，纵轴的夹角
        double nvHorizontal = LineUtils.calcCrossAngleOfTwoLine(next, new HiPoint(finalX, finalY), new HiPoint(0, 0), new HiPoint(1000, 0));
        double nvVertical = LineUtils.calcCrossAngleOfTwoLine(next, new HiPoint(finalX, finalY), new HiPoint(0, 0), new HiPoint(0, 1000));

        double ndegHorizoncal = LineUtils.transferToDeg(nvHorizontal);
        double ndegVertical = LineUtils.transferToDeg(nvVertical);

        if (ndegHorizoncal < degAllow || 180 - ndegHorizoncal < degAllow) {
            finalY = next.y;
        } else if (ndegVertical < degAllow || 180 - ndegVertical < degAllow) {
            finalX = next.x;
        }

        LinkedList<HiPoint> points = dragLine.getPoints();

        if (!points.contains(val)) {
            if (!needtoRemove) {
                //不包含才新增
                //节点插入位置错误，需要在
                int i = points.indexOf(dragLinePoint.getNext());
                if (i == -1) {
                    points.add(val);
                } else {
                    points.add(i, val);
                }
            }
        } else {
            if (needtoRemove) {
                points.remove(val);
            }
        }
        val.x = finalX;
        val.y = finalY;
    }

    /**
     * 控制拖动时候的对齐操作
     *
     * @param e
     */
    private void dragingAlign(MouseEvent e) {
        //强制offset
        //设 参照图形（x1,y1） 拖动图形（x2,y2） 点击点(x3,y3) 拖动实时点(x4,y4)
        // |x1-x2-(x4-x3)|<5时，表示需要吸附
        // 那么 x1-x2 = x4-x3 锁定了这个距离
        // 而offsetX = x4 -x3 ,即 offset=x1-x2 在这种情况下就给他们锁定了
        //右边对齐的情况 （x2+=x2.width）
        if (dragpressPoint == null) {
            return;
        }
        double offsetX = (e.getPoint().x - dragpressPoint.x) / scale;
        double offsetY = (e.getPoint().y - dragpressPoint.y) / scale;

        boolean hitx = false;
        boolean hity = false;

        Rect movebefore = dragPressObj.toRect();
        Rect moving = new Rect(movebefore.getX() + offsetX, movebefore.getY() + offsetY, movebefore.getW(), movebefore.getH());
        shapes.forEach(Shape::resetAlign);

        HiPoint movc = moving.getCenterPoint();
        List<Shape> sortlist = new ArrayList<Shape>(shapes);
        sortlist = sortlist.stream().filter(Shape::alignRefAble).collect(Collectors.toList());
        sortlist.sort((o1, o2) -> {
            Double d1 = LineUtils.distance(movc, o1.getCenterPoint());
            double d2 = LineUtils.distance(movc, o2.getCenterPoint());
            return d1.compareTo(d2);
        });
        //对需要遍历的图形按照距离远近进行排序

        for (Shape shape : sortlist) {
            //计算出当前对象的实际参考位置
            if (shape == dragPressObj && !shape.alignSelf()) {
                continue;
            }
            Rect shapeR = shape.toRect();
            Rect rel = new Rect(shapeR.getX(), shapeR.getY(), shapeR.getW(), shapeR.getH());
            //这里其实应该拿距离最小的的优先
            if (!hitx) {
                int posX = Shape.relPosOfX(rel, moving, alignDistance);
                if (posX != Shape.REL_POS_NAIL) {
                    hitx = true;
                    //最小距离只有在命中的时候，才去计算
                    //其实应该优先考虑居中对齐呀
                    int[] pos = {Shape.REL_POS_CENTER, Shape.REL_POS_LEFT_IN, Shape.REL_POS_LEFT_OUT, Shape.REL_POS_RIGHT_OUT, Shape.REL_POS_RIGHT_IN};
                    int anyhit = 0;
                    for (int po : pos) {
                        if ((posX & po) != Shape.REL_POS_NAIL) {
                            anyhit = po;
                            break;
                        }
                    }
                    //根据不同的命中位置，计算出应该的命中边？
                    //对齐属性与对象绑定在一起？可否,然后由控件自身
                    shape.setAlign(Alignable.REL_VERTICAL | posX);
                    offsetX = Shape.targetPosOfX(anyhit, rel, moving) - movebefore.getX();
                }
            }

            //对于最小距离进行处理


            if (!hity) {
                int posY = Shape.relPosOfY(rel, moving, alignDistance);
                if (posY != Shape.REL_POS_NAIL) {
                    hity = true;
                    int[] pos = {Shape.REL_POS_CENTER, Shape.REL_POS_LEFT_IN, Shape.REL_POS_LEFT_OUT, Shape.REL_POS_RIGHT_OUT, Shape.REL_POS_RIGHT_IN};
                    int anyhit = 0;
                    for (int po : pos) {
                        //命中某一条边的情况下
                        if ((posY & po) != Shape.REL_POS_NAIL) {
                            anyhit = po;
                            break;
                        }
                    }
                    shape.setAlign(Alignable.REL_HORIZONTAL | posY);
                    offsetY = Shape.targetPosOfY(anyhit, rel, moving) - movebefore.getY();
                }
            }

            if (hitx && hity) {
                break;
            }

        }
        //与强制line
        //拥有共同对齐的对象，现在就是

        this.draggingOffsetX = offsetX;
        this.draggingOffsetY = offsetY;
    }


    /**
     * 计算移动中的线是否与图形的左边还是右边发生了重合，一旦重合，将重合边线偏移量返回
     *
     * @param xmovingLine 移动的线段
     * @param shapeLeft   移动对象的左边线
     * @param shapeRight  移动对象的右边线
     * @return
     */
    private Double calcAlign(Double xmovingLine, Double shapeLeft, Double shapeRight) {
        if (xmovingLine != null) {
            if (Shape.aboutTo(shapeLeft, xmovingLine, alignDistance)) {
                //matchXLeft
                return shapeLeft;
            } else if (Shape.aboutTo(shapeRight, xmovingLine, alignDistance)) {
                return shapeRight;
                //matchXRight
            } else if (Shape.aboutTo((shapeLeft + shapeRight) / 2, xmovingLine, alignDistance)) {
                return (shapeRight + shapeLeft) / 2;
            }

        }
        return null;
    }

    /**
     * 我们现在需要求解resizeEndPoint.x的坐标
     * 我们已知δx=resizeEndPoint.x-resizeStartPoint.x
     * 图形被拉升一共有两个情况
     * 1 左边线被拖动, 直至与对齐目标线相接近时
     * 那么开始坐标就是resizePressObj.x 终止坐标是alignX,我们使用end-start=δx
     * alignx-resizePressObj.x=δx=resizeEndPoint.x-resizeStartPoint.x
     * δx=targetPos-startPOs=pos-resizeRight=resizeEnd-resizeStart
     * 因为最终需要求解resizeEndPoint.x的实际坐标
     * 2 右边线被拖动，直至与对齐目标线相接近时
     * 按照最终坐标减去开始坐标为δ值
     * δx=targetPos-startPos=pos-resizeRight=resizeEnd-resizeStart
     */
    private static double calcFinalPosition(MoveType moveType, double pos, double resizeLeft, double resizeRight, double resizeStartPos) {
        if (MoveType.RIGHT.equals(moveType)) {
            return pos - resizeRight + resizeStartPos;
        } else if (MoveType.LEFT.equals(moveType)) {
            return pos - resizeLeft + resizeStartPos;
        }
        throw new RuntimeException("this should not be happen");
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouseCurrentPoint = e.getPoint();
        if (whitespacePressing) {
            return;
        }
        if (MODE_DEFAULT == mode) {
            mouseMovedModeDefault(e);
        } else if (MODE_LINE == mode) {
            mouseMovedModeLine(e);
        }
    }

    /**
     * 线段模式下，鼠标移动
     *
     * @param e
     */
    private void mouseMovedModeLine(MouseEvent e) {
        //如果是线模式的
        HiPoint cpoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
        boolean triggerRepaint = false;
        if (lineStartObj != null) {
            lineCursorTracker = cpoint;
            triggerRepaint = true;
        }


        HiPoint point = cpoint;
        //逆序
        Shape change = null;

        //给未hover元素去hover
        //如果元素处于hover 状态，新增hover状态
        for (Shape shape : shapes) {
            if (shape == lineStartObj) {
                continue;
            }
            if (shape.inside(point)) {
                //只有最后一个会被选中
                change = shape;
            }
            //对于所有的元素进行状态重置
            if (shape.isHover() == true) {
                shape.setHover(false);
                triggerRepaint = true;
            }
        }
        if (change != null) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (change.isHover() != true) {
                change.setHover(true);
                triggerRepaint = true;
            }
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        if (triggerRepaint) {
            repaint();
        }
    }

    /**
     * 默认移动模式下，鼠标移动事件
     *
     * @param e
     */
    private void mouseMovedModeDefault(MouseEvent e) {
        //重置默认信息
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        HiPoint cpoint = retranslatePoint(e.getPoint().x, e.getPoint().y);

        //任意一个被渲染，就不再选中后面的元素
        boolean triggerRepaint = false;
        if (aboveShape(cpoint, shape -> shape.updateHoverStatus(true, e), shape -> shape.updateHoverStatus(false, e))) {
            triggerRepaint = true;
        }
        if (aboveLine(cpoint, line -> line.updateHoverStatus(true, e), line -> line.updateHoverStatus(false, e))) {
            triggerRepaint = true;
        }

        //处理在链表上的情况以及在链表中间点的情况
        for (Line line : lines) {
            if (line.isSelected()) {
                PointLink p = line.hoveringPoint(cpoint);
                if (p != null) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
        }

        if (triggerRepaint) {
            repaint();
        }
        //如果元素处于九点选择状态

        for (Iterator<Shape> it = shapes.descendingIterator();
             it.hasNext(); ) {
            Shape shape = it.next();
            if (!shape.isResizing()) {
                continue;
            }
            //根据鼠标悬浮状态来进行判断
            //判断是否悬浮于9个点里面
            ShapePos shapePos = ShapeUtils.insideDot(cpoint, shape.toRect());
            switch (shapePos) {
                case TOP_LEFT:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    break;
                case TOP_CENTER:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                    break;
                case TOP_RIGHT:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    break;
                case MIDDLE_LEFT:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    break;
                case MIDDLE_CENTER:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    break;
                case MIDDLE_RIGHT:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    break;
                case BOTTOM_LEFT:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    break;
                case BOTTOM_CENTER:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                    break;
                case BOTTOM_RIGHT:
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    break;
                case NOT_INSIDE:
                    if (shape.isHover()) {
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                    break;
            }
            if (shapePos != ShapePos.NOT_INSIDE && shapePos != ShapePos.MIDDLE_CENTER) {

                break;
            }


        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //删除

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (ctrlPressing) {
            if (keyCode == KeyEvent.VK_A) {
                //Ctrl+A
                for (Shape shape : shapes) {
                    shape.focus(null);
                }
            } else if (keyCode == KeyEvent.VK_Z) {
                //Ctrl+z
                undo();
            } else if (keyCode == KeyEvent.VK_Y) {
                //Ctrl+Y
                redo();
            } else if (keyCode == KeyEvent.VK_C) {
                //Ctrl+C
                copy();
            } else if (keyCode == KeyEvent.VK_V) {
                //Ctrl+V
                paste();
            } else if (keyCode == KeyEvent.VK_S) {
                //Ctrl+S
                save();
            }

        }

        if (KeyEvent.VK_SHIFT == e.getKeyCode()) {
            if (!shiftPressing) {
                shiftPressing = true;
            }
        }
        if (KeyEvent.VK_CONTROL == e.getKeyCode()) {
            //ctrl控制键
            if (ctrlPressing != true) {
                ctrlPressing = true;
                markCursor();
            }
        } else if (KeyEvent.VK_SPACE == e.getKeyCode()) {
            //空格键，标识移动
            markCursor();
            whitespacePressing = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
            //esc键
            if (mode == MODE_LINE) {
                lineStartObj = null;
                lineCursorTracker = null;
                repaint();
            } else if (mode == MODE_NEW_SHAPE) {

            } else if (mode == MODE_DEFAULT) {
                for (Line line : lines) {
                    if (line.isSelected()) {
                        line.setSelected(false);
                    }
                }
                for (Shape shape : shapes) {
                    shape.setDraging(false);
                    shape.setResizing(false);
                }
            }
        } else if (KeyEvent.VK_DELETE == e.getKeyCode()) {
            shapeDelete();
        } else if (KeyEvent.VK_LEFT == e.getKeyCode()) {
            //LEFT
            draggingOffsetX = -1;
            draggingOffsetY = 0;
            doDrag();
        } else if (KeyEvent.VK_UP == e.getKeyCode()) {
            //UP
            draggingOffsetX = 0;
            draggingOffsetY = -1;
            doDrag();
        } else if (KeyEvent.VK_RIGHT == e.getKeyCode()) {
            //RIGHT
            draggingOffsetX = 1;
            draggingOffsetY = 0;
            doDrag();
        } else if (KeyEvent.VK_DOWN == e.getKeyCode()) {
            //DOWN
            draggingOffsetX = 0;
            draggingOffsetY = 1;
            doDrag();
        }

        repaint();
    }

    private void paste() {
        List<BaseState> afters = new ArrayList<>();

        Point location = MouseInfo.getPointerInfo().getLocation();
        HiPoint copyMouseEndPoint = retranslatePoint(location);
        double diffy = copyMouseEndPoint.y - copyMouseStartPoint.y;
        double diffx = copyMouseEndPoint.x - copyMouseStartPoint.x;
        if (diffx < 5 && diffy < 5) {
            diffx = 10;
            diffy = 10;
        }
        for (Shape shape : copyList) {
            shape.addXWithOffset(diffx);
            shape.addYWithOffset(diffy);
            //记录历史操作
            setIdForShape(shape);
            shapes.add(shape);
            afters.add(shape.serialize());
        }
        for (Line line : copyLines) {
            line.addXWithOffset(diffx);
            line.addYWithOffset(diffy);
            setIdForLine(line);
            lines.add(line);
            afters.add(line.serialize());
        }
        copyList.clear();
        recordStateChange(new StateChange(null, new BaseGroupState(afters)));
        copyMouseStartPoint = null;
    }

    private void copy() {
        copyList.clear();
        Point location = MouseInfo.getPointerInfo().getLocation();
        copyMouseStartPoint = retranslatePoint(location);
        //新图形，旧图形映射集合
        Map<Shape, Shape> oldShapeNewShapeMap = new HashMap<>();
        for (Shape shape : shapes) {
            try {
                if (shape instanceof Label) {
                    continue;
                }
                if (!shape.isDraging()) {
                    continue;
                }
                //只有两端都被选中的线段有必要进行拷贝，否则均不拷贝
                //如果某条线段已经
                Shape clone = (Shape) shape.clone();
                copyList.add(clone);
                oldShapeNewShapeMap.put(shape, clone);
            } catch (CloneNotSupportedException cloneNotSupportedException) {
                cloneNotSupportedException.printStackTrace();
            }
        }
        //如果线段被选中，同事两端的对象被选中
        for (Line line : lines) {
            try {
                if (!line.isSelected()) {
                    continue;
                }
                //是否添加需要查看两个绑定的图形是否被选中了
                Shape startShape = line.getStartShape();
                if (!oldShapeNewShapeMap.containsKey(startShape)) {
                    continue;
                }
                Shape endShape = line.getEndShape();
                if (!oldShapeNewShapeMap.containsKey(endShape)) {
                    continue;
                }
                Line clone = (Line) line.clone();
                clone.setStartShape(oldShapeNewShapeMap.get(startShape));
                clone.setEndShape(oldShapeNewShapeMap.get(endShape));
                clone.setSourceRef(clone.getStartShape().getName());
                clone.setTargetRef(clone.getEndShape().getName());
                copyLines.add(clone);
            } catch (CloneNotSupportedException cloneNotSupportedException) {
                cloneNotSupportedException.printStackTrace();
            }
        }


    }

    /**
     * 图形删除的操作回调函数
     */
    public void shapeDelete() {
        boolean removed = false;
        List<Shape> collect = shapes.stream().filter(it -> it.isDraging()).collect(Collectors.toList());
        List<BaseState> befores = new ArrayList<>();
        for (Shape shape : collect) {
            if (shape instanceof Label) {
                Label label = (Label) shape;
                Line boundLine = label.getBoundLine();
                boundLine.setSelected(true);
            } else {
                List<Line> headOfLine = shape.headOfLine;
                ///关联删除
                for (Line line : headOfLine) {
                    if (lines.contains(line)) {
                        removeLine(befores, line);
                        removed = true;
                    }
                }

                //关联删除
                List<Line> tailOfLine = shape.tailOfLine;
                for (Line line : tailOfLine) {
                    if (lines.contains(line)) {
                        removeLine(befores, line);
                        removed = true;

                    }
                }
                befores.add(shape.serialize());
                shapes.remove(shape);
                removed = true;

            }

        }
        List<Line> ls = lines.stream().filter(it -> it.isSelected()).collect(Collectors.toList());
        for (Line li : ls) {
            removeLine(befores, li);
            removed = true;

        }
        recordStateChange(new StateChange(new BaseGroupState(befores), null));
        if (removed) {
            if (SpdEditorDBState.getInstance().autoSave) {
                fireSavedListener();
            }
        }
    }

    /**
     * 移除线段所需要执行的操作
     *
     * @param befores
     * @param line
     */
    private void removeLine(List<BaseState> befores, Line line) {
        Label label = line.getLabel();
        if (shapes.contains(label)) {
            befores.add(label.serialize());
            shapes.remove(label);
        }
        befores.add(line.serialize());
        lines.remove(line);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (KeyEvent.VK_CONTROL == e.getKeyCode()) {
            ctrlPressing = false;
            setCursor(getCursorByMode(mode));
        } else if (KeyEvent.VK_SPACE == e.getKeyCode()) {
            whitespacePressing = false;
            setCursor(getCursorByMode(mode));
        } else if (KeyEvent.VK_SHIFT == e.getKeyCode()) {
            //shift
            if (shiftPressing) {
                shiftPressing = false;
            }
        }
        repaint();
    }

    private void markCursor() {
        markCursor = this.getCursor();
    }

    private void resetCursor() {
        this.setCursor(markCursor);
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (mode == MODE_MOVE || ctrlPressing) {
            double scaleBefore = this.scale;
            double decreasePercent = e.getPreciseWheelRotation() / 10 / 1.5;

            double scaleAfter = this.scale - this.scale * decreasePercent;
            if (scaleAfter <= 0.1) {
                scaleAfter = 0.1;
            } else if (scaleAfter > 10) {
                scaleAfter = 10;
            } else {
                HiPoint rPoint = retranslatePoint(e.getPoint().x, e.getPoint().y);
                originalPoint.x += (scaleBefore - scaleAfter) / scaleBefore * (rPoint.x + originalPoint.x);
                originalPoint.y += (scaleBefore - scaleAfter) / scaleBefore * (rPoint.y + originalPoint.y);
//                if (originalPoint.x < 0) {
//                    originalPoint.x = 0;
//                }
//                if (originalPoint.y < 0) {
//                    originalPoint.y = 0;
//                }

            }

            this.scale = scaleAfter;
            //重置origin
            this.repaint();
        } else {
            final AtomicInteger starti = new AtomicInteger(0);
            final Timer timer = new Timer(1000 / 60, null);
            //经过调教，此规律是最顺滑的
            final long time = 15;
            final long pace = 60;
            timer.addActionListener(ee -> {
                double next = Math.sin(Math.PI / 2 * (starti.get() + 1) / time);
                double prev = Math.sin(Math.PI / 2 * starti.get() / time);
                double offset = retranslate((next - prev) * pace * e.getPreciseWheelRotation());
                if (shiftPressing) {
                    originalPoint.x -= offset;
                } else {
                    originalPoint.y -= offset;
                }
                if (starti.incrementAndGet() > time) {
                    timer.stop();
                }
                repaint();
            });
            timer.start();
            //上下滚动
        }
    }

    /**
     * 重置原点
     */
    public void resetOriginalPoint() {
        this.originalPoint.x = initialOriginalPoint.x;
        this.originalPoint.y = initialOriginalPoint.y;
        repaint();
    }

    public void markColor() {
        markColors.push(currentGraphic.getColor());
    }

    public void markFont() {
        markFonts.push(currentGraphic.getFont());
    }

    public void resetColor() {
        Color pop = markColors.pop();
        if (pop != null) {
            currentGraphic.setColor(pop);
        } else {
            throw new RuntimeException("no remain color in markColors");
        }
    }

    public void resetFont() {
        Font pop = markFonts.pop();
        if (pop != null) {
            currentGraphic.setFont(pop);
        } else {
            throw new RuntimeException("no remain color in markColors");
        }
    }

    public Color getColor() {
        return currentGraphic.getColor();
    }

    public void resetStroke() {
        if (currentStroke != null) {
            Graphics2D g = (Graphics2D) this.currentGraphic;
            g.setStroke(this.currentStroke);
        }
    }

    public void markStroke() {
        this.currentStroke = ((Graphics2D) this.currentGraphic).getStroke();
    }

    public void setStroke(BasicStroke basicStroke) {
        ((Graphics2D) this.currentGraphic).setStroke(basicStroke);
    }

    public void resetScale() {
        scale = 1;
        repaint();
    }

    private int currentHistoryHash = 0;


    /**
     * 触发保存监听器，用于对当前流程进行保存
     */
    public void fireSavedListener() {
        this.currentHistoryHash = getTopHistoryHash();
        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setId(id);
        processDefinition.setName(name);
        processDefinition.setShapes(this.shapes);
        processDefinition.setLines(this.lines);
        byte[] bytes = TemplateLoaderImpl.getInstance().saveToBytes(processDefinition);
        for (ProcessSaveListener saveListener : processSaveListener) {
            try {
                saveListener.save(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        triggerChartChangeListener();
    }

    public void save() {
        JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setCurrentDirectory(new File("E:\\RTC_work\\DEV3\\xfunds_201608\\import\\"));
//        jFileChooser.setCurrentDirectory(new File("./"));
        jFileChooser.showDialog(new JLabel(), "请选择");
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile == null) {
            return;
        }
        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setId(id);
        processDefinition.setName(name);
        processDefinition.setShapes(this.shapes);
        processDefinition.setLines(this.lines);
        TemplateLoaderImpl.getInstance().saveToFile(processDefinition, selectedFile.getAbsolutePath());
    }

    /**
     * 从字符串中进行加载
     *
     * @param inputStream
     */
    public void loadFromInputStream(InputStream inputStream) {
        try {

            ProcessDefinition processDefinition = TemplateLoaderImpl.getInstance().loadFromInputStream(inputStream);
            if (processDefinition == null) {
                JOptionPane.showMessageDialog(this, "所选文件格式有误");
                return;
            }
            this.id = processDefinition.getId();
            this.name = processDefinition.getName();
            autofixedOffset(processDefinition);
            List<Shape> shapes = processDefinition.getShapes();
            List<Line> lines = processDefinition.getLines();
            this.shapes.clear();
            this.lines.clear();
            List<Label> labels = lines.stream().filter(it -> it.getLabel() != null).map(it -> it.getLabel()).collect(Collectors.toList());
            this.shapes.addAll(shapes);
            this.lines.addAll(lines);
            this.shapes.addAll(labels);

            //记录历史？算了吧
            recalcBoard();
            fireShapeSelected(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "解析时候发生异常:" + e.getMessage());
        }
    }


    /**
     * FIXME
     * 加载的部分逻辑可能需要修改
     * 1.不是从file进行读取而是从VirtualFile读取
     * 2.VirtualFile变动的时候，需要跟着变动，例如，外部修改的时候，需要及时从外部文件进行同步
     */
    public void loadFromFile(File file) {

        loadFromFile(file);
    }

    /**
     * 清空画板
     */
    public void clear() {
        this.shapes.clear();
        this.lines.clear();
        recalcBoard();
    }

    public void load() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setCurrentDirectory(new File("E:\\RTC_work\\DEV3\\xfunds_201608\\import\\"));
        jFileChooser.showDialog(new JLabel(), "请选择");
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile == null) {
            return;
        }
        loadFromFile(selectedFile);
    }


    public void autofixedOffset(ProcessDefinition pd) {
        double minx = 0;
        double miny = 0;
        for (Shape shape : pd.getShapes()) {
            if (shape.getX() < minx) {
                minx = shape.getX();
            }
            if (shape.getY() < miny) {
                miny = shape.getY();
            }
        }
        for (Line line : pd.getLines()) {
            for (HiPoint point : line.getPoints()) {
                if (point.x < minx) {
                    minx = point.x;
                }
                if (point.y < miny) {
                    miny = point.y;
                }
            }
        }

        double offsetx = 0;
        double offsety = 0;
        if (minx < 0) {
            offsetx = Math.abs(minx) + 10;
        }
        if (miny < 0) {
            offsety = Math.abs(miny) + 10;
        }
        for (Shape shape : pd.getShapes()) {
            shape.setX(shape.getX() + offsetx);
            shape.setY(shape.getY() + offsety);
        }
        for (Line line : pd.getLines()) {
            for (HiPoint point : line.getPoints()) {
                point.x += offsetx;
                point.y += offsety;
            }
        }

    }

    public void recalcBoard() {
        double maxw = 0;
        double maxh = 0;
        for (Shape shape : shapes) {
            double cgw = shape.getRight();
            double cgh = shape.getBottom();
            if (cgw > maxw) {
                maxw = cgw;
            }
            if (cgh > maxh) {
                maxh = cgh;
            }
        }
        for (Line line : lines) {
            for (HiPoint point : line.getPoints()) {
                if (point.x > maxw) {
                    maxw = point.x;
                }
                if (point.y > maxh) {
                    maxh = point.y;
                }
            }
        }
        setBoardSize(maxw, maxh);


    }

    /**
     * 获取字符串的合理大小
     *
     * @param text
     * @return
     */
    public Size getStringSize(String text) {

        FontMetrics fontMetrics = this.currentGraphic.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int lineheight = ascent + descent;
        if (text != null && !"".equals(text.trim())) {
            int width = fontMetrics.stringWidth(text);
            return new Size(width, lineheight);
        }
        return new Size(0, lineheight);
    }

    public boolean isModified() {
        if (stateHistory.size() > 0) {
            return true;
        }
        return false;

    }


    /**
     * 触发图形选中监听器
     *
     * @param shape
     */
    private void fireShapeSelected(PropertyObject shape) {
        ShapeSelectedEvent shapeSelectedEvent = new ShapeSelectedEvent(shape);
        shapeSelectedListeners.forEach(listener -> listener.shapeSelected(shapeSelectedEvent));
        this.selectedObject = shape;
    }

    /**
     * 流程图保存回调
     */
    public void registerProcessSaveListener(ProcessSaveListener listener) {
        processSaveListener.add(listener);
    }

    /**
     * 图形选中监听
     *
     * @param listener
     */
    public void registerShapeSelectedListener(ShapeSelectedListener listener) {
        shapeSelectedListeners.add(listener);
    }

    /**
     * 图形选中监听
     *
     * @param listener
     */
    public void unRegisterShapeSelectedListener(ShapeSelectedListener listener) {
        shapeSelectedListeners.remove(listener);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        Property[] ps = new Property[]{
                new TextFieldProperty("id", this),
                new TextFieldProperty("name", this)
        };
        return ps;
    }

    /**
     * 生成sql脚本
     *
     * @return
     */
    public List<String> generateSql() {
        List<String> sqls = new ArrayList<>();
        if (this.id == null || this.id.trim().equals("")) {
            Messages.showErrorDialog("流程id未设置", "发生异常");
            return null;
        }
        //注意使用转义字符
        sqls.add("delete from ENGINE_TASK where ID_ like '" + this.id + "\\_%' escape '\\'");
        sqls.add(String.format("delete from ENGINE_FLOW where PROCESSID_='%s'", this.id));
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape instanceof Label) {
                continue;
            }
            if (shape instanceof SqlAggregator) {
                sqls.add(((SqlAggregator) shape).toSql(this.id));
            }
        }
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            if (line instanceof FlowSqlAggregator) {
                sqls.add(((FlowSqlAggregator) line).toSql(this.id));
            }
        }
        return sqls;
    }


    @Override
    public void focusGained(FocusEvent e) {
        //重置状态
        ctrlPressing = false;
    }

    @Override
    public void focusLost(FocusEvent e) {
        //重置
        ctrlPressing = false;
    }

    public Graphics2D getCurrentGraphic() {
        return currentGraphic;
    }

    public void setCurrentGraphic(Graphics2D currentGraphic) {
        this.currentGraphic = currentGraphic;
    }

    /**
     * 图形改变监听器
     * 一旦图形发生改变，将触发监听器
     *
     * @param changeListener
     */
    public void addChangeListener(ChartChangeListener changeListener) {
        chartChangeListenerList.add(changeListener);
    }

    private Map<Key<? extends Object>,Object> userdata = new HashMap<>();

    @SuppressWarnings("all")
    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return (T) userdata.get(key);
    }

    @SuppressWarnings("all")
    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {
        userdata.put(key, t);
    }

    public PropertyObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(PropertyObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {

        if (CURRENT_CHART_PANEL.is(dataId)) {
            return this;
        }
        return null;
    }

    public List<Shape> getAllElements() {
        return this.shapes;
//        LinkedList<Shape> shapes = this.shapes;
//        ret
//        return shapes.stream().map(shape -> {
//            Map<String, Object> result = new HashMap<String, Object>();
//            StringBuilder searchString = new StringBuilder();
//            searchString.append(shape.getName());
//            searchString.append(" ");
//            if (shape instanceof UserTask) {
//                searchString.append(((UserTask) shape).getExpression());
//            } else if (shape instanceof ServiceTask) {
//                searchString.append(((ServiceTask) shape).getExpression());
//            } else if (shape instanceof CallActivity) {
//                searchString.append(((CallActivity) shape).getCalledElement());
//            }
//            result.put("obj", shape);
//            result.put("text", searchString.toString());
//            return result;
//        }).collect(Collectors.toList());

    }

    public void selectShape(Shape o) {
        //1,将其他图形反选
        for (Shape shape : this.shapes) {
            if (shape != o) {
                shape.blur(null);
            }
        }
        o.focus(null);
        double x = o.getX();
        double y = o.getY();

        //要求当前对象转义到(width/2,height/2)
        /**
         *
         (x+originalPoint.x)*scale=width/2
         (x+originalPoint.x)=width/(2*scale)
         originalPoint.x=width/(2*scale)-x
         */
        originalPoint.x = this.getWidth() / (2 * scale) - x;
        originalPoint.y = this.getHeight() / (2 * scale) - y;

        this.fireShapeSelected(o);

        //

    }
}