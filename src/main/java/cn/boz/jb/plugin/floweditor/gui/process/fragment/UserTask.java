package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.property.impl.IntegerSpinnerProperty;
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

public class UserTask extends RectBridge implements SqlAggregator {

    protected String bussinesId;
    protected String bussinesKey;
    protected String bussinesDescrition;
    protected String rights;
    protected String expression;
    protected String eventListener;
    protected String validSecond;
    protected String openSecond;

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getUserTask());
    @Override
    public String indicatorFont() {
        return  IcoMoonUtils.getUser();

    }

    public String getBussinesId() {
        return bussinesId;
    }

    public void setBussinesId(String bussinesId) {
        this.bussinesId = bussinesId;
    }

    public String getBussinesKey() {
        if (bussinesKey == null) {
            return "";
        }
        return bussinesKey;
    }

    public void setBussinesKey(String bussinesKey) {
        this.bussinesKey = bussinesKey;
    }

    public String getBussinesDescrition() {
        return bussinesDescrition;
    }

    public void setBussinesDescrition(String bussinesDescrition) {
        this.bussinesDescrition = bussinesDescrition;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getExpression() {
        if (expression == null) {
            return "";
        }
        return expression;
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getEventListener() {
        return eventListener;
    }

    public void setEventListener(String eventListener) {
        this.eventListener = eventListener;
    }

    public String getValidSecond() {
        if (validSecond == null || validSecond.trim().equals("")) {
            return "0";
        }
        return validSecond;
    }

    public void setValidSecond(String validSecond) {
        this.validSecond = validSecond;
    }

    public String getOpenSecond() {
        if (openSecond == null || openSecond.trim().equals("")) {
            return "0";
        }
        return openSecond;
    }

    public void setOpenSecond(String openSecond) {
        this.openSecond = openSecond;
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
        UserTask usertask = new UserTask();

        usertask.setX(this.x);
        usertask.setY(this.y);
        usertask.setWidth(this.width);
        usertask.setHeight(this.height);

        usertask.setName(this.name);
        usertask.setExpression(this.expression);
        return usertask;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("userTask");
        element.addAttribute("id", TranslateUtils.translateToXmlString(this.getId()));
        element.addAttribute("name", TranslateUtils.translateToXmlString(this.getName()));
        element.addAttribute("bussinesId", TranslateUtils.translateToXmlString(this.getBussinesId()));
        element.addAttribute("bussinesKey", TranslateUtils.translateToXmlString(this.getBussinesKey()));
        element.addAttribute("bussinesDescrition", TranslateUtils.translateToXmlString(this.getBussinesDescrition()));
        element.addAttribute("rights", TranslateUtils.translateToXmlString(this.getRights()));
        element.addAttribute("expression", TranslateUtils.translateToXmlString(this.getExpression()));
        if (!StringUtils.isBlank(this.getEventListener())) {
            element.addAttribute("eventListener", TranslateUtils.translateToXmlString(this.getEventListener()));
        }
        element.addAttribute("validSecond", TranslateUtils.translateToXmlString(this.getValidSecond()));
        element.addAttribute("openSecond", TranslateUtils.translateToXmlString(this.getOpenSecond()));

        return element;
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("User Task");
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("User Task");
    }

    @Override
    public String getIdPrefix() {
        return "userTask";
    }

    private Property[] ps;

    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        if (ps == null) {
            synchronized (UserTask.class) {
                if (ps == null) {
                    ps = new Property[]{

                            new TextFieldProperty("name", this, propertyEditor),
                            new TextFieldProperty("bussinesKey", this, propertyEditor),
                            new TextFieldProperty("bussinesId", this, propertyEditor),
                            new TextFieldProperty("bussinesDescrition", this, propertyEditor),
                            new TextFieldProperty("rights", this, propertyEditor),
                            new TextAreaProperty("expression", this, propertyEditor),
                            new IntegerSpinnerProperty("validSecond", this, propertyEditor, -1, Integer.MAX_VALUE, 1),
                            new IntegerSpinnerProperty("openSecond", this, propertyEditor, -1, Integer.MAX_VALUE, 1),
                            new TextFieldProperty("eventListener", this, propertyEditor)

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
                        "VALUES ('%s', 'USER', '%s', '%s', null, '%s', '%s', '%s', %s, %s, %d, '%s', null)",
                processId + "_" + getId(), getName(), TranslateUtils.tranExpression(TranslateUtils.translateToSql(getExpression())), getBussinesKey(), getBussinesDescrition(), getRights(), Integer.parseInt(getValidSecond()),
                getStringOrNull(getEventListener()), Integer.parseInt(getOpenSecond()), getBussinesId());
        return sql;
    }

}
