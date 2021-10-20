package cn.boz.jb.plugin.floweditor.gui.property.impl;

import cn.boz.jb.plugin.floweditor.gui.property.Property;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//通过
public class TextAreaProperty extends Property {

    private JTextArea textArea;

    private JScrollPane jScrollPane;

    public TextAreaProperty(String propertyName, String displayPropertyName, Object operatedObj) {

        super(propertyName, displayPropertyName, operatedObj);
    }

    public TextAreaProperty(String propertyName, Object operatedOb) {
        super(propertyName, operatedOb);
        jScrollPane = new JBScrollPane();
        textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setLineWrap(true);
        jScrollPane.setViewportView(textArea);
        MouseAdapter l = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    String inputString = Messages.showMultilineInputDialog(null, null, "请录入", textArea.getText(), null, null);
                    if(inputString!=null){
                        textArea.setText(inputString);
                    }
                }

                super.mouseClicked(e);
            }
        };
        textArea.addMouseListener(l);
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
