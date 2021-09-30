package cn.boz.jb.plugin.idea.property.impl;

import cn.boz.jb.plugin.floweditor.gui.property.Property;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JTextArea;

//通过
public class MyTextAreaProperty extends Property {

    private JTextArea textArea;

    private JBScrollPane jScrollPane;

    public MyTextAreaProperty(String propertyName, String displayPropertyName, Object operatedObj) {
        super(propertyName, displayPropertyName, operatedObj);
    }

    public MyTextAreaProperty(String propertyName, Object operatedOb) {
        super(propertyName, operatedOb);
        jScrollPane = new JBScrollPane();
        textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setLineWrap(true);
        jScrollPane.setViewportView(textArea);
    }

    @Override
    public String getInputValue() {
        return textArea.getText();
    }

    //有必要再封装一层吗
    @Override
    public JComponent getEditor() {
        //属性值如何回写？
        textArea.setText((String) getValue());
        return jScrollPane;
    }

    @Override
    public Integer getRowHeight() {
        return 72;
    }


}
