package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextAreaProperty;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.TranslateUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Cursor;

public class ServiceTask extends RectBridge implements SqlAggregator {

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getServiceTask());
    @Override
    public String indicatorFont() {
        return  IcoMoonUtils.getGear();

    }
    private String expression;
    private String listener;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);
        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont16());
        chartPanel.drawString(this.getX() + 1, this.getY() + 1, indicatorFont());
        chartPanel.resetFont();
        chartPanel.drawString(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getName());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ServiceTask serviceTask = new ServiceTask();

        serviceTask.setX(this.x);
        serviceTask.setY(this.y);
        serviceTask.setWidth(this.width);
        serviceTask.setHeight(this.height);

        serviceTask.setName(this.name);
        serviceTask.setExpression(this.expression);
        return serviceTask;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("serviceTask");
        element.addAttribute("id", TranslateUtils.translateToXmlString(this.getId()));
        element.addAttribute("name", TranslateUtils.translateToXmlString(this.getName()));
        element.addAttribute("expression", TranslateUtils.translateToXmlString(this.getExpression()));
        //如果是空的那么就先不写入
        if (!StringUtils.isBlank(this.getListener())) {
            element.addAttribute("listener", TranslateUtils.translateToXmlString(this.getListener()));
        }
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "servicetask";
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("Service Task");

    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("Service Task");
    }

    private Property[] ps;

    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        if (ps == null) {
            synchronized (ServiceTask.class) {
                if (ps == null) {
                    ps = new Property[]{
                            new TextFieldProperty("name", this, propertyEditor),
                            new TextAreaProperty("expression", this, propertyEditor),
                            new TextFieldProperty("listener", this, propertyEditor),
                    };
                }
            }
        }
        return ps;
    }

    @Override
    public String toSql(String processId) {
        String sql = String.format("INSERT INTO ENGINE_TASK (ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, BUSSINESDESC_," +
                        "RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_)" +
                        "VALUES ('%s', 'SERVICE', '%s', '%s', null, null, null, null, 10000, null, 60, null, %s)",
                processId + "_" + getId(), getName(), TranslateUtils.tranExpression(TranslateUtils.translateToSql(getExpression())), getStringOrNull(getListener()));
        return sql;
    }

}

