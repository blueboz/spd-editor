package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.codeInsight.daemon.impl.DaemonProgressIndicator;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.progress.util.BackgroundTaskUtil;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Consumer;
import kotlin.internal.UProgressionUtilKt;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 编辑器右键，输入任意action的开头，然后跳转到任意Action
 */
public class GotoAnyRefExtAction extends DumbAwareAction {
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
        MouseEvent me = (MouseEvent) inputEvent;
        popup.showCenteredInCurrentWindow(anActionEvent.getProject());
//        popup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));
        go.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //搜索action或者流程?
                Disposer.dispose(popup);
//                while (!popup.isDisposed()){
//
//                }
                String text = jTextArea.getText();
                queryEngineAction(anActionEvent, text);

            }
        });
    }

    public void queryEngineAction(AnActionEvent anActionEvent, String name) {
        DBUtils dbUtils = DBUtils.getInstance();

        Ref<List<EngineAction>> engineActionRef = new Ref<>();


        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try {
                List<EngineAction> engineActions = dbUtils.queryEngineActionByActionScript(anActionEvent.getProject(), name);
                engineActionRef.set(engineActions);
            } catch (Exception e) {
                DBUtils.dbExceptionProcessor(e, anActionEvent.getProject());
            }
        }, "fetching...", true, anActionEvent.getProject());

        EventQueue.invokeLater(() -> {
            if (engineActionRef.isNull()) {
                return;
            }
            GotoRefFileAction.showListPopup(new ArrayList<>(engineActionRef.get()), anActionEvent.getProject(), new Consumer<Object>() {
                @Override
                public void consume(Object selectedValue) {
                    if (selectedValue instanceof EngineAction) {
                        EngineAction engineAction = (EngineAction) selectedValue;
                        GotoRefFileAction.tryToGotoAction(engineAction.getId(), anActionEvent, true);
                    } else {
                        EngineTaskDialog engineTaskDialog = new EngineTaskDialog((EngineTask) selectedValue);
                        JBScrollPane jbScrollPane = new JBScrollPane(engineTaskDialog);
                        JBPopup popup;
                        popup = JBPopupFactory.getInstance()
                                .createComponentPopupBuilder(jbScrollPane, null)
                                .setRequestFocus(true)
                                .setFocusable(true)
                                .setMovable(true)
                                .setTitle("EngineTask").setCancelOnOtherWindowOpen(true).setProject(anActionEvent.getProject()).createPopup();

                        popup.showCenteredInCurrentWindow(anActionEvent.getProject());
                    }
                }
            }, true, name, "");
        });

    }
}
