package cn.boz.jb.plugin.floweditor.gui.property;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;

public class InputLongTextDialog extends DialogWrapper {

    private JTextArea textArea = null;

    private JBScrollPane jScrollPane=null;

    public InputLongTextDialog(@Nullable Project project, boolean canBeParent, String inputText) {
        super(project, canBeParent);
        this.textArea = new JTextArea();
        textArea.setText(inputText);
        textArea.setAutoscrolls(true);
//        textArea.setLineWrap(true);
        jScrollPane = new JBScrollPane(textArea);
        jScrollPane.setPreferredSize(new Dimension(800,600));
        init();
//        this.setTitle("");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.jScrollPane;
    }

    public String getInputText() {
        return this.textArea.getText();
    }
}
