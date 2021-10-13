package cn.boz.jb.plugin.floweditor.gui.control;

import cn.boz.jb.plugin.floweditor.gui.property.Property;

public interface PropertyObject {

    default Property[] getPropertyEditors() {
        Property[] ps = new Property[0];
        return ps;
    }


}
