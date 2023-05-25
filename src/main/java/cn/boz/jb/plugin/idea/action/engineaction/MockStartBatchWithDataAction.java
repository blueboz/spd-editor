package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;
import cn.boz.jb.plugin.idea.configurable.SpdEditorNormState;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.utils.ExceptionProcessorUtils;
import cn.boz.jb.plugin.idea.utils.MockUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.StatusBarProgress;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import icons.SpdEditorIcons;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MockStartBatchWithDataAction extends DumbAwareAction {

    JBPanel<JBPanel> jbPanelJBPanel;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        jbPanelJBPanel = new JBPanel<>();
        JTextArea jTextArea = new JTextArea(16, 70);
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
        popup.showInFocusCenter();
        go.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //搜索action或者流程?
//                popup.dispose();
//                while (!popup.isDisposed()){
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException interruptedException) {
//                        interruptedException.printStackTrace();
//                    }
//                }
                String text = jTextArea.getText();
                doLogic(anActionEvent, text);

            }
        });


    }

    protected void doLogic(AnActionEvent anActionEvent, String text) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (requiredData instanceof JBTable) {
            process((JBTable) requiredData, anActionEvent, text);
        } else if (requiredData instanceof JBScrollPane) {
            JBScrollPane jbScrollPane = (JBScrollPane) requiredData;
            JViewport viewport = jbScrollPane.getViewport();
            Component view = viewport.getView();
            process((JBTable) view, anActionEvent, text);
        } else if (requiredData instanceof ChartPanel) {
            process((ChartPanel) requiredData, anActionEvent, text);
        }

    }

    private void process(ChartPanel chartPanel, AnActionEvent anActionEvent, String text) {
        String id = chartPanel.getId();
        if (StringUtils.isBlank(id)) {
            Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.WARNING);
            spdEditorNotification.setTitle("MockStart Process Fail");
            spdEditorNotification.setContent("process id not exist");
            spdEditorNotification.notify(anActionEvent.getProject());
        }
        httpPostRequest(id, anActionEvent, text);
    }

    private void process(JBTable jbTable, AnActionEvent anActionEvent, String text) {

        int selectedRow = jbTable.getSelectedRow();
        int modelidx = jbTable.convertRowIndexToModel(selectedRow);
        //如何拿到当前控件
        ListTableModel model = (ListTableModel) jbTable.getModel();
        Object item = model.getItem(modelidx);
        if (item instanceof XfundsBatch) {
            String enterName = ((XfundsBatch) item).getEnterName();

            httpPostRequest(enterName, anActionEvent, text);

        }

    }

    protected void httpPostRequest(String processId, AnActionEvent anActionEvent, String text) {
        String body="{ \"beanName\": \"processEngine\", \"argsClass\": [ \"java.lang.String\", \"java.util.Map\" ], \"methodName\": \"start\", \"requestBody\": [ \"" + processId + "\", " + text + "] };";
        String mockbase = SpdEditorNormState.getInstance(anActionEvent.getProject()).getState().mockbase;
        MockUtils.httpPostRequest(anActionEvent,body,mockbase);

    }
}
