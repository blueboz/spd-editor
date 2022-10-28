package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.idea.configurable.SpdEditorNormState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.ProgressManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MockUtils {

    public static void httpPostRequest(AnActionEvent anActionEvent, String reqBody,String url) {
        ProgressManager progressManager = ProgressManager.getInstance();
        progressManager.executeProcessUnderProgress(()->{
            CloseableHttpClient httpClient = HttpClients.createDefault();
//            String mockbase = SpdEditorNormState.getInstance().getState().mockbase;
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().
                    setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                    .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/json");
            StringBuilder reqMsg = new StringBuilder();
            reqMsg.append("post url:" + httpPost);
            reqMsg.append("\n");
            reqMsg.append("post body:" + reqBody);
            reqMsg.append("\n");
            try {
                httpPost.setEntity(new StringEntity(reqBody));
                HttpResponse response = httpClient.execute(httpPost);
                if (response != null) {
                    reqMsg.append("status:"+response.getStatusLine().toString());
                    String result = EntityUtils.toString(response.getEntity());
                    reqMsg.append("post result:" + result);
                    reqMsg.append("\n");
                }else{
                    reqMsg.append("response is null");
                }
                NotificationUtils.warnning("mockStartProcess",reqMsg.toString(),anActionEvent.getProject());
            } catch (Exception e) {
                ExceptionProcessorUtils.exceptionProcessor(e,anActionEvent.getProject(),reqMsg.toString());
            } finally {
                if (null != httpClient) {
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProgressIndicatorProvider.getGlobalProgressIndicator());
    }
}
