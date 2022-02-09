package cn.boz.jb.plugin.floweditor.gui.property;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class InputLongTextDialog extends DialogWrapper {

    private JTextArea textArea = null;

    private JBScrollPane jScrollPane = null;

    private JBPanel<JBPanel> jbPanelJBPanel;

    public InputLongTextDialog(@Nullable Project project, boolean canBeParent, String inputText) {
        super(project, canBeParent);

        this.jbPanelJBPanel = new JBPanel<>();
        jbPanelJBPanel.setLayout(new BorderLayout());
        this.textArea = new JTextArea();


        textArea.setText(inputText);
        textArea.setAutoscrolls(true);
        jScrollPane = new JBScrollPane(textArea);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxRect = graphicsEnvironment.getMaximumWindowBounds();
        jScrollPane.setPreferredSize(new Dimension((int) (maxRect.getWidth() * .96), (int) (maxRect.getHeight() * .8)));
        init();
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction("spd.engineaction.dlg.group");
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
}
