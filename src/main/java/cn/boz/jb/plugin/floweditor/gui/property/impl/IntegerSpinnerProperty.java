package cn.boz.jb.plugin.floweditor.gui.property.impl;

import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;

import javax.swing.*;
import java.beans.PropertyChangeListener;

//通过
public class IntegerSpinnerProperty extends Property {

    private JSpinner jSpinner = new JSpinner();
    private SpinnerNumberModel spinnerNumberModel;
    private Integer minVal;
    private Integer maxVal;
    private Integer step;


    public IntegerSpinnerProperty(String propertyName, Object operatedObj, Integer minVal, Integer maxVal, Integer step) {
        super(propertyName, operatedObj);
        jSpinner.setModel(spinnerNumberModel);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.step = step;

    }

    public IntegerSpinnerProperty(String propertyName, String displayPropertyName, Object operatedObj, Integer minVal, Integer maxVal, Integer step) {
        super(propertyName, displayPropertyName, operatedObj);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.step = step;
    }

    public IntegerSpinnerProperty(String propertyName, Object operatedObj, PropertyChangeListener listener, Integer minVal, Integer maxVal, Integer step) {
        super(propertyName, operatedObj, listener);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.step = step;
    }

    public IntegerSpinnerProperty(String propertyName, Object operatedObj, PropertyChangeListener listener, PropertyEditorListener propertyEditorListener, Integer minVal, Integer maxVal, Integer step) {
        super(propertyName, operatedObj, listener, propertyEditorListener);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.step = step;
    }

    public IntegerSpinnerProperty(String propertyName, Object operatedObj, PropertyEditorListener propertyEditor, Integer minVal, Integer maxVal, Integer step) {
        super(propertyName, operatedObj, propertyEditor);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.step = step;

    }

    @Override
    public String getInputValue() {
        return String.format("%s", jSpinner.getValue());
    }

    //有必要再封装一层吗
    @Override
    public JComponent getEditor() {
        //属性值如何回写？
        //
        jSpinner.setModel(new SpinnerNumberModel(Integer.parseInt((String) getValue()), (int) minVal, (int) maxVal, (int) step));

        return jSpinner;
    }
}
