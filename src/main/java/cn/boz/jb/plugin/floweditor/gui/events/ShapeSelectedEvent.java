package cn.boz.jb.plugin.floweditor.gui.events;

public class ShapeSelectedEvent {

    private Object selectedObject;

    public ShapeSelectedEvent(Object selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Object getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Object selectedObject) {
        this.selectedObject = selectedObject;
    }
}
