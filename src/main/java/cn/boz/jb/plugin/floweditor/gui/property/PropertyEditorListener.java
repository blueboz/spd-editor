package cn.boz.jb.plugin.floweditor.gui.property;

public interface PropertyEditorListener {

    void propertyEdited(Property property, Object operatedObj, Object oldValue, Object newValue);
}
