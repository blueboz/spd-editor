package cn.boz.jb.plugin.floweditor.gui.property;

import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import io.netty.util.Constant;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.EditorKit;
import java.awt.*;

public class InputLongTextDialog extends DialogWrapper {

    private JBTextArea textArea = null;

    private JBScrollPane jScrollPane = null;

    private JBPanel<JBPanel> jbPanelJBPanel;

    public InputLongTextDialog(@Nullable Project project, boolean canBeParent, String inputText) {
        super(project, canBeParent);
        setTitle("大文本编辑");

        this.jbPanelJBPanel = new JBPanel<>();
        jbPanelJBPanel.setLayout(new BorderLayout());
        this.textArea = new JBTextArea();
        this.textArea.setLineWrap(false);
        this.textArea.setFocusable(true);


        textArea.setText(inputText);
        textArea.setAutoscrolls(true);
        jScrollPane = new JBScrollPane(textArea);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxRect = graphicsEnvironment.getMaximumWindowBounds();
        jScrollPane.setPreferredSize(new Dimension((int) (maxRect.getWidth() * .96), (int) (maxRect.getHeight() * .8)));
        init();
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_ENGINE_ACTION);
        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);
        JComponent gotoactionScript = spd_tb.getComponent();
        jbPanelJBPanel.add(jScrollPane, BorderLayout.CENTER);
        jbPanelJBPanel.add(gotoactionScript, BorderLayout.SOUTH);

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.jbPanelJBPanel;
    }

    public String getInputText() {
        return this.textArea.getText();
    }
    public void setInputText(String jsonString){
        this.textArea.setText(jsonString);
    }
}
