package cn.boz.jb.plugin.idea.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.SmoothProgressAdapter;
import com.intellij.openapi.progress.util.StatusBarProgress;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MockUtils {

    public static void httpPostRequest(AnActionEvent anActionEvent, String reqBody, String url) {

        ProgressManager progressManager = ProgressManager.getInstance();
        Task.Backgroundable waiting = new Task.Backgroundable(anActionEvent.getProject(), "waiting", true) {
            String msg;
            private Exception e;
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {

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
                        reqMsg.append("status:" + response.getStatusLine().toString());
                        String result = EntityUtils.toString(response.getEntity());
                        reqMsg.append("post result:" + result);
                        reqMsg.append("\n");
                    } else {
                        reqMsg.append("response is null");
                    }
//                    NotificationUtils.warnning("mockStartProcess", reqMsg.toString(), anActionEvent.getProject());
                } catch (Exception e) {
                    throw new RuntimeException(e);

//                    ExceptionProcessorUtils.exceptionProcessor(e, anActionEvent.getProject(), reqMsg.toString());
                } finally {
                    this.msg=reqMsg.toString();

                    if (null != httpClient) {
                        try {
                            httpClient.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                ExceptionProcessorUtils.exceptionProcessor(error,anActionEvent.getProject(),msg);
            }

            @Override
            public void onFinished() {
                NotificationUtils.warnning("mockStartProcess",msg,anActionEvent.getProject());
            }


        };
        StatusBarProgress statusBarProgress = new StatusBarProgress();
        statusBarProgress.setIndeterminate(true);

        progressManager.runProcessWithProgressAsynchronously(waiting,statusBarProgress);
    }
}
