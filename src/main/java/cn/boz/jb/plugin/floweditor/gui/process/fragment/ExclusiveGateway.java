package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.Gateway;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.PrismaticBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.TranslateUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Cursor;

public class ExclusiveGateway extends PrismaticBridge implements Gateway, SqlAggregator {

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getExclusiveGateway());

    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);

        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont20());
        chartPanel.drawString(this.getX(), this.getY() + 1, this.getWidth(), this.getHeight(), IcoMoonUtils.getClose());
        chartPanel.resetFont();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setX(this.x);
        exclusiveGateway.setY(this.y);
        exclusiveGateway.setWidth(this.width);
        exclusiveGateway.setHeight(this.height);

        exclusiveGateway.setName(this.name);
//        foreachGateway.setExpression(this.expression);
        return exclusiveGateway;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("exclusiveGateway");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", TranslateUtils.translateToXmlString(this.getName()));
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "exclusivegateway";
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("For Gateway");
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("For Gateway");
    }

    @Override
    public String toSql(String processId) {
        return String.format("INSERT INTO ENGINE_TASK (ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, BUSSINESDESC_," +
                "RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_)" +
                "VALUES ('%s', 'CASE', '%s', null, null, null, null, null, 10000, null, 60, null," +
                "null)", processId + "_" + id, name);
    }
}
