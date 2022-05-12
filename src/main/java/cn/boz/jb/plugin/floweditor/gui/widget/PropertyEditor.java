package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.events.ShapeSelectedEvent;
import cn.boz.jb.plugin.floweditor.gui.listener.ShapeSelectedListener;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class PropertyEditor extends JPanel implements ShapeSelectedListener, PropertyEditorListener {
    private PropertyObject operatedObject;

    public PropertyObject getOperatedObject() {
        return operatedObject;
    }


    /**
     * 更新指向对象的
     *
     * @param operatedObject
     */
    public void setOperatedObject(PropertyObject operatedObject) {
        //修改操作的对象的时候，是需要重新进行维护的
        if (this.operatedObject == operatedObject) {
            return;
        }
        this.operatedObject = operatedObject;
        jTable.clearProperies();
        //怎样使得编辑器编辑的时候，会通知编辑容器呢
        Property[] propertyEditors = operatedObject.getPropertyEditors(this);
        for (Property p : propertyEditors) {
            jTable.addProperty(p);
        }
        jTable.resetRowHeight();
    }

    private ChartPanel chartPanel;

    private SpdTable jTable;

    private JScrollPane jScrollPane;

    public PropertyEditor(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        chartPanel.registerShapeSelectedListener(this);
        jTable = new SpdTable();
//        jScrollPane = new JBScrollPane(jTable);
        jScrollPane = new JScrollPane(jTable);
        this.setLayout(new BorderLayout());
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void shapeSelected(ShapeSelectedEvent shapeSelectedEvent) {
        PropertyObject selectedObject = shapeSelectedEvent.getSelectedObject();
        setOperatedObject(selectedObject);
    }

    @Override
    public void propertyEdited(Property property, Object operatedObj, Object oldValue, Object newValue) {

    }
}
