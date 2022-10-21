package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;

public class MockStartBatchAction extends DumbAwareAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);

        Component data = anActionEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        InputEvent inputEvent = anActionEvent.getInputEvent();
        Component component = inputEvent.getComponent();
        ActionPopupMenu popupMenu = (ActionPopupMenu) SwingUtilities.getAncestorOfClass(ActionPopupMenu.class, component);

        if (requiredData instanceof JBTable) {
            process((JBTable) requiredData,anActionEvent);
        } else if (requiredData instanceof JBScrollPane) {
            JBScrollPane jbScrollPane = (JBScrollPane) requiredData;
            JViewport viewport = jbScrollPane.getViewport();
            Component view = viewport.getView();
            process((JBTable) view,anActionEvent);
        }

    }

    private void process(JBTable jbTable,AnActionEvent anActionEvent) {

        int selectedRow = jbTable.getSelectedRow();
        int modelidx = jbTable.convertRowIndexToModel(selectedRow);
        //如何拿到当前控件
        ListTableModel model = (ListTableModel) jbTable.getModel();
        Object item = model.getItem(modelidx);
        if(item instanceof XfundsBatch){
            String enterName = ((XfundsBatch) item).getEnterName();

            httpPostRequest(enterName,anActionEvent);

        }

    }

    private void httpPostRequest(String enterName,AnActionEvent anActionEvent) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonObject = null;
        HttpPost httpPost = new HttpPost("http://localhost:10923/");
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        try {
            httpPost.setEntity(new StringEntity("{ \"beanName\": \"processEngine\", \"argsClass\": [ \"java.lang.String\", \"java.util.Map\" ], \"methodName\": \"start\", \"requestBody\": [ \""+enterName+"\", { } ] }", ContentType.create("application/json", "utf-8")));
            System.out.println("request parameters" + EntityUtils.toString(httpPost.getEntity()));
            System.out.println("httpPost:" + httpPost);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println("result:" + result);
                Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.WARNING);
                spdEditorNotification.setTitle("http request");
                spdEditorNotification.setContent(result);
                spdEditorNotification.notify(anActionEvent.getProject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
