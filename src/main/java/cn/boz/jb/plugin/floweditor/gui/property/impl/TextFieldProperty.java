package cn.boz.jb.plugin.floweditor.gui.property.impl;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.property.Property;

import javax.swing.JComponent;
import javax.swing.JTextField;
import java.beans.PropertyChangeListener;

//通过
public class TextFieldProperty extends Property {

    public JTextField textField = new JTextField();


    public TextFieldProperty(String propertyName, Object operatedObj) {
        super(propertyName, operatedObj);
    }

    public TextFieldProperty(String propertyName, String displayPropertyName, Object operatedObj) {
        super(propertyName, displayPropertyName, operatedObj);
    }

    public TextFieldProperty(String propertyName, Object operatedObj, PropertyChangeListener listener) {
        super(propertyName, operatedObj,listener);
    }

    @Override
    public String getInputValue() {
        return textField.getText();
    }

    //有必要再封装一层吗
    @Override
    public JComponent getEditor() {
        //属性值如何回写？
        textField.setText((String) getValue());
        return textField;
    }
}
