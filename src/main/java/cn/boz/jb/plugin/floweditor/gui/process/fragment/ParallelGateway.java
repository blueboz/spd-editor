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

public class ParallelGateway extends PrismaticBridge implements Gateway, SqlAggregator {

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getParallelGateway());


    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);

        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont20());
        chartPanel.drawString(this.getX(), this.getY() + 3, this.getWidth(), this.getHeight(), IcoMoonUtils.getPlus());
        chartPanel.resetFont();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ParallelGateway parallelGateway = new ParallelGateway();
        parallelGateway.setX(this.x);
        parallelGateway.setY(this.y);
        parallelGateway.setWidth(this.width);
        parallelGateway.setHeight(this.height);

        parallelGateway.setName(this.name);
        return parallelGateway;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("parallelGateway");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", TranslateUtils.translateToXmlString(this.getName()));
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "parallelgateway";
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("Parallel Gateway");
    }


    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("Parallel Gateway");
    }

    @Override
    public String toSql(String processId) {
        return String.format("INSERT INTO ENGINE_TASK (ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, " +
                "BUSSINESDESC_, RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_) " +
                "VALUES ('%s', 'PARALLEL', '%s', null, null, null, null, null," +
                " 10000, null, 60, null, null)", processId + "_" + this.id, this.name);
    }
}
