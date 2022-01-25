package cn.boz.jb.plugin.floweditor.gui.property;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JTextArea;

public class InputLongTextDialog extends DialogWrapper {

    private JTextArea textArea = null;

    public InputLongTextDialog(@Nullable Project project, boolean canBeParent,String inputText) {
        super(project, canBeParent);
        this.textArea = new JTextArea();
        textArea.setText(inputText);
        this.setSize(800, 600);
        init();
//        this.setTitle("");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.textArea;
    }

    public String getInputText() {
        return this.textArea.getText();
    }
}
