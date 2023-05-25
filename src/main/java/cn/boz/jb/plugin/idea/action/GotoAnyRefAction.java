package cn.boz.jb.plugin.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 弹出一个输入框，进入任意
 */
public class GotoAnyRefAction extends DumbAwareAction {


    JBPanel<JBPanel> jbPanelJBPanel;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        jbPanelJBPanel = new JBPanel<>();
        JTextArea jTextArea = new JTextArea(7, 30);
        jTextArea.setAutoscrolls(true);
        JBScrollPane jbScrollPane = new JBScrollPane(jTextArea);
        JButton go = new JButton("Go");
        jbPanelJBPanel.setLayout(new BorderLayout());
        jbPanelJBPanel.add(jbScrollPane, BorderLayout.CENTER);
        jbPanelJBPanel.add(go, BorderLayout.SOUTH);


        //弹出一个输入框
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(jbPanelJBPanel, null)
                .setProject(anActionEvent.getProject())
                .setMovable(true)
                .setRequestFocus(true)
                .setFocusable(true)
                .setTitle("Go To Any Ref")
                .setCancelOnOtherWindowOpen(true)
                .createPopup();

        InputEvent inputEvent = anActionEvent.getInputEvent();
        popup.showCenteredInCurrentWindow(anActionEvent.getProject());
//        popup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));
        go.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //搜索action或者流程?
                popup.dispose();
//                while (!popup.isDisposed()){
//                }

                String text = jTextArea.getText();
                GotoRefFileAction.tryToGotoAction(text, anActionEvent, false);
            }
        });
    }
}
