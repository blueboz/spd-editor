package cn.boz.jb.plugin.floweditor.gui.control;

import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;

public interface PropertyObject {

    default Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        Property[] ps = new Property[0];
        return ps;
    }


}
