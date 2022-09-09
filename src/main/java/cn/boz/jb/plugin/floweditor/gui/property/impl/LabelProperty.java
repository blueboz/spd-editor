package cn.boz.jb.plugin.floweditor.gui.property.impl;

import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;

import javax.swing.*;
import java.beans.PropertyChangeListener;

//通过
public class LabelProperty extends Property {

    private JLabel jlabel = new JLabel();

    public LabelProperty(String propertyName, Object operatedObj) {
        super(propertyName, operatedObj);
    }

    public LabelProperty(String propertyName, String displayPropertyName, Object operatedObj) {
        super(propertyName, displayPropertyName, operatedObj);
    }

    public LabelProperty(String propertyName, Object operatedObj, PropertyChangeListener listener) {
        super(propertyName, operatedObj, listener);
    }

    public LabelProperty(String propertyName, Object operatedObj, PropertyChangeListener listener, PropertyEditorListener propertyEditorListener) {
        super(propertyName, operatedObj, listener, propertyEditorListener);
    }

    public LabelProperty(String propertyName, Object operatedObj, PropertyEditorListener propertyEditor) {
        super(propertyName, operatedObj, propertyEditor);

    }

    @Override
    public String getInputValue() {
        return jlabel.getText();
    }

    //有必要再封装一层吗
    @Override
    public JComponent getEditor() {
        //属性值如何回写？
        jlabel.setText((String) getValue());
        return jlabel;
    }
}
