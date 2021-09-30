package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.events.ShapeSelectedEvent;
import cn.boz.jb.plugin.floweditor.gui.listener.ShapeSelectedListener;
import cn.boz.jb.plugin.floweditor.gui.property.Property;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class PropertyEditor extends JPanel implements ShapeSelectedListener {
    private PropertyObject operatedObject;

    public PropertyObject getOperatedObject() {
        return operatedObject;
    }

    /**
     * 正向转换
     *
     * @param value
     * @return
     */
    public String trans(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("#LEY#", "\n");
    }

    /**
     * 反向转换
     *
     * @param value
     * @return
     */
    public String unTrans(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\n", "#LEY#");
    }

    /**
     * 更新指向对象的
     * @param operatedObject
     */
    public void setOperatedObject(PropertyObject operatedObject) {
        //修改操作的对象的时候，是需要重新进行维护的
        if (this.operatedObject == operatedObject) {
            return;
        }
        this.operatedObject = operatedObject;
        jTable.clearProperies();
        Property[] propertyEditors = operatedObject.getPropertyEditors();
        for (Property p : propertyEditors) {
            jTable.addProperty(p);
        }
        jTable.resetRowHeight();
    }

    private ChartPanel chartPanel;

    private MyTable jTable;

    private JScrollPane jScrollPane;

    public PropertyEditor(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        chartPanel.registerShapeSelectedListener(this);
        jTable = new MyTable();
        jScrollPane = new JScrollPane(jTable);
        this.setLayout(new BorderLayout());
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void shapeSelected(ShapeSelectedEvent shapeSelectedEvent) {
        PropertyObject selectedObject = shapeSelectedEvent.getSelectedObject();
        setOperatedObject(selectedObject);
    }
}
