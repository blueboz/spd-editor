package cn.boz.jb.plugin.floweditor.gui.events;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;

public class ShapeSelectedEvent {

    private PropertyObject selectedObject;

    public ShapeSelectedEvent(PropertyObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    public PropertyObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(PropertyObject selectedObject) {
        this.selectedObject = selectedObject;
    }
}
