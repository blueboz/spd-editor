package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Color;

public class UserTask extends RectBridge {

    protected String bussinesId;
    protected String bussinesKey;
    protected String bussinesDescrition;
    protected String rights;
    protected String expression;
    protected String eventListener;
    protected String validSecond;
    protected String openSecond;


    public String getBussinesId() {
        return bussinesId;
    }

    public void setBussinesId(String bussinesId) {
        this.bussinesId = bussinesId;
    }

    public String getBussinesKey() {
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
        return validSecond;
    }

    public void setValidSecond(String validSecond) {
        this.validSecond = validSecond;
    }

    public String getOpenSecond() {
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
        chartPanel.drawString(this.getX() + 1, this.getY() + 1, IcoMoonUtils.getUser());

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
        usertask.setActiveBackgroundColor(this.activeBackgroundColor);
        usertask.setActiveForegroundColor(this.activeForegroundColor);
        usertask.setBackgroundColor(this.backgroundColor);
        usertask.setForegroundColor(this.foregroundColor);
        usertask.setName(this.name);
        usertask.setExpression(this.expression);
        return usertask;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("userTask");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        element.addAttribute("expression", this.getExpression());
        element.addAttribute("bussinesDescrition", this.getBussinesDescrition());
        element.addAttribute("rights", this.getRights());
        element.addAttribute("validSecond", this.getValidSecond());
        element.addAttribute("openSecond", this.getOpenSecond());
        element.addAttribute("eventListener", this.getEventListener());
        element.addAttribute("bussinesId", this.getBussinesId());
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
}
