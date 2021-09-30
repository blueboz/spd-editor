package cn.boz.jb.plugin.floweditor.gui.property.impl;

import cn.boz.jb.plugin.floweditor.gui.property.Property;
import com.intellij.openapi.ui.Messages;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

//通过
public class TextAreaProperty extends Property {

    private JTextArea textArea;

    private JScrollPane jScrollPane;

    public TextAreaProperty(String propertyName, String displayPropertyName, Object operatedObj) {
        super(propertyName, displayPropertyName, operatedObj);
    }

    public TextAreaProperty(String propertyName, Object operatedOb) {
        super(propertyName, operatedOb);
        jScrollPane = new JScrollPane();
        textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setLineWrap(true);
        jScrollPane.setViewportView(textArea);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    JTextField jTextField = new JTextField();
                    jTextField.setText(textArea.getText());
                    Messages.showTextAreaDialog(jTextField,"请录入",null);
                    textArea.setText(jTextField.getText());
                }

                super.mouseClicked(e);
            }
        });
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
