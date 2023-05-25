package cn.boz.jb.plugin.idea.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.BackgroundTaskQueue;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
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

        Task.Backgroundable waiting = new Task.Backgroundable(anActionEvent.getProject(), "http posting...", true) {
            private String msg;
            private CloseableHttpClient httpClient = HttpClients.createDefault();

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                HttpPost httpPost = new HttpPost(url);
                progressIndicator.setText("init...");
                progressIndicator.setFraction(.1f);


                RequestConfig requestConfig = RequestConfig.custom().
                        setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                        .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
                httpPost.setConfig(requestConfig);
                httpPost.setHeader("Content-Type", "application/json");
                StringBuilder reqMsg = new StringBuilder();
                reqMsg.append("<h3>Req Url</h3>\n");
                reqMsg.append(httpPost);
                reqMsg.append("\n");
                reqMsg.append("<h3>Req Body</h3>\n");
                reqMsg.append(reqBody);
                reqMsg.append("\n");
                try {
                    httpPost.setEntity(new StringEntity(reqBody));
                    progressIndicator.setFraction(.55f);
                    progressIndicator.setText("requesting...");
                    HttpResponse response = httpClient.execute(httpPost);
                    progressIndicator.setFraction(1f);
                    progressIndicator.setText("done!...");

                    if (response != null) {
                        reqMsg.append(response.getStatusLine().toString());
                        String result = EntityUtils.toString(response.getEntity());
                        reqMsg.append("<h3>Res</h3>\n");
                        reqMsg.append(result);
                        reqMsg.append("\n");
                    } else {
                        reqMsg.append("<h3>Res</h3>\n");
                        reqMsg.append("NULL");
                        reqMsg.append("\n");
                    }
                } catch (Exception e) {
                    reqMsg.append("<h3>IDE Err</h3>\n");
                    reqMsg.append("<pre>");
                    reqMsg.append(ExceptionProcessorUtils.generateRecrusiveException(e));
                    reqMsg.append("</pre>");
                    reqMsg.append("\n");
                    throw new RuntimeException(e);

                } finally {
                    this.msg = reqMsg.toString();
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
                ExceptionProcessorUtils.exceptionProcessor(error, anActionEvent.getProject(), "");
            }

            @Override
            public void onCancel() {
                try {
                    httpClient.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                NotificationUtils.warnning("mockStartProcess", msg, anActionEvent.getProject());
            }


        };

        BackgroundTaskQueue waiting1 = new BackgroundTaskQueue(anActionEvent.getProject(), "waiting");
        waiting1.run(waiting);
    }
}
