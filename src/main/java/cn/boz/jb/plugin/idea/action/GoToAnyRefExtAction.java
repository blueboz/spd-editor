package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoToAnyRefExtAction extends DumbAwareAction {
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
        popup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));
        go.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //搜索action或者流程?
                popup.dispose();
                String text = jTextArea.getText();
                queryEngineAction(anActionEvent, text);

            }
        });
    }

    public void queryEngineAction(AnActionEvent anActionEvent, String name) {
        DBUtils dbUtils = DBUtils.getInstance();

        Ref<List<EngineAction>> engineActionRef = new Ref<>();
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try (Connection connection = dbUtils.getConnection()) {
                List<EngineAction> engineActions = dbUtils.queryEngineActionByActionScript(connection, name);
                engineActionRef.set(engineActions);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }, "Loading Engine Action...", true, anActionEvent.getProject());

        if (engineActionRef.isNull()) {
            return;
        }
        GoToRefFile.showListPopup(new ArrayList<>(engineActionRef.get()), anActionEvent.getProject(), new Consumer<Object>() {
            @Override
            public void consume(Object o) {

            }
        }, true);
    }
}
